package io.highload.dao

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 *
 */
class KeysTest {
    @Test
    fun keyTest1() {
        val key1 = UserVisitKey(0, 0, 0)
        val key2 = UserVisitKey(0, 0, 1)

        assertTrue(key1 < key2)
    }

    @Test
    fun keyTest2() {
        val key1 = UserVisitKey(0, 0, 0)
        val key2 = UserVisitKey(0, 1, 1)

        assertTrue(key1 < key2)
    }

    @Test
    fun keyTest3() {
        val key1 = UserVisitKey(0, 0, 0)
        val key2 = UserVisitKey(0, 1, 0)

        assertTrue(key1 < key2)
    }

    @Test
    fun keyTest4() {
        val key1 = UserVisitKey(0, 0, Int.MIN_VALUE)
        val key2 = UserVisitKey(0, 0, Int.MAX_VALUE)

        assertTrue(key1 < key2)
    }

    @Test
    fun keyTest5() {
        val key1 = LocationVisitKey(0, Int.MIN_VALUE)
        val key2 = LocationVisitKey(0, Int.MAX_VALUE)

        assertTrue(key1 < key2)
    }
}