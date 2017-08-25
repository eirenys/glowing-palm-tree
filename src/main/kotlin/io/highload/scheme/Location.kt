package io.highload.scheme

/**
 *
 */
class Location : Entity(5) {
    val id: Int get() = values[0] as Int
    val place: String get() = values[1] as String
    val country: String get() = values[2] as String
    val city: String get() = values[3] as String
    val distance: Int get() = values[4] as Int

    override fun toByteChain(next: ByteChain?): ByteChain = ByteChain(JSON_END, next)
            .link(toByteArr(distance))
            .link(DISTANCE)
            .link(city.toByteArray())
            .link(CITY)
            .link(country.toByteArray())
            .link(COUNTRY)
            .link(place.toByteArray())
            .link(PLACE)
            .link(toByteArr(id))
            .link(JSON_START)
}