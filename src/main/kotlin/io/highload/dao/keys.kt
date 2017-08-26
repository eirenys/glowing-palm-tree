package io.highload.dao

import io.highload.scheme.Visit

/**
 *
 */
class UserVisitComparator() : Comparator<Visit> {
    override fun compare(o1: Visit, other: Visit): Int {
        val cmp1 = o1.user.compareTo(other.user)
        if (cmp1 == 0) {
            val cmp2 = o1.visitedAt.compareTo(other.visitedAt)
            if (cmp2 == 0) {
                return o1.id.compareTo(other.id)
            }
            return cmp2
        }
        return cmp1
    }
}

class LocationVisitComparator() : Comparator<Visit> {
    override fun compare(o1: Visit, other: Visit): Int {
        val cmp1 = o1.location.compareTo(other.location)
        if (cmp1 == 0) {
            return o1.id.compareTo(other.id)
        }
        return cmp1
    }
}