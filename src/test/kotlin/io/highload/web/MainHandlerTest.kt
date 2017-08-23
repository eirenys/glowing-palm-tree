package io.highload.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.highload.dao.StubDao
import io.highload.scheme.Location
import io.highload.scheme.Visit
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
    val location = Location()

    init {
        location[0] = 123
        location[1] = "place"
        location[2] = "country"
        location[3] = "city"
        location[4] = 1000
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
        val visits = listOf(visit(), visit())
        val result = handler.convertVisits(visits, emptyParams)
        assertEquals(2, result.size)
    }

    @Test
    fun avgTest() = runBlocking {
        val visits = listOf(mark(2), mark(4), mark(5))
        val result = handler.avg(visits, emptyParams)
        assertEquals(BigDecimal("3.66667"), result)
    }

    private fun visit() = Visit().also {
        it[0] = 0
        it[1] = 123
        it[2] = 0
        it[3] = 0
        it[4] = 0
    }

    private fun mark(mark: Int) = Visit().also {
        it[4] = mark
    }
}