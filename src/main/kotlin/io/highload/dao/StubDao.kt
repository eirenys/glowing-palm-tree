package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import io.highload.scheme.Visit2
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 *
 */
class StubDao : EntityDao() {
    private val mutex = Mutex()
    val users = TreeMap<Int, User>()
    val locations = TreeMap<Int, Location>()
    val visits = TreeMap<Int, Visit>()

    override suspend fun insert(user: User): Unit = mutex.withLock {
        if (user.id in users) {
            error("already inserted")
        }
        user.checkEntity()
        users.put(user.id, user)
    }

    override suspend fun insert(location: Location): Unit = mutex.withLock {
        if (location.id in locations) {
            error("already inserted")
        }
        location.checkEntity()
        locations.put(location.id, location)
    }

    override suspend fun insert(visit: Visit): Unit = mutex.withLock {
        if (visit.id in visits) {
            error("already inserted")
        }
        visit.checkEntity()
        visits.put(visit.id, visit)
    }

    override suspend fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[id]?.also {
            users.remove(it.id)
            val entity = block()
            it.modify(entity)
            users.put(it.id, it)
        }
    }

    suspend override fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[id]?.also {
            locations.remove(it.id)
            val entity = block()
            it.modify(entity)
            locations.put(it.id, it)
        }
    }

    suspend override fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[id]?.also {
            visits.remove(it.id)
            val entity = block()
            it.modify(entity)
            visits.put(it.id, it)
        }
    }

    override suspend fun findUser(id: Int): User? = mutex.withLock {
        return users[id]
    }

    override suspend fun findLocation(id: Int): Location? = mutex.withLock {
        return locations[id]
    }

    override suspend fun findVisit(id: Int): Visit? = mutex.withLock {
        return visits[id]
    }

    override suspend fun findVisits(userId: Int, country: String?, fromDate: Long?, toDate: Long?, toDistance: Int?)
            : List<Visit2>? = mutex.withLock {
        if (users[userId] == null) {
            return@withLock null
        }
        visits.values.map {
            if (it.user == userId && (fromDate == null || it.visitedAt > fromDate) && (toDate == null || it.visitedAt < toDate)) {
                val loc = if (country != null || toDistance != null) {
                    locations[it.location]
                } else {
                    null
                }
                if ((country == null || loc?.country == country) &&
                        (toDistance == null || (loc?.distance ?: Int.MAX_VALUE) < toDistance)) {
                    Visit2(it, loc ?: locations[it.location]!!)
                } else {
                    null
                }
            } else {
                null
            }
        }.filterNotNull().sortedBy { it.vistedAt }
    }

    suspend override fun avg(locationId: Int, fromDate: Long?, toDate: Long?, fromBirth: Long?, toBirth: Long?, gender: Char?): BigDecimal? = mutex.withLock {
        if (locations[locationId] == null) {
            return@withLock null
        }
        val marks = visits.values.map {
            if (it.location == locationId && (fromDate == null || it.visitedAt > fromDate) && (toDate == null || it.visitedAt < toDate)) {
                val us = if (fromBirth != null || toBirth != null || gender != null) {
                    users[it.user]
                } else {
                    null
                }
                if ((fromBirth == null || (us?.birthDate ?: Long.MIN_VALUE) > fromBirth) &&
                        (toBirth == null || (us?.birthDate ?: Long.MAX_VALUE) < toBirth) &&
                        (gender == null || us?.gender == gender)) {
                    it.mark
                } else {
                    null
                }
            } else {
                null
            }
        }.filterNotNull()

        if (marks.isEmpty()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(marks.average()).setScale(5, RoundingMode.HALF_UP)
        }
    }
}