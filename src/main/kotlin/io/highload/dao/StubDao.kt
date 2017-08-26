package io.highload.dao

import io.highload.persistence.BTree
import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import java.util.*

/**
 *
 */
class StubDao {
    val users = BTree<User>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) })
    val locations = BTree<Location>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) })
    val visits = BTree<Visit>(Comparator { o1, o2 -> o1.id.compareTo(o2.id) })
    val visitsByUsers = BTree(UserVisitComparator())
    val visitsByLocation = BTree(LocationVisitComparator())

    fun insert(user: User) {
        if (users.get(user) != null) {
            error("already inserted")
        }
        user.checkEntity()
        users.put(user)
    }

    fun insert(location: Location) {
        if (locations.get(location) != null) {
            error("already inserted")
        }
        location.checkEntity()
        locations.put(location)
    }

    fun insert(visit: Visit) {
        if (visits.get(visit) != null) {
            error("already inserted")
        }
        visit.checkEntity()
        visits.put(visit)
        visitsByUsers.put(visit)
        visitsByLocation.put(visit)
    }

    fun updateUser(id: Int, block: () -> User): User? {
        return users.get(User(id))?.also {
            it.modify(block())
            users.put(it)
        }
    }

    fun updateLocation(id: Int, block: () -> Location): Location? {
        return locations.get(Location(id))?.also {
            it.modify(block())
            locations.put(it)
        }
    }

    fun updateVisit(id: Int, block: () -> Visit): Visit? {
        return visits.get(Visit(id))?.also {
            val new = Visit(it.id)
            new[1] = it.location
            new[2] = it.user
            new[3] = it.visitedAt
            new[4] = it.mark

            new.modify(block())

            visits.remove(it)
            visitsByUsers.remove(it)
            visitsByLocation.remove(it)
            visits.put(new)
            visitsByUsers.put(new)
            visitsByLocation.put(new)
        }
    }

    fun findUser(id: Int): User? {
        return users.get(User(id))
    }

    fun findLocation(id: Int): Location? {
        return locations.get(Location(id))
    }

    fun findVisit(id: Int): Visit? {
        return visits.get(Visit(id))
    }

    fun findOrderedVisitsByUserId(userId: Int, fromDate: Int?, toDate: Int?): Collection<Visit>? {
        if (users.get(User(userId)) == null) {
            return null
        }
        if (fromDate != null && toDate != null && fromDate >= toDate) {
            return emptyList()
        }
        val left = Visit().also {
            it[0] = Int.MAX_VALUE
            it[1] = 0
            it[2] = userId
            it[3] = fromDate ?: Int.MIN_VALUE
            it[4] = 0
        }
        val right = Visit().also {
            it[0] = Int.MIN_VALUE
            it[1] = 0
            it[2] = userId
            it[3] = toDate ?: Int.MAX_VALUE
            it[4] = 0
        }
        return visitsByUsers.subMap(left, right)
    }

    fun findVisitsByLocationId(locationId: Int): Collection<Visit>?  {
        if (locations.get(Location(locationId)) == null) {
            return null
        }
        val left = Visit().also {
            it[0] = Int.MIN_VALUE
            it[1] = locationId
            it[2] = 0
            it[3] = 0
            it[4] = 0
        }
        val right = Visit().also {
            it[0] = Int.MAX_VALUE
            it[1] = locationId
            it[2] = 0
            it[3] = 0
            it[4] = 0
        }
        return visitsByLocation.subMap(left, right)
    }
}