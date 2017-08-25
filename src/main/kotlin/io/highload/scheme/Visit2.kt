package io.highload.scheme

/**
 *
 */
class Visit2(visit: Visit, location: Location) {
    val mark = visit.mark
    val vistedAt = visit.visitedAt
    val place = location.place

    override fun toString() = "{\"place\":\"$place\",\"mark\":$mark,\"visited_at\":$vistedAt}"

    fun toByteChain(next: ByteChain?): ByteChain = ByteChain(toString().toByteArray(), next)
}