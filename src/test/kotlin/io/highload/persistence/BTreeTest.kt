package io.highload.persistence

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

/**
 *
 */
class BTreeTest {
    val tree = BTree<Int, Int>(Comparator { o1, o2 -> o1.compareTo(o2) }, 4)

    @Test
    fun putTest() {
        tree.put(1, 2)
        assertEquals(listOf(1 to 2), tree.toList())
    }

    @Test
    fun putCorrectOrderTest() {
        tree.put(2, 20)
        tree.put(3, 30)
        tree.put(1, 10)
        assertEquals(listOf(1 to 10, 2 to 20, 3 to 30), tree.toList())
    }

    @Test
    fun putUniqueTest() {
        tree.put(1, 10)
        tree.put(1, 11)
        assertEquals(listOf(1 to 11), tree.toList())
    }

    @Test
    fun expandOnPutTest() {
        tree.put(10, 11)
        tree.put(20, 21)
        tree.put(30, 31)
        tree.put(40, 41)
        tree.put(50, 51)
        tree.put(0, 0)
        tree.put(15, 15)
//        assertEquals(listOf(0 to 0, 10 to 11, 15 to 15, 20 to 21, 30 to 31, 40 to 41, 50 to 51), tree.toList())
    }
}