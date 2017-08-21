package io.highload.scheme

/**
 *
 */
class Locations {
    var locations = emptyList<Location>()
}

class Location {
    var id: Int = 0
    var place: String = ""
    var country: String = ""
    var city: String = ""
    var distance: Int = 0
}