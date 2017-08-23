package io.highload.dao

/**
 *
 */
class UserVisitKey(val user: Int, val visitedAt: Int, val id: Int) : Comparable<UserVisitKey> {
    override fun compareTo(other: UserVisitKey): Int {
        val cmp1 = user.compareTo(other.user)
        if (cmp1 == 0) {
            val cmp2 = visitedAt.compareTo(other.visitedAt)
            if (cmp2 == 0) {
                return id.compareTo(other.id)
            }
            return cmp2
        }
        return cmp1
    }
}

class LocationVisitKey(val location: Int, val id: Int) : Comparable<LocationVisitKey> {
    override fun compareTo(other: LocationVisitKey): Int {
        val cmp1 = location.compareTo(other.location)
        if (cmp1 == 0) {
            return id.compareTo(other.id)
        }
        return cmp1
    }
}