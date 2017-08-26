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
                        handler.get(buf.get(req.path), buf.get(req.query))
                    } finally {
                        m.aq()
                    }
                }
            } else if (matches(buf, req.verb, io.highload.web.POST)) {
                req.response(ctx) {
                    m.bq()
                    try {
                        val arr = ByteArray(req.body.length)
                        buf.get(req.body, arr, 0)
                        handler.post(buf.get(req.path), arr)
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

    protected inline fun RapidoidHelper.response(ctx: Channel, block: () -> ByteArray?): HttpStatus {
        val keepAlive = isKeepAlive.value
        try {
            val json = block()
            if (json != null) {
                startResponse(ctx, keepAlive)
                writeBody(ctx, json, medType)
                ctx.closeIf(!keepAlive)
            } else {
                ctx.write(HTTP_404)
                ctx.closeIf(!keepAlive)
            }
        } catch (e: Error) {
            e.printStackTrace()
            ctx.write(BAD_REQ)
            ctx.closeIf(!keepAlive)
        } catch (e: Throwable) {
            ctx.write(BAD_REQ)
            ctx.closeIf(!keepAlive)
        }

        return HttpStatus.ASYNC
    }
}