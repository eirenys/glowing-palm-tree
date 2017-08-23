package io.highload.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.fail

/**
 *
 */
class JacksonConverterTest {
    val converter = JacksonConverter(ObjectMapper())

    @Test
    fun parseInvalidJsonTest() {
        assertFails {
            val json = "{\"users\":[}"
            converter.parseUsers(json.byteInputStream()).forEach {
            }
        }
    }

    @Test
    fun parseEmptyUserListTest() {
        val json = "{\"users\":[]}"
        converter.parseUsers(json.byteInputStream()).forEach {
            fail("shold not be called")
        }
    }

    @Test
    fun parseEmptyLocationsListTest() {
        val json = "{\"locations\":[]}"
        converter.parseLocations(json.byteInputStream()).forEach {
            fail("shold not be called")
        }
    }

    @Test
    fun parseEmptyUserVisitsTest() {
        val json = "{\"visits\":[]}"
        converter.parseVisits(json.byteInputStream()).forEach {
            fail("shold not be called")
        }
    }

    @Test
    fun parseEmptyUserTest() {
        val json = "{\"users\":[{}]}"
        converter.parseUsers(json.byteInputStream()).forEach {
            for (i in 0..5) {
                assertEquals(null, it.tryGet(i))
            }
        }
    }

    @Test
    fun parseInvalidUserTest1() {
        assertFails {
            val json = "{\"users\":[{\"id\":null}]}"
            converter.parseUsers(json.byteInputStream()).forEach {
            }
        }
    }

    @Test
    fun parseInvalidUserTest2() {
        assertFails {
            val json = "{\"users\":[{\"first_name\":null}]}"
            converter.parseUsers(json.byteInputStream()).forEach {
            }
        }
    }

    @Test
    fun parseUserTest() {
        val json = "{\"users\":[{\"id\":0, \"email\":\"email\", \"first_name\":\"firstName\", " +
                "\"last_name\":\"lastName\", \"gender\":\"m\", \"birth_date\": 0}]}"
        converter.parseUsers(json.byteInputStream()).forEach {
            it.checkEntity()
        }
    }

    @Test
    fun parseLocationTest() {
        val json = "{\"locations\":[{\"id\":0, \"place\":\"place\", \"country\":\"country\", " +
                "\"city\":\"city\", \"distance\":0}]}"
        converter.parseLocations(json.byteInputStream()).forEach {
            it.checkEntity()
        }
    }

    @Test
    fun parseVisitTest() {
        val json = "{\"visits\":[{\"id\":0, \"location\":0, \"user\":0, \"visited_at\":0, \"mark\": 0}]}"
        converter.parseVisits(json.byteInputStream()).forEach {
            it.checkEntity()
        }
    }
}