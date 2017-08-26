package io.highload.persistence

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *
 */
class BTreeTest {
    val tree = BTree<Int>(Comparator { o1, o2 -> o1.compareTo(o2) }, 4)

    @Test
    fun putTest() = runBlocking {
        tree.put(1)
        assertEquals(listOf(1), tree.toList())
    }

    @Test
    fun putCorrectOrderTest() = runBlocking {
        tree.put(2)
        tree.put(3)
        tree.put(1)
        assertEquals(listOf(1, 2, 3), tree.toList())
    }

    @Test
    fun putUniqueTest() = runBlocking {
        tree.put(1)
        tree.put(1)
        assertEquals(listOf(1), tree.toList())
    }

    @Test
    fun expandOnPutTest() = runBlocking {
        tree.put(10)
        tree.put(20)
        tree.put(30)
        tree.put(40)
        tree.put(50)
        tree.put(0)
        tree.put(15)
        assertEquals(listOf(0, 10, 15, 20, 30, 40, 50), tree.toList())
    }
}