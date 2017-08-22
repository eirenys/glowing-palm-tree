package io.highload

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.highload.dao.StubDao
import io.highload.json.JacksonConverter
import kotlinx.coroutines.experimental.CommonPool

/**
 *
 */
//const val DATA_FILE = "C:\\main\\docker\\data.zip"
const val DATA_FILE = "C:\\main\\docker\\testdata.zip"
const val PORT = 1488
//const val DATA_FILE = "/tmp/data/data.zip"
//const val PORT = 80

fun main(args: Array<String>) {
    val mapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    val dao = StubDao()
    val converter = JacksonConverter(mapper)

    ZipExtractor(dao, converter, CommonPool).extract(DATA_FILE)

    val server = VertxServer(dao, converter, CommonPool)
    server.start(PORT)
}