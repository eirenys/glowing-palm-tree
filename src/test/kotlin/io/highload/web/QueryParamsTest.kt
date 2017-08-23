package io.highload.web

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *
 */
class QueryParamsTest {
    @Test
    fun parametersTest() {
        val query = QueryParams.parse("country=test&toDate=1")
        assertEquals("test", query.country)
        assertEquals(1, query.toDate)
    }

    @Test
    fun countryTest() {
        val query = QueryParams.parse("country=%D0%98%D0%BE%D1%80%D0%B4%D0%B0%D0%BD%D0%B8%D1%8F")
        assertEquals("Иордания", query.country)
    }
}