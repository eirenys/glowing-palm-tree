package io.highload.web

import io.highload.dao.StubDao
import io.highload.scheme.ByteChain
import io.highload.scheme.Visit
import io.highload.scheme.Visit2
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 *
 */
class MainHandler(val dao: StubDao, val converter: JsonConverter) {
    fun get(path: CharSequence, query: CharSequence?): ByteArray? {
        if (path.startsWith("/users/")) {
            if (path.endsWith("/visits")) {
                val id = path.substring("/users/".length, path.length - "/visits".length).toIntOrNull()
                        ?: return null
                val params = QueryParams.parse(query)
                val result = dao.findOrderedVisitsByUserId(id, params.fromDate, params.toDate)
                        ?: return null
                return toByteArray(convertVisits(result, params))
            } else {
                val id = path.substring("/users/".length).toIntOrNull()
                        ?: return null
                return dao.findUser(id)?.toByteArray()
            }
        } else if (path.startsWith("/locations/")) {
            if (path.endsWith("/avg")) {
                val id = path.substring("/locations/".length, path.length - "/avg".length).toIntOrNull()
                        ?: return null
                val params = QueryParams.parse(query)
                val vists = dao.findVisitsByLocationId(id)
                        ?: return null
                val result = avg(vists, params)
                return "{\"avg\":$result}".toByteArray()
            } else {
                val id = path.substring("/locations/".length).toIntOrNull()
                        ?: return null
                return dao.findLocation(id)?.toByteArray()
            }
        } else if (path.startsWith("/visits/")) {
            val id = path.substring("/visits/".length).toIntOrNull()
                    ?: return null
            return dao.findVisit(id)?.toByteArray()
        }
        return null
    }

    fun post(path: CharSequence, body: ByteArray): ByteArray? {
        if (body.isEmpty()) {
            error("empty body")
        }
        when {
            path.startsWith("/users/new") -> {
                val user = converter.parseUser(ByteArrayInputStream(body))
                dao.insert(user)
                return EMPTY_JSON
            }
            path.startsWith("/locations/new") -> {
                val location = converter.parseLocation(ByteArrayInputStream(body))
                dao.insert(location)
                return EMPTY_JSON
            }
            path.startsWith("/visits/new") -> {
                val visit = converter.parseVisit(ByteArrayInputStream(body))
                dao.insert(visit)
                return EMPTY_JSON
            }
            path.startsWith("/users/") -> {
                val id = path.substring("/users/".length).toInt()
                val result = dao.updateUser(id) {
                    converter.parseUser(ByteArrayInputStream(body))
                }
                return if (result != null) EMPTY_JSON else null
            }
            path.startsWith("/locations/") -> {
                val id = path.substring("/locations/".length).toInt()
                val result = dao.updateLocation(id) {
                    converter.parseLocation(ByteArrayInputStream(body))
                }
                return if (result != null) EMPTY_JSON else null
            }
            path.startsWith("/visits/") -> {
                val id = path.substring("/visits/".length).toInt()
                val result = dao.updateVisit(id) {
                    converter.parseVisit(ByteArrayInputStream(body))
                }
                return if (result != null) EMPTY_JSON else null
            }
            else -> return null
        }
    }

    fun convertVisits(visits: Collection<Visit>, params: QueryParams): List<Visit2> {
        return visits.mapNotNull {
            if ((params.fromDate == null || it.visitedAt > params.fromDate)
                    && (params.toDate == null || it.visitedAt < params.toDate)) {
                val location = dao.findLocation(it.location) ?: error("location not found")
                if ((params.country == null || location.country == params.country) &&
                        (params.toDistance == null || location.distance < params.toDistance)) {
                    return@mapNotNull Visit2(it, location)
                }
            }

            null
        }
    }

    fun avg(visits: Collection<Visit>, params: QueryParams): BigDecimal {
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
            BigDecimal(list.sum()).divide(BigDecimal(list.size), MathContext(20)).setScale(5, RoundingMode.HALF_UP)
        }
    }

    fun toByteArray(list: List<Visit2>): ByteArray {
        var result = ByteChain(VISITS_END, null)
        list.asReversed().forEachIndexed { index, visit ->
            if (index > 0) {
                result = result.link(DELIMITER)
            }
            result = visit.toByteChain(result)
        }
        return result.link(VISITS).toByteArray()
    }
}