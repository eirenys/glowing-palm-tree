package io.highload.metrics

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 *
 */
object MetricsAggregator {
    val mmm: AtomicReference<Metrics> = AtomicReference()
    val count = AtomicLong()
    val totalTime = AtomicLong()
    val startedImp = AtomicLong()
    val endedImp = AtomicLong()

    fun startProduce(local: Boolean) {
        while (true) {
            Thread.sleep(if (local) 3000 else 30000)
            val a = mmm.get()
            if (a != null) {
                println(a)
                print("cnt = $count, avg = " + (totalTime.get() / count.get()))
            }
            val imp = endedImp.get() - startedImp.get()
            if (imp != 0L) {
                print(" $imp of ${startedImp.get()} ")
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