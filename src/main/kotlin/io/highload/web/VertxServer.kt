package io.highload.web

import io.highload.metrics.Metrics
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerResponse

/**
 *
 */
class VertxServer(val handler: MainHandler) {
    fun start(port: Int) {
        val vertx = Vertx.factory.vertx()
        val server = vertx.createHttpServer(HttpServerOptions().setAcceptBacklog(2048))

        server.requestHandler { request ->
            request.bodyHandler { body ->
                val m = Metrics()
                m.start()
                when (request.method()) {
                    HttpMethod.GET -> request.response().response {
                        m.bq()
                        try {
                            handler.get(request.path(), request.query())
                        } finally {
                            m.aq()
                        }
                    }
                    HttpMethod.POST -> request.response().response {
                        m.bq()
                        try {
                            handler.post(request.path(), body.bytes)
                        } finally {
                            m.aq()
                        }
                    }
                    else -> request.response().response {
                        null // 404
                    }
                }
            }
        }.listen(port) {
            if (it.succeeded()) {
                println("listening port: $port")
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