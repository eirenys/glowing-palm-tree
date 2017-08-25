package io.highload

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.highload.dao.StubDao
import io.highload.web.JacksonConverter
import io.highload.web.MainHandler
import io.highload.web.SocketServer

/**
 *
 */
const val LOCAL_DATA_FILE = "C:\\main\\docker\\data.zip"
const val LOCAL_PORT = 1488
const val DATA_FILE = "/tmp/data/data.zip"
const val PORT = 80

fun main(args: Array<String>) {
    val local = args.firstOrNull() == "-local"
    val dataFile = if (local) LOCAL_DATA_FILE else DATA_FILE
    val port = if (local) LOCAL_PORT else PORT
    val mapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    val dao = StubDao()
    val converter = JacksonConverter(mapper)
    val handler = MainHandler(dao, converter)

    // init phase
    try {
        dao.findUser(-1)
        handler.get("", null)
    } catch (e: Throwable) {
    }
    try {
        handler.post("", "{}".toByteArray())
    } catch (e: Throwable) {
    }

    val startTime = System.currentTimeMillis()
    println("start data import")
    ZipExtractor(dao, converter).extract(dataFile)
    val time = (System.currentTimeMillis() - startTime) / 1000
    println("data imported ($time sec)")

//    VertxServer(handler).start(port)
    SocketServer(handler).start(port)
}