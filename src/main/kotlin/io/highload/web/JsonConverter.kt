package io.highload.web

import io.highload.scheme.*
import java.io.InputStream
import java.io.OutputStream

/**
 *
 */
abstract class JsonConverter {
    abstract fun parseUsers(inp: InputStream): Sequence<User>
    abstract fun parseUser(inp: InputStream): User

    abstract fun parseLocations(inp: InputStream): Sequence<Location>
    abstract fun parseLocation(inp: InputStream): Location

    abstract fun parseVisits(inp: InputStream): Sequence<Visit>
    abstract fun parseVisit(inp: InputStream): Visit

    abstract fun formatUser(out: OutputStream, obj: User)
    abstract fun formatLocation(out: OutputStream, obj: Location)
    abstract fun formatVisit(out: OutputStream, obj: Visit)
    abstract fun formatVisits(out: OutputStream, obj: Visits2)
    abstract fun formatAvg(out: OutputStream, obj: Avg)
}