package io.highload.web

import io.highload.metrics.Metrics
import io.highload.metrics.MetricsAggregator
import org.rapidoid.buffer.Buf
import org.rapidoid.http.AbstractHttpServer
import org.rapidoid.http.HttpStatus
import org.rapidoid.http.MediaType
import org.rapidoid.net.abstracts.Channel
import org.rapidoid.net.impl.RapidoidHelper

/**
 *
 */
class RapidoidServer(val handler: MainHandler) : AbstractHttpServer() {
    val medType = MediaType.create("application/json;charset=utf-8", "json", "map")
    val BAD_REQ = fullResp(400, "Bad request".toByteArray());

    override fun handle(ctx: Channel, buf: Buf, req: RapidoidHelper): HttpStatus {
        val m = Metrics()
        m.start()
        try {
            return if (matches(buf, req.verb, io.highload.web.GET)) {
                req.response(ctx) {
                    m.bq()
                    try {
                        handler.get(buf.get(req.path), buf.get(req.query))?.toByteArray()
                    } finally {
                        m.aq()
                    }
                }
            } else if (matches(buf, req.verb, io.highload.web.POST)) {
                req.response(ctx) {
                    m.bq()
                    try {
                        handler.post(buf.get(req.path), buf.get(req.body).toByteArray())?.toByteArray()
                    } finally {
                        m.aq()
                    }
                }
            } else {
                req.response(ctx) {
                    null // 404
                }
            }
        } finally {
            m.end()
            MetricsAggregator.save(m)
        }
    }

    fun start(port: Int) {
        listen(port)
//        val vertx = Vertx.factory.vertx()
//        val server = vertx.createHttpServer(HttpServerOptions().setAcceptBacklog(2048))
//
//        server.requestHandler { request ->
//            request.bodyHandler { body ->
//                val m = Metrics()
//                m.start()
//                when (request.method()) {
//                    HttpMethod.GET -> request.response().response {
//                        m.bq()
//                        try {
//                            handler.get(request.path(), request.query())
//                        } finally {
//                            m.aq()
//                        }
//                    }
//                    HttpMethod.POST -> request.response().response {
//                        m.bq()
//                        try {
//                            handler.post(request.path(), body.bytes)
//                        } finally {
//                            m.aq()
//                        }
//                    }
//                    else -> request.response().response {
//                        null // 404
//                    }
//                }
//            }
//        }.listen(port) {
//            if (it.succeeded()) {
//                println("listening port: $port")
//            } else {
//                it.cause().printStackTrace()
//            }
//        }
    }

    protected inline fun RapidoidHelper.response(ctx: Channel, block: () -> ByteArray?): HttpStatus {
        try {
            val json = block()
            if (json != null) {
                return ok(ctx, isKeepAlive.value, json, medType)
            } else {
                return HttpStatus.NOT_FOUND
            }
        } catch (e: Error) {
            e.printStackTrace()
            ctx.write(BAD_REQ)
            return HttpStatus.DONE
        } catch (e: Throwable) {
            ctx.write(BAD_REQ)
            return HttpStatus.DONE
        }
    }
}