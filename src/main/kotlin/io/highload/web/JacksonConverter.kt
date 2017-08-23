package io.highload.web

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import java.io.InputStream

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

    private fun parseUser(parser: JsonParser): User {
        val result = User()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "email" -> result[1] = parser.nextString()
                "first_name" -> result[2] = parser.nextString()
                "last_name" -> result[3] = parser.nextString()
                "gender" -> result[4] = parser.nextString().first()
                "birth_date" -> result[5] = parser.nextInt()
                else -> unknownField(it)
            }
        }

        return result
    }

    private fun parseLocation(parser: JsonParser): Location {
        val result = Location()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "place" -> result[1] = parser.nextString()
                "country" -> result[2] = parser.nextString()
                "city" -> result[3] = parser.nextString()
                "distance" -> result[4] = parser.nextInt()
                else -> unknownField(it)
            }
        }

        return result
    }

    private fun parseVisit(parser: JsonParser): Visit {
        val result = Visit()
        parser.until(END_OBJECT) {
            when (parser.currentName) {
                "id" -> result[0] = parser.nextInt()
                "location" -> result[1] = parser.nextInt()
                "user" -> result[2] = parser.nextInt()
                "visited_at" -> result[3] = parser.nextInt()
                "mark" -> result[4] = parser.nextInt()
                else -> unknownField(it)
            }
        }

        return result
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

    private fun unknownField(token: JsonToken?) {
        if (token == FIELD_NAME) {
            error("unknown field")
        }
    }

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