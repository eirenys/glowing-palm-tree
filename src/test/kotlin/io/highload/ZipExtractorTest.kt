package io.highload

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.highload.dao.StubDao
import io.highload.web.JacksonConverter
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *
 */
class ZipExtractorTest {
    val mapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    val dao = StubDao()
    val extractor = ZipExtractor(dao, JacksonConverter(mapper))

    @Test
    fun extractTest() {
        runBlocking {
            extractor.extractResource("testdata.zip")
            assertEquals(11, dao.users.size)
            assertEquals(5, dao.locations.size)
            assertEquals(22, dao.visits.size)
        }
    }
}