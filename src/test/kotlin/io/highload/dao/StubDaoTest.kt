package io.highload.dao

import io.highload.scheme.*
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

/**
 *
 */
class StubDaoTest {
    val dao = StubDao()
    val user = makeUser(10)
    val location = makeLocation(20)
    val visits: List<Visit>

    init {
        visits = (0..9).map { i ->
            makeVisit(
                    id = 30 + i,
                    userId = 10,
                    locId = 20 + (i / 5),
                    distance = 9 - (i / 2),
                    mark = 3)
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
    fun updateSingleEntityTest() = runBlocking {
        dao.updateUser(10) {
            val user2 = User()
            user2[0] = 10
            user2[5] = 12
            user2
        }

        assertEquals(1, dao.users.size)
    }

    @Test
    fun onUpdateFailTest() = runBlocking {
        try {
            dao.updateUser(10) {
                error("parse error")
            }
        } catch (e: Throwable) {
        }
        assertEquals(1, dao.users.size)
    }

    @Test
    fun findVistsTest11() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, null, null).orEmpty()
        assertEquals(10, result.size)
    }

    @Test
    fun findVistsTest12() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, 7, 9).orEmpty()
        assertEquals(listOf(32, 33), result.map { it.id })
    }

    @Test
    fun findVistsTest13() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, 8, 8)
        assertNotNull(result)
        assertEquals(0, result?.size)
    }

    @Test
    fun findVistsTest14() = runBlocking {
        val result = dao.findOrderedVisitsByUserId(10, 9, 8)
        assertNotNull(result)
        assertEquals(0, result?.size)
    }

    @Test
    fun findVistsTest21() = runBlocking {
        val result = dao.findVisitsByLocationId(20)?.toList().orEmpty()
        assertEquals(5, result.size)
    }
}