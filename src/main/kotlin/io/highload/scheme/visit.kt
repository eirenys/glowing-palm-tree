package io.highload.scheme

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 */
class Visits {
    var visits = emptyList<Visit>()
}

class Visit {
    private var fields = 0

    var id: Int = 0
        set(value) {
            fields = fields or 1
            field = value
        }

    var location: Int = 0
        set(value) {
            fields = fields or 2
            field = value
        }

    var user: Int = 0
        set(value) {
            fields = fields or 4
            field = value
        }

    @JsonProperty("visited_at")
    var visitedAt: Long = 0
        set(value) {
            fields = fields or 8
            field = value
        }

    var mark: Int = 0
        set(value) {
            fields = fields or 16
            field = value
        }

    fun checkEntity() {
        check(fields == ALL)
    }

    fun modify(other: Visit) {
        other.ifHaveValue(1) { id = other.id}
        other.ifHaveValue(2) { location = other.location}
        other.ifHaveValue(4) { user = other.user}
        other.ifHaveValue(8) { visitedAt = other.visitedAt}
        other.ifHaveValue(16) { mark = other.mark}
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

class Visits2(val visits: List<Visit2>)

class Visit2(visit: Visit, location: Location) {
    val mark = visit.mark

    @JsonProperty("visited_at")
    val vistedAt = visit.visitedAt

    val place = location.place
}