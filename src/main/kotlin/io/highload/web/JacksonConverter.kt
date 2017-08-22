package io.highload.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.scheme.*
import java.io.InputStream
import java.io.OutputStream

/**
 *
 */
class JacksonConverter(val mapper: ObjectMapper) : JsonConverter() {
    override fun parseUsers(out: InputStream): Users {
        return mapper.readValue(out, Users::class.java)
    }

    override fun parseUser(out: InputStream): User {
        return mapper.readValue(out, User::class.java)
    }

    override fun parseLocations(out: InputStream): Locations {
        return mapper.readValue(out, Locations::class.java)
    }

    override fun parseLocation(out: InputStream): Location {
        return mapper.readValue(out, Location::class.java)
    }

    override fun parseVisits(out: InputStream): Visits {
        return mapper.readValue(out, Visits::class.java)
    }

    override fun parseVisit(out: InputStream): Visit {
        return mapper.readValue(out, Visit::class.java)
    }

    override fun formatUser(out: OutputStream, obj: User) {
        mapper.writeValue(out, obj)
    }

    override fun formatLocation(out: OutputStream, obj: Location) {
        mapper.writeValue(out, obj)
    }

    override fun formatVisit(out: OutputStream, obj: Visit) {
        mapper.writeValue(out, obj)
    }

    override fun formatVisits(out: OutputStream, obj: Visits2) {
        mapper.writeValue(out, obj)
    }

    override fun formatAvg(out: OutputStream, obj: Avg) {
        mapper.writeValue(out, obj)
    }
}