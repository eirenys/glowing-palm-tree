package io.highload.web

import io.highload.PORT
import io.highload.dao.EntityDao
import io.highload.scheme.Avg
import io.highload.scheme.Visits2
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import kotlinx.coroutines.experimental.launch
import org.joda.time.DateTime
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.coroutines.experimental.CoroutineContext

/**
 *
 */
class VertxServer(val dao: EntityDao, val converter: JsonConverter, val context: CoroutineContext) {
    fun start(port: Int) {
        val vertx = Vertx.factory.vertx()
        val server = vertx.createHttpServer()

        server.requestHandler { request ->
            request.bodyHandler { body ->
                launch(context) {
                    when (request.method()) {
                        HttpMethod.GET -> request.response().response {
                            get(request.path(), request.query())
                        }
                        HttpMethod.POST -> request.response().response {
                            post(request.path(), body.bytes)
                        }
                        else -> request.response().response {
                            error("invalid request")
                        }
                    }
                }
            }
        }.listen(PORT) {
            if (it.succeeded()) {
                println("listening port: ${PORT}")
            } else {
                it.cause().printStackTrace()
            }
        }
    }

    inline fun HttpServerResponse.response(block: () -> String?) {
        try {
            val json = block()
            if (json != null) {
                putHeader("Content-Type", "application/json;charset=utf-8")
                end(json)
            } else {
                statusCode = 404
                end("Not Found")
            }
        } catch (e: Error) {
            e.printStackTrace()
            statusCode = 400
            end("Internal Server Error")
        } catch (e: Throwable) {
            statusCode = 400
            end("Bad Request")
        }

    }

    suspend fun get(path: CharSequence, query: CharSequence?): String? {
        if (path.startsWith("/users/")) {
            if (path.endsWith("/visits")) {
                val id = path.substring("/users/".length, path.length - "/visits".length).toInt()
                var country: String? = null
                var fromDate: Long? = null
                var toDate: Long? = null
                var toDistance: Int? = null

                query?.split("&")?.forEach{
                    val pair = it.split("=")
                    when(pair[0]) {
                        "country" -> country = pair[1]
                        "fromDate" -> fromDate = pair[1].toLong()
                        "toDate" -> toDate = pair[1].toLong()
                        "toDistance" -> toDistance = pair[1].toInt()
                    }
                }
                val visits = dao.findVisits(id, country, fromDate, toDate, toDistance)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatVisits(out, Visits2(visits))
                return out.toString()
            } else {
                val id = path.substring("/users/".length).toInt()
                val result = dao.findUser(id)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatUser(out, result)
                return out.toString()
            }
        } else if (path.startsWith("/locations/")) {
            if (path.endsWith("/avg")) {
                val id = path.substring("/locations/".length, path.length - "/avg".length).toInt()
                var fromDate: Long? = null
                var toDate: Long? = null
                var fromBirth: Long? = null
                var toBirth: Long? = null
                var gender: Char? = null

                query?.split("&")?.forEach{
                    val pair = it.split("=")
                    when(pair[0]) {
                        "fromDate" -> fromDate = pair[1].toLong()
                        "toDate" -> toDate = pair[1].toLong()
                        "fromAge" -> toBirth = DateTime.now().minusYears(pair[1].toInt()).millis / 1000
                        "toAge" -> fromBirth = DateTime.now().minusYears(pair[1].toInt()).millis / 1000
                        "gender" -> gender = pair[1].first()
                    }
                }
                val avg = dao.avg(id, fromDate, toDate, fromBirth, toBirth, gender)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatAvg(out, Avg(avg))
                return out.toString()
            } else {
                val id = path.substring("/locations/".length).toIntOrNull()
                        ?: error("invalid id")
                val result = dao.findLocation(id)
                        ?: return null
                val out = ByteArrayOutputStream()
                converter.formatLocation(out, result)
                return out.toString()
            }
        } else if (path.startsWith("/visits/")) {
            val id = path.substring("/visits/".length).toInt()
            val result = dao.findVisit(id)
                    ?: return null
            val out = ByteArrayOutputStream()
            converter.formatVisit(out, result)
            return out.toString()
        }
        return null
    }

    suspend fun post(path: CharSequence, body: ByteArray): String? {
        if (path.startsWith("/users/new")) {
            val user = converter.parseUser(ByteArrayInputStream(body))
            dao.insert(user)
            return "{}"
        } else if (path.startsWith("/locations/new")) {
            val location = converter.parseLocation(ByteArrayInputStream(body))
            dao.insert(location)
            return "{}"
        } else if (path.startsWith("/visits/new")) {
            val visit = converter.parseVisit(ByteArrayInputStream(body))
            dao.insert(visit)
            return "{}"
        } else if (path.startsWith("/users/")) {
            val id = path.substring("/users/".length).toInt()
            val result = dao.updateUser(id) {
                converter.parseUser(ByteArrayInputStream(body))
            }
            return if (result != null) "{}" else null
        } else if (path.startsWith("/locations/")) {
            val id = path.substring("/locations/".length).toInt()
            val result = dao.updateLocation(id) {
                converter.parseLocation(ByteArrayInputStream(body))
            }
            return if (result != null) "{}" else null
        } else if (path.startsWith("/visits/")) {
            val id = path.substring("/visits/".length).toInt()
            val result = dao.updateVisit(id) {
                converter.parseVisit(ByteArrayInputStream(body))
            }
            return if (result != null) "{}" else null
        }
        return null
    }
}