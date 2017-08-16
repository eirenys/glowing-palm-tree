package io.highload

import io.vertx.core.Vertx

/**
 *
 */
fun main(args: Array<String>) {
    val vertx = Vertx.factory.vertx()
    val server = vertx.createHttpServer()
    server.requestHandler { request ->
        request.bodyHandler { body ->
            println(body)
            request.response().end("ok")
        }
    }.listen(1488)
}