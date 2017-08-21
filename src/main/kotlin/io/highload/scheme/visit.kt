package io.highload.scheme

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 */
class Visits {
    var visits = emptyList<Visit>()
}

class Visit {
    var id: Int = 0
    var location: Int = 0
    var user: Int = 0
    @JsonProperty("visited_at") var visitedAt: Long = 0
    var mark: Int = 0
}