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
}