package io.highload.metrics

/**
 *
 */
class Metrics {
    var startTime = 0L
    var bq = 0L
    var aq = 0L
    var endTime = 0L

    fun start() {
        startTime = System.nanoTime()
    }

    fun bq() {
        bq = System.nanoTime()
    }

    fun aq() {
        aq = System.nanoTime()
    }

    fun end() {
        endTime = System.nanoTime()
    }


    fun f(time: Long) = "" + (time / 1000) + "." + (time % 1000)

    override fun toString(): String {
        return "accept = +" + f(bq - startTime) + ", query = +" + f(aq - bq) + ", resp = " + f(endTime - aq) +
                ", total = " + f(endTime - startTime)
    }
}