package io.highload.scheme

/**
 *
 */
class Visit2(visit: Visit, location: Location) {
    val mark = visit.mark
    val vistedAt = visit.visitedAt
    val place = location.place

    override fun toString(): String = toByteChain(null).toString()
//    override fun toString() = "{\"mark\":$mark,\"visited_at\":$vistedAt,\"place\":\"$place\"}"

    fun toByteChain(next: ByteChain?): ByteChain = ByteChain(JSON_END)
            .link(toByteArr(vistedAt))
            .link(VISITED_AT)
            .link(toByteArr(mark))
            .link(MARK2)
            .link(place.toByteArray())
            .link(PLACE2)
            .link(JSON_START0)
}