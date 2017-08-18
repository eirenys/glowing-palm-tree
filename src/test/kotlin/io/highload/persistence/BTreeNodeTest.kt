package io.highload.persistence

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFails

/**
 *
 */
class BTreeNodeTest {
    val treeNode = BTreeNode<Int, Int>(Comparator { o1, o2 -> o1.compareTo(o2) }, 8)

    @Test
    fun insertSimpleTest() {
        treeNode.insert(0, 10, 11)
        assertEquals(listOf(10 to 11), treeNode.toList())
    }

    @Test
    fun insertOutboundTest() {
        assertFails {
            treeNode.insert(1, 10, 11)
        }
    }

    @Test
    fun insertOverflowTest() {
        assertFails {
            treeNode.insert(0, 10, 11)
            treeNode.insert(1, 20, 21)
            treeNode.insert(2, 30, 31)
            treeNode.insert(3, 40, 41)
            treeNode.insert(4, 50, 51)
            treeNode.insert(5, 60, 61)
            treeNode.insert(6, 70, 71)
            treeNode.insert(7, 80, 81)
            treeNode.insert(8, 90, 91)
        }
    }

    @Test
    fun insertAtStartTest() {
        treeNode.insert(0, 30, 31)
        treeNode.insert(0, 20, 21)
        treeNode.insert(0, 10, 11)
        assertEquals(listOf(10 to 11, 20 to 21, 30 to 31), treeNode.toList())
    }

    @Test
    fun insertAtCenterTest() {
        treeNode.insert(0, 10, 11)
        treeNode.insert(1, 30, 31)
        treeNode.insert(1, 20, 21)
        assertEquals(listOf(10 to 11, 20 to 21, 30 to 31), treeNode.toList())
    }

    @Test
    fun insertAtEndTest() {
        treeNode.insert(0, 10, 11)
        treeNode.insert(1, 20, 21)
        treeNode.insert(2, 30, 31)
        assertEquals(listOf(10 to 11, 20 to 21, 30 to 31), treeNode.toList())
    }
}