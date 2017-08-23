package io.highload.web

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

/**
 *
 */
class VertxServer(val handler: MainHandler, val context: CoroutineContext) {
    fun start(port: Int) {
        val vertx = Vertx.factory.vertx()
        val server = vertx.createHttpServer()

        server.requestHandler { request ->
            request.bodyHandler { body ->
                launch(context) {
                    when (request.method()) {
                        HttpMethod.GET -> request.response().response {
                            handler.get(request.path(), request.query())
                        }
                        HttpMethod.POST -> request.response().response {
                            handler.post(request.path(), body.bytes)
                        }
                        else -> request.response().response {
                            error("invalid request")
                        }
                    }
                }
            }
        }.listen(port) {
            if (it.succeeded()) {
                println("listening port: ${port}")
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
}