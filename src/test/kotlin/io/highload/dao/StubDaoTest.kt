package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 *
 */
class StubDaoTest {
    val dao = StubDao()
    val user = User()
    val location = Location()
    val visits: List<Visit>

    init {
        user[0] = 10
        user[1] = "email"
        user[2] = "firstName"
        user[3] = "lastName"
        user[4] = 'm'
        user[5] = 11
        location[0] = 20
        location[1] = "place"
        location[2] = "country"
        location[3] = "city"
        location[4] = 21

        visits = (0..9).map { i ->
            val vis = Visit()
            vis[0] = 30 + i
            vis[1] = 20 + (i /5)
            vis[2] = 10
            vis[3] = 9 - (i / 2)
            vis[4] = 3
            vis
        }

        runBlocking {
            dao.insert(user)
            dao.insert(location)
            visits.forEach {
                dao.insert(it)
            }
        }
    }

    @Test
    fun insertFailTest() {
        assertFails {
            runBlocking {
                dao.insert(User())
            }
        }
    }

    @Test
    fun insertDuplicateFailTest() {
        assertFails {
            runBlocking {
                dao.insert(user)
            }
        }
    }

    @Test
    fun findVistsTest1() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, null, null)?.toList().orEmpty()
        assertEquals(10, result.size)
    }

    @Test
    fun findVistsTest2() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, 7, 9)?.toList().orEmpty()
        assertEquals(listOf(32, 33), result.map { it.id })
    }

    @Test
    fun findVistsTest3() = runBlocking {
        val result = dao.findVisitsByLocationId(20)?.toList().orEmpty()
        assertEquals(5, result.size)
    }
}