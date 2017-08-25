package io.highload.metrics

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.CoroutineContext

/**
 *
 */
object MetricsAggregator {
    val mmm: AtomicReference<Metrics> = AtomicReference()
    val count = AtomicLong()
    val totalTime = AtomicLong()

    fun startProduce(context: CoroutineContext) {
        while (true) {
            Thread.sleep(5000)
            val a = mmm.get()
            if (a != null) {
                println(a)
                println("count = $count, totalTime = $totalTime, average = " + (totalTime.get() / count.get()))
            }
        }
    }

    fun save(m: Metrics) {
        mmm.set(m)
        val time = (m.endTime - m.startTime) / 1000
        count.incrementAndGet()
        totalTime.addAndGet(time)
    }
}