package io.highload.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.dao.StubDao
import io.highload.scheme.makeLocation
import io.highload.scheme.makeVisit
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFails

/**
 *
 */
class MainHandlerTest {
    val dao = StubDao()
    val handler = MainHandler(dao, JacksonConverter(ObjectMapper()))
    val emptyParams = QueryParams(null, null, null, null, null, null, null)
    val location = makeLocation(123)

    init {
        runBlocking {
            dao.insert(location)
        }
    }

    @Test
    fun emptyBodyPostTest() {
        assertFails {
            runBlocking {
                handler.post("", "".toByteArray())
            }
        }
    }

    @Test
    fun convertVisitsTest() = runBlocking {
        val visits = listOf(makeVisit(1, 123, 0, 0, 0), makeVisit(2, 123, 0, 0, 0))
        val result = handler.convertVisits(visits, emptyParams)
        assertEquals(2, result.size)
    }

    @Test
    fun avgTest() = runBlocking {
        val visits = listOf(mark(2), mark(4), mark(5))
        val result = handler.avg(visits, emptyParams)
        assertEquals(BigDecimal("3.66667"), result)
    }

    private fun mark(mark: Int) = makeVisit(0, 0, 0, 0, mark)
}