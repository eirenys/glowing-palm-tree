package io.highload.web

import io.highload.metrics.Metrics
import io.highload.metrics.MetricsAggregator
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.nio.charset.Charset
import java.util.concurrent.Future
import kotlin.coroutines.experimental.CoroutineContext

/**
 *
 */
class AsyncSocketServer(val handler: MainHandler, val context: CoroutineContext) {
    val chset = Charset.forName("UTF-8")

    fun start(port: Int) {
        val listener = AsynchronousServerSocketChannel.open().bind(InetSocketAddress(port))
        listener.accept("", Handler(listener))
        MetricsAggregator.startProduce()
    }

    inline suspend fun AsynchronousSocketChannel.response(block: () -> String?) {
        try {
            val json = block()?.toByteArray()
            if (json != null) {
                writeSuspended(HTTP_OK)
                writeSuspended(CONTENT_TYPE_JSON)
                writeSuspended(CONTENT_LENGTH)
                writeSuspended(Integer.toString(json.size).toByteArray())
                writeSuspended(LINE)
                writeSuspended(LINE)
                writeSuspended(json)
            } else {
                writeSuspended(HTTP_NOT_FOUND)
            }
        } catch (e: Error) {
            e.printStackTrace()
            writeSuspended(HTTP_ERROR)
        } catch (e: Throwable) {
            writeSuspended(HTTP_ERROR)
        } finally {
            close()
        }
    }

    suspend fun AsynchronousSocketChannel.writeSuspended(byteArray: ByteArray) = kotlinx.coroutines.experimental.run(CommonPool) {
        write(ByteBuffer.wrap(byteArray)).getSuspended()
    }

    suspend fun <T> Future<T>.getSuspended(): T = kotlinx.coroutines.experimental.run(CommonPool) {
        this.get()
    }

    class BufSeq(val chbuf: CharBuffer, val start: Int = 0, val end: Int = chbuf.length) : CharSequence {
        override val length: Int get() = end - start

        override fun get(index: Int): Char = chbuf[start + index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = BufSeq(chbuf, start + startIndex, start + endIndex)

        override fun toString(): String = String(chbuf.array(), start, end - start)
    }

    inner class Handler(val listener: AsynchronousServerSocketChannel) : CompletionHandler<AsynchronousSocketChannel, String> {
        override fun completed(channel: AsynchronousSocketChannel, attachment: String) {
            listener.accept(attachment, this)

            val m = Metrics()
            launch(context) {
                m.start()
                try {
                    val byteBuffer = ByteBuffer.allocate(4096)
                    var fullBuffer = byteBuffer
                    val readed = channel.read(byteBuffer).getSuspended()
                    if (readed == 4096) {
                        while (true) {
                            val readed2 = channel.read(byteBuffer).getSuspended()
                            if (readed2 == -1) {
                                break
                            }
                            fullBuffer = ByteBuffer.allocate(fullBuffer.capacity() + readed2).put(fullBuffer).put(byteBuffer)
                        }
                    }

                    fullBuffer.flip()
                    val charBuffer = chset.decode(fullBuffer)
                    val seq = BufSeq(charBuffer)

                    if (seq.startsWith("POST")) {
                        channel.response {
                            m.bq()
                            val body = seq.substring(seq.indexOf("\r\n\r\n") + 4, seq.end)
                            val res = handler.post(seq.subSequence(5, seq.indexOf(' ', 5)), body.toByteArray())
                            m.aq()
                            res
                        }
                    } else if (seq.startsWith("GET")) {
                        channel.response {
                            m.bq()
                            val path = seq.subSequence(4, seq.indexOf(' ', 5))
                            val pos = path.indexOf('?')
                            val res = if (pos == -1) {
                                handler.get(path, null)
                            } else {
                                handler.get(path.subSequence(0, pos), path.subSequence(pos + 1, path.length))
                            }
                            m.aq()
                            res
                        }
                    } else {
                        channel.response {
                            null // 404
                        }
                    }
                } catch (e: Throwable) {

                } finally {
                    m.end()
                    MetricsAggregator.save(m)
                }
            }
        }

        override fun failed(exc: Throwable?, attachment: String) {
            listener.accept(attachment, this)
        }
    }
}