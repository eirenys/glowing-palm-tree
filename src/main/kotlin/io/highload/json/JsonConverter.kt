package io.highload.json

import io.highload.scheme.*
import java.io.InputStream
import java.io.OutputStream

/**
 *
 */
abstract class JsonConverter {
    abstract fun parseUsers(out: InputStream): Users
    abstract fun parseUser(out: InputStream): User

    abstract fun parseLocations(out: InputStream): Locations
    abstract fun parseLocation(out: InputStream): Location

    abstract fun parseVisits(out: InputStream): Visits
    abstract fun parseVisit(out: InputStream): Visit

    abstract fun formatUser(out: OutputStream, obj: User)
    abstract fun formatLocation(out: OutputStream, obj: Location)
    abstract fun formatVisit(out: OutputStream, obj: Visit)
}