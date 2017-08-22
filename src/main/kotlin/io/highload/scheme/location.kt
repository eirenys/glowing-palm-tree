package io.highload.scheme

/**
 *
 */
class Locations {
    var locations = emptyList<Location>()
}

class Location {
    private var fields = 0

    var id: Int = 0
        set(value) {
            fields = fields or 1
            field = value
        }

    var place: String = ""
        set(value) {
            fields = fields or 2
            field = value
        }

    var country: String = ""
        set(value) {
            fields = fields or 4
            field = value
        }

    var city: String = ""
        set(value) {
            fields = fields or 8
            field = value
        }

    var distance: Int = 0
        set(value) {
            fields = fields or 16
            field = value
        }

    fun checkEntity() {
        check(fields == ALL)
    }

    fun modify(other: Location) {
        other.ifHaveValue(1) { id = other.id}
        other.ifHaveValue(2) { place = other.place}
        other.ifHaveValue(4) { country = other.country}
        other.ifHaveValue(8) { city = other.city}
        other.ifHaveValue(16) { distance = other.distance}
    }

    private inline fun ifHaveValue(flag: Int, block: () -> Unit) {
        if ((fields and flag) == flag) {
            block()
        }
    }

    companion object {
        private const val ALL = 0x1F
    }
}