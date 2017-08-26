package io.highload.dao

import io.highload.persistence.BTree
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
    private val CHUNK_SIZE = 1024
    private val mutex = ReentrantLock()
    val users = BTree<User>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) }, CHUNK_SIZE)
    val locations = BTree<Location>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) }, CHUNK_SIZE)
    val visits = BTree<Visit>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) }, CHUNK_SIZE)
    val visitsByUsers = BTree<Visit>(UserVisitComparator(), CHUNK_SIZE)
    val visitsByLocation = BTree<Visit>(LocationVisitComparator(), CHUNK_SIZE)

    fun insert(user: User): Unit = mutex.withLock {
        if (users[user] != null) {
            error("already inserted")
        }
        user.checkEntity()
        users.put(user)
    }

    fun insert(location: Location): Unit = mutex.withLock {
        if (locations[location] != null) {
            error("already inserted")
        }
        location.checkEntity()
        locations.put(location)
    }

    fun insert(visit: Visit): Unit = mutex.withLock {
        if (visits[visit] != null) {
            error("already inserted")
        }
        visit.checkEntity()
        visits.put(visit)
        visitsByUsers.put(visit)
        visitsByLocation.put(visit)
    }

    fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[User(id)]?.also {
            it.modify(block())
            users.put(it)
        }
    }

    fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[Location(id)]?.also {
            it.modify(block())
            locations.put(it)
        }
    }

    fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[Visit(id)]?.also {
            val new = Visit(it.id)
            new[1] = it.location
            new[2] = it.user
            new[3] = it.visitedAt
            new[4] = it.mark

            new.modify(block())

//            visitsByUsers.remove(old)
//            visitsByLocation.remove(old) todo
            visits.put(new)
            visitsByUsers.put(new)
            visitsByLocation.put(new)
        }
    }

    fun findUser(id: Int): User? {
        return users[User(id)]
    }

    fun findLocation(id: Int): Location? {
        return locations[Location(id)]
    }

    fun findVisit(id: Int): Visit? {
        return visits[Visit(id)]
    }

    fun findOrderedVisitsByUserId(userId: Int, fromDate: Int?, toDate: Int?): Collection<Visit>? {
        if (users[User(userId)] == null) {
            return null
        }
        if (fromDate != null && toDate != null && fromDate >= toDate) {
            return emptyList()
        }
        TODO()
//        return visitsByUsers.subMap(
//                UserVisitKey(userId, fromDate ?: Int.MIN_VALUE, Int.MAX_VALUE), false,
//                UserVisitKey(userId, toDate ?: Int.MAX_VALUE, Int.MIN_VALUE), false
//        ).values
    }

    fun findVisitsByLocationId(locationId: Int): Collection<Visit>?  {
        if (locations[Location(locationId)] == null) {
            return null
        }
        TODO()
//        return visitsByLocation.subMap(
//                LocationVisitKey(locationId, Int.MIN_VALUE), false,
//                LocationVisitKey(locationId, Int.MAX_VALUE), false
//        ).values
    }
}