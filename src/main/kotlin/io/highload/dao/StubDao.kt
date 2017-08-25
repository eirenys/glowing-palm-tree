package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 *
 */
class StubDao {
    private val mutex = ReentrantLock()
    val users = TreeMap<Int, User>()
    val locations = TreeMap<Int, Location>()
    val visits = TreeMap<Int, Visit>()
    val visitsByUsers = TreeMap<UserVisitKey, Visit>()
    val visitsByLocation = TreeMap<LocationVisitKey, Visit>()

    fun insert(user: User): Unit = mutex.withLock {
        if (user.id in users) {
            error("already inserted")
        }
        user.checkEntity()
        users.put(user.id, user)
    }

    fun insert(location: Location): Unit = mutex.withLock {
        if (location.id in locations) {
            error("already inserted")
        }
        location.checkEntity()
        locations.put(location.id, location)
    }

    fun insert(visit: Visit): Unit = mutex.withLock {
        if (visit.id in visits) {
            error("already inserted")
        }
        visit.checkEntity()
        visits.put(visit.id, visit)
        visitsByUsers.put(UserVisitKey(visit.user, visit.visitedAt, visit.id), visit)
        visitsByLocation.put(LocationVisitKey(visit.location, visit.id), visit)
    }

    fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[id]?.also {
            it.modify(block())

            users.remove(id)
            users.put(it.id, it)
        }
    }

    fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[id]?.also {
            it.modify(block())

            locations.remove(id)
            locations.put(it.id, it)
        }
    }

    fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[id]?.also {
            val oldKey1 = UserVisitKey(it.user, it.visitedAt, it.id)
            val oldKey2 = LocationVisitKey(it.location, it.id)

            it.modify(block())

            visits.remove(id)
            visitsByUsers.remove(oldKey1)
            visitsByLocation.remove(oldKey2)
            visits.put(it.id, it)
            visitsByUsers.put(UserVisitKey(it.user, it.visitedAt, it.id), it)
            visitsByLocation.put(LocationVisitKey(it.location, it.id), it)
        }
    }

    fun findUser(id: Int): User? {
        return users[id]
    }

    fun findLocation(id: Int): Location? {
        return locations[id]
    }

    fun findVisit(id: Int): Visit? {
        return visits[id]
    }

    fun findOrderedVisitsByUserId(userId: Int, fromDate: Int?, toDate: Int?): Collection<Visit>? {
        if (userId !in users) {
            return null
        }
        if (fromDate != null && toDate != null && fromDate >= toDate) {
            return emptyList<Visit>()
        }
        return visitsByUsers.subMap(
                UserVisitKey(userId, fromDate ?: Int.MIN_VALUE, Int.MAX_VALUE), false,
                UserVisitKey(userId, toDate ?: Int.MAX_VALUE, Int.MIN_VALUE), false
        ).values
    }

    fun findVisitsByLocationId(locationId: Int): Collection<Visit>?  {
        if (locationId !in locations) {
            return null
        }
        return visitsByLocation.subMap(
                LocationVisitKey(locationId, Int.MIN_VALUE), false,
                LocationVisitKey(locationId, Int.MAX_VALUE), false
        ).values
    }
}