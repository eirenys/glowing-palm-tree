package io.highload.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.dao.StubDao
import io.highload.scheme.Visit
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

/**
 *
 */
class MainHandlerTest {
    val dao = StubDao()
    val handler = MainHandler(dao, JacksonConverter(ObjectMapper()))
    val emptyParams = QueryParams(null, null, null, null, null, null, null)

    @Test
    fun avgTest() = runBlocking {
        val visits = listOf(mark(2), mark(4), mark(5))
        val result = handler.avg(visits, emptyParams)
        assertEquals(BigDecimal("3.66667"), result)
    }

    private fun mark(mark: Int) = Visit().also {
        it[4] = mark
    }
}