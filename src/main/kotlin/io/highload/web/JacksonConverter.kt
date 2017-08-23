package io.highload.web

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.core.JsonToken.START_OBJECT
import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.scheme.*
import java.io.InputStream
import java.io.OutputStream

/**
 *
 */
class JacksonConverter(val mapper: ObjectMapper) : JsonConverter() {
    val factory = JsonFactory()

    override fun parseUsers(inp: InputStream): Sequence<User> {
        val parser = factory.createParser(inp)
        return parser.on(START_OBJECT) {
            makeSequence {
                parser.on(START_OBJECT) {
                    parseUser(parser)
                }
            }
        } ?: emptySequence()
    }

    override fun parseUser(inp: InputStream): User = parseUser(factory.createParser(inp))

    fun parseUser(parser: JsonParser): User {
        val result = User()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "email" -> result[1] = parser.nextString()
                "first_name" -> result[2] = parser.nextString()
                "last_name" -> result[3] = parser.nextString()
                "gender" -> result[4] = parser.nextString().first()
                "birth_date" -> result[5] = parser.nextInt()
            }
        }

        return result
    }

    override fun parseLocations(inp: InputStream): Sequence<Location> {
        val parser = factory.createParser(inp)
        return parser.on(START_OBJECT) {
            makeSequence {
                parser.on(START_OBJECT) {
                    parseLocation(parser)
                }
            }
        } ?: emptySequence()
    }

    override fun parseLocation(inp: InputStream): Location = parseLocation(factory.createParser(inp))

    fun parseLocation(parser: JsonParser): Location {
        val result = Location()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "place" -> result[1] = parser.nextString()
                "country" -> result[2] = parser.nextString()
                "city" -> result[3] = parser.nextString()
                "distance" -> result[4] = parser.nextInt()
            }
        }

        return result
    }

    override fun parseVisits(inp: InputStream): Sequence<Visit> {
        val parser = factory.createParser(inp)
        return parser.on(START_OBJECT) {
            makeSequence {
                parser.on(START_OBJECT) {
                    parseVisit(parser)
                }
            }
        } ?: emptySequence()
    }

    override fun parseVisit(inp: InputStream): Visit = parseVisit(factory.createParser(inp))

    fun parseVisit(parser: JsonParser): Visit {
        val result = Visit()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "location" -> result[1] = parser.nextInt()
                "user" -> result[2] = parser.nextInt()
                "visited_at" -> result[3] = parser.nextInt()
                "mark" -> result[4] = parser.nextInt()
            }
        }

        return result
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

    private inline fun <T> JsonParser.on(token: JsonToken?, block: () -> T): T? {
        do {
            val actual = nextToken()
            if (actual == token) {
                return block()
            }
        } while (actual != null)
        return null
    }

    private inline fun JsonParser.until(token: JsonToken?, block: (JsonToken?) -> Unit) {
        do {
            val actual = nextToken()
            if (actual == token) {
                break
            } else {
                block(actual)
            }
        } while (actual != null)
    }

    private fun JsonParser.nextInt(): Int = if (nextToken() == JsonToken.VALUE_NUMBER_INT)
        intValue
    else
        error("invalid value")

    private fun JsonParser.nextString(): String = if (nextToken() == JsonToken.VALUE_STRING)
        text
    else
        error("invalid value")

    private fun <T> makeSequence(nextElement: () -> T?): Sequence<T> {
        return object : Sequence<T> {
            override fun iterator(): Iterator<T> {
                return object : Iterator<T> {
                    var element: T? = nextElement()

                    override fun hasNext(): Boolean = element != null

                    override fun next(): T {
                        val result = element!!
                        element = nextElement()
                        return result
                    }
                }
            }
        }
    }
}