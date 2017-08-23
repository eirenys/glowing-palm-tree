package io.highload.web

import org.joda.time.DateTime
import java.net.URLDecoder

/**
 *
 */
class QueryParams(
        val country: String?,
        val fromDate: Int?,
        val toDate: Int?,
        val toDistance: Int?,
        val fromBirth: Int?,
        val toBirth: Int?,
        val gender: Char?) {
    companion object {
        fun parse(query: CharSequence?): QueryParams {
            var country: String? = null
            var fromDate: Int? = null
            var toDate: Int? = null
            var toDistance: Int? = null
            var fromBirth: Int? = null
            var toBirth: Int? = null
            var gender: Char? = null

            if (query != null) {
                query.split("&").forEach {
                    val pair = it.split("=")
                    when (pair[0]) {
                        "country" -> country = URLDecoder.decode(pair[1], "UTF-8")
                        "fromDate" -> fromDate = pair[1].toInt()
                        "toDate" -> toDate = pair[1].toInt()
                        "toDistance" -> toDistance = pair[1].toInt()
                        "fromAge" -> toBirth = (DateTime.now().minusYears(pair[1].toInt()).millis / 1000).toInt()
                        "toAge" -> fromBirth = (DateTime.now().minusYears(pair[1].toInt()).millis / 1000).toInt()
                        "gender" -> gender = pair[1].first()
                    }
                }
            }

            return QueryParams(country, fromDate, toDate, toDistance, fromBirth, toBirth, gender)
        }
    }
}