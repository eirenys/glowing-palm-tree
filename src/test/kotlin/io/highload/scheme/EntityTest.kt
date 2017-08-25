package io.highload.scheme

import org.junit.Assert
import org.junit.Test

/**
 *
 */
class EntityTest {

    @Test
    fun userTest() {
        val expected = "{\"id\":1,\"email\":\"email 1\",\"first_name\":\"firstName 1\",\"last_name\":\"lastName 1\"," +
                "\"gender\":\"m\",\"birth_date\":2}"
        val res = makeUser(1).toString()
        Assert.assertEquals(expected, res)
    }

    @Test
    fun locationTest() {
        val expected = "{\"id\":2,\"place\":\"place 2\",\"country\":\"country 2\",\"city\":\"city 2\",\"distance\":3}"
        val res = makeLocation(2).toString()
        Assert.assertEquals(expected, res)
    }

    @Test
    fun visitTest() {
        val expected = "{\"id\":3,\"location\":2,\"user\":1,\"visited_at\":4,\"mark\":5}"
        val res = makeVisit(3, 2, 1, 4, 5).toString()
        Assert.assertEquals(expected, res)
    }

    @Test
    fun visit2Test() {
        val expected = "{\"place\":\"place 1\",\"mark\":5,\"visited_at\":4}"
        val res = Visit2(makeVisit(3, 2, 1, 4, 5), makeLocation(1)).toString()
        Assert.assertEquals(expected, res)
    }
}