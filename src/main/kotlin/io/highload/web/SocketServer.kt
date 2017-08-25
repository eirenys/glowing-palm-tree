package io.highload.web

import io.highload.metrics.Metrics
import io.highload.metrics.MetricsAggregator
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.CharBuffer

/**
 *
 */
class SocketServer(val handler: MainHandler) {
    val BUFFER_SIZE = 2048

    fun start(port: Int) {
        val ss = ServerSocket(port, 4096)

        (1..16).forEach {
            Thread {
                listenCycle(ss)
            }.start()
        }

        MetricsAggregator.startProduce()
    }

    fun listenCycle(ss: ServerSocket) {
        do {
            val m = Metrics()
            val sock = ss.accept()
            m.start()
            try {
                val byteArray = ByteArray(BUFFER_SIZE)
                var fullBuffer = ByteBuffer.wrap(byteArray)
                val inp = sock.getInputStream()
                var limit = inp.read(byteArray)
                if (limit == 4096) {
                    while (inp.available() > 0) {
                        fullBuffer = ByteBuffer.allocate(fullBuffer.capacity() + BUFFER_SIZE)
                        val readed2 = sock.getInputStream().read(byteArray)
                        fullBuffer.put(fullBuffer).put(ByteBuffer.wrap(byteArray))
                        limit += readed2
                    }
                }
                fullBuffer.limit(limit)

                val seq = BufSeq(Charsets.UTF_8.decode(fullBuffer))
                val stream = RStream(sock.getOutputStream(), byteArray)
                if (seq.startsWith("POST")) {
                    stream.response {
                        m.bq()
                        try {
                            val body = seq.substring(seq.indexOf("\r\n\r\n") + 4, seq.end)
                            handler.post(seq.subSequence(5, seq.indexOf(' ', 5)), body.toByteArray())
                        } finally {
                            m.aq()
                        }
                    }
                } else if (seq.startsWith("GET")) {
                    stream.response {
                        m.bq()
                        try {
                            val path = seq.subSequence(4, seq.indexOf(' ', 5))
                            val pos = path.indexOf('?')
                            if (pos == -1) {
                                handler.get(path, null)
                            } else {
                                handler.get(path.subSequence(0, pos), path.subSequence(pos + 1, path.length))
                            }
                        } finally {
                            m.aq()
                        }
                    }
                } else {
                    stream.response {
                        null // 404
                    }
                }
            } finally {
                sock.close()
                m.end()
                MetricsAggregator.save(m)
            }
        } while (true)
    }

    inline fun RStream.response(block: (RStream) -> String?) {
        try {
            val json = block(this)?.toByteArray()
            if (json != null) {
                write(HTTP_OK)
                write(CONTENT_TYPE_JSON)
                write(CONTENT_LENGTH)
                write(Integer.toString(json.size).toByteArray())
                write(LINELINE)
                write(json)
            } else {
                write(HTTP_NOT_FOUND)
            }
        } catch (e: Error) {
            e.printStackTrace()
            write(HTTP_ERROR)
        } catch (e: Throwable) {
            write(HTTP_ERROR)
        }
        flush()
    }

    class BufSeq(val chbuf: CharBuffer, val start: Int = 0, val end: Int = chbuf.length) : CharSequence {
        override val length: Int get() = end - start

        override fun get(index: Int): Char = chbuf[start + index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = BufSeq(chbuf, start + startIndex, start + endIndex)

        override fun toString(): String = String(chbuf.array(), start, end - start)
    }
}