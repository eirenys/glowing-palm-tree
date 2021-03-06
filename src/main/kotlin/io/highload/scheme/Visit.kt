package io.highload.scheme

/**
 *
 */
class Visit(id: Int = -1) : Entity(5) {
    init {
        if (id != -1) {
            values[0] = id
        }
    }

    val id: Int get() = values[0] as Int
    val location: Int get() = values[1] as Int
    val user: Int get() = values[2] as Int
    val visitedAt: Int get() = values[3] as Int
    val mark: Int get() = values[4] as Int

    override fun toString() = "{\"id\":$id,\"location\":$location,\"user\":$user,\"visited_at\":$visitedAt,\"mark\":$mark}"
}