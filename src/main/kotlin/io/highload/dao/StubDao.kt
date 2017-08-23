package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.*

/**
 *
 */
class StubDao : EntityDao() {
    private val mutex = Mutex()
    val users = TreeMap<Int, User>()
    val locations = TreeMap<Int, Location>()
    val visits = TreeMap<Int, Visit>()
    val visitsByUsers = TreeMap<UserVisitKey, Visit>()
    val visitsByLocation = TreeMap<LocationVisitKey, Visit>()

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
        visitsByUsers.put(UserVisitKey(visit.user, visit.visitedAt, visit.id), visit)
        visitsByLocation.put(LocationVisitKey(visit.location, visit.id), visit)
    }

    override suspend fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[id]?.also {
            it.modify(block())
        }
    }

    suspend override fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[id]?.also {
            it.modify(block())
        }
    }

    suspend override fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[id]?.also {
            visitsByUsers.remove(UserVisitKey(it.user, it.visitedAt, it.id))
            visitsByLocation.remove(LocationVisitKey(it.location, it.id))

            it.modify(block())
            visitsByUsers.put(UserVisitKey(it.user, it.visitedAt, it.id), it)
            visitsByLocation.put(LocationVisitKey(it.location, it.id), it)
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

    suspend override fun findOrderedVisitsByUserId(userId: Int, fromDate: Int?, toDate: Int?): Collection<Visit>? = mutex.withLock {
        if (userId !in users) {
            return@withLock null
        }
        if (fromDate != null && toDate != null && fromDate >= toDate) {
            return@withLock emptyList<Visit>()
        }
        return visitsByUsers.subMap(
                UserVisitKey(userId, fromDate ?: Int.MIN_VALUE, Int.MAX_VALUE), false,
                UserVisitKey(userId, toDate ?: Int.MAX_VALUE, Int.MIN_VALUE), false
        ).values
    }

    suspend override fun findVisitsByLocationId(locationId: Int): Collection<Visit>? = mutex.withLock {
        if (locationId !in locations) {
            return@withLock null
        }
        return visitsByLocation.subMap(
                LocationVisitKey(locationId, Int.MIN_VALUE), false,
                LocationVisitKey(locationId, Int.MAX_VALUE), false
        ).values
    }
}