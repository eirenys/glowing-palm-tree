package io.highload.scheme

/**
 *
 */
class Visit : Entity(5) {
    val id: Int get() = values[0] as Int
    val location: Int get() = values[1] as Int
    val user: Int get() = values[2] as Int
    val visitedAt: Int get() = values[3] as Int
    val mark: Int get() = values[4] as Int

    override fun toByteChain(next: ByteChain?): ByteChain = ByteChain(JSON_END)
            .link(toByteArr(mark))
            .link(MARK)
            .link(toByteArr(visitedAt))
            .link(VISITED_AT)
            .link(toByteArr(user))
            .link(USER)
            .link(toByteArr(location))
            .link(LOCATION)
            .link(toByteArr(id))
            .link(JSON_START)
}