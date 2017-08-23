package io.highload.web

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import java.io.InputStream

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
}