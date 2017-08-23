package io.highload.scheme

/**
 *
 */
class Visit2(visit: Visit, location: Location) {
    val mark = visit.mark
    val vistedAt = visit.visitedAt
    val place = location.place

    override fun toString() = "{\"mark\":$mark,\"visited_at\":$vistedAt,\"place\":\"$place\"}"
}