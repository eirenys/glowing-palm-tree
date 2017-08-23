package io.highload.web

import io.highload.dao.EntityDao
import io.highload.scheme.Visit
import io.highload.scheme.Visit2
import io.highload.scheme.Visits2
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

/**
 *
 */
class MainHandler(val dao: EntityDao, val converter: JsonConverter) {
    suspend fun get(path: CharSequence, query: CharSequence?): String? {
        if (path.startsWith("/users/")) {
            if (path.endsWith("/visits")) {
                val id = path.substring("/users/".length, path.length - "/visits".length).toInt()
                val params = QueryParams.parse(query)
                val result = dao.findOrderedVisitsByUserId(id, params.fromDate, params.toDate)
                        ?: return null
                val visits2 = convertVisits(result, params)
                val out = ByteArrayOutputStream()
                converter.formatVisits(out, Visits2(visits2))
                return out.toString()
            } else {
                val id = path.substring("/users/".length).toInt()
                val result = dao.findUser(id)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatUser(out, result)
                return out.toString()
            }
        } else if (path.startsWith("/locations/")) {
            if (path.endsWith("/avg")) {
                val id = path.substring("/locations/".length, path.length - "/avg".length).toInt()
                val params = QueryParams.parse(query)
                val vists = dao.findVisitsByLocationId(id)
                        ?: return null
                val result = avg(vists, params)
                return "{\"avg\":$result}"
            } else {
                val id = path.substring("/locations/".length).toIntOrNull()
                        ?: error("invalid id")
                val result = dao.findLocation(id)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatLocation(out, result)
                return out.toString()
            }
        } else if (path.startsWith("/visits/")) {
            val id = path.substring("/visits/".length).toInt()
            val result = dao.findVisit(id)
                    ?: return null
            val out = ByteArrayOutputStream()
            converter.formatVisit(out, result)
            return out.toString()
        }
        return null
    }

    suspend fun post(path: CharSequence, body: ByteArray): String? {
        when {
            path.startsWith("/users/new") -> {
                val user = converter.parseUser(ByteArrayInputStream(body))
                dao.insert(user)
                return "{}"
            }
            path.startsWith("/locations/new") -> {
                val location = converter.parseLocation(ByteArrayInputStream(body))
                dao.insert(location)
                return "{}"
            }
            path.startsWith("/visits/new") -> {
                val visit = converter.parseVisit(ByteArrayInputStream(body))
                dao.insert(visit)
                return "{}"
            }
            path.startsWith("/users/") -> {
                val id = path.substring("/users/".length).toInt()
                val result = dao.updateUser(id) {
                    converter.parseUser(ByteArrayInputStream(body))
                }
                return if (result != null) "{}" else null
            }
            path.startsWith("/locations/") -> {
                val id = path.substring("/locations/".length).toInt()
                val result = dao.updateLocation(id) {
                    converter.parseLocation(ByteArrayInputStream(body))
                }
                return if (result != null) "{}" else null
            }
            path.startsWith("/visits/") -> {
                val id = path.substring("/visits/".length).toInt()
                val result = dao.updateVisit(id) {
                    converter.parseVisit(ByteArrayInputStream(body))
                }
                return if (result != null) "{}" else null
            }
            else -> return null
        }
    }

    suspend fun convertVisits(visits: Collection<Visit>, params: QueryParams): List<Visit2> {
        return visits.mapNotNull {
            if ((params.fromDate == null || it.visitedAt > params.fromDate)
                    && (params.toDate == null || it.visitedAt < params.toDate)) {
                val location = dao.findLocation(0)!!
                if ((params.country == null || location.country == params.country) &&
                        (params.toDistance == null || location.distance < params.toDistance)) {
                    return@mapNotNull Visit2(it, location)
                }
            }

            null
        }
    }

    suspend fun avg(visits: Collection<Visit>, params: QueryParams): BigDecimal {
        val list = visits.mapNotNull {
            if ((params.fromDate == null || it.visitedAt > params.fromDate)
                    && (params.toDate == null || it.visitedAt < params.toDate)) {
                if (params.fromBirth == null && params.toBirth == null && params.gender == null) {
                    return@mapNotNull it.mark
                }
                val user = dao.findUser(it.user)!!
                if ((params.fromBirth == null || user.birthDate > params.fromBirth)
                        && (params.toBirth == null || user.birthDate < params.toBirth)
                        && (params.gender == null || user.gender == params.gender)) {
                    return@mapNotNull it.mark
                }
            }

            null
        }

        return if (list.isEmpty()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(list.average()).setScale(5, RoundingMode.HALF_UP)
        }
    }
}