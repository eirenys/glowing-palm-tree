package io.highload.scheme

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

/**
 *
 */
class Avg(val avg: BigDecimal)

class Visits2(val visits: List<Visit2>)

class Visit2(visit: Visit, location: Location) {
    val mark = visit.mark

    @JsonProperty("visited_at")
    val vistedAt = visit.visitedAt

    val place = location.place
}