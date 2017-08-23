package io.highload

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.highload.dao.StubDao
import io.highload.web.JacksonConverter
import io.highload.web.MainHandler
import io.highload.web.VertxServer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/**
 *
 */
//const val DATA_FILE = "C:\\main\\docker\\data.zip"
//const val PORT = 1488
const val DATA_FILE = "/tmp/data/data.zip"
const val PORT = 80

fun main(args: Array<String>) {
    val mapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    val dao = StubDao()
    val converter = JacksonConverter(mapper)
    val handler = MainHandler(dao, converter)

    launch(CommonPool) {
        val startTime = System.currentTimeMillis()
        println("start data import")
        ZipExtractor(dao, converter).extract(DATA_FILE)
        val time = (System.currentTimeMillis() - startTime) / 1000
        println("data imported ($time sec)")
    }

    val server = VertxServer(handler, CommonPool)
    server.start(PORT)
}