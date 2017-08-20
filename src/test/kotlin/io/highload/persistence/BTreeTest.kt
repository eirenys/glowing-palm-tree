package io.highload.persistence

import org.junit.Assert.assertEquals
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
    fun correctOrderTest() {
        tree.put(2, 20)
        tree.put(3, 30)
        tree.put(1, 10)
        assertEquals(listOf(1 to 10, 2 to 20, 3 to 30), tree.toList())
    }
}