package io.highload.persistence

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFails

/**
 *
 */
class BTreeNodeTest {
    val treeNode = BTreeNode<Int, Int>(Comparator { o1, o2 -> o1.compareTo(o2) }, 4)

    @Test
    fun getKeyValueTest() {
        treeNode.insert(0, 1, 2)
        assertEquals(1, treeNode.getKey(0))
        assertEquals(2, treeNode.getValue(0))
    }

    @Test
    fun findIndexOnEmptyNodeTest() {
        assertEquals(KeyIndex(false, 0), treeNode.findIndex(111))
    }

    @Test
    fun findIndexOnSingleElementNodeTest() {
        treeNode.insert(0, 10, 11)

        assertEquals(KeyIndex(false, 0), treeNode.findIndex(0))
        assertEquals(KeyIndex(true, 0), treeNode.findIndex(10))
        assertEquals(KeyIndex(false, 1), treeNode.findIndex(20))
    }

    @Test
    fun findIndexOnTwoElementsNodeTest() {
        treeNode.insert(0, 10, 11)
        treeNode.insert(1, 20, 21)

        assertEquals(KeyIndex(false, 0), treeNode.findIndex(0))
        assertEquals(KeyIndex(true, 0), treeNode.findIndex(10))
        assertEquals(KeyIndex(false, 1), treeNode.findIndex(15))
        assertEquals(KeyIndex(true, 1), treeNode.findIndex(20))
        assertEquals(KeyIndex(false, 2), treeNode.findIndex(25))
    }

    @Test
    fun findIndexTest() {
        treeNode.insert(0, 10, 11)
        treeNode.insert(1, 20, 21)
        treeNode.insert(2, 30, 31)

        assertEquals(KeyIndex(false, 0), treeNode.findIndex(0))
        assertEquals(KeyIndex(true, 0), treeNode.findIndex(10))
        assertEquals(KeyIndex(false, 1), treeNode.findIndex(15))
        assertEquals(KeyIndex(true, 1), treeNode.findIndex(20))
        assertEquals(KeyIndex(false, 2), treeNode.findIndex(25))
        assertEquals(KeyIndex(true, 2), treeNode.findIndex(30))
        assertEquals(KeyIndex(false, 3), treeNode.findIndex(35))
    }

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

    @Test
    fun replaceTest() {
        treeNode.insert(0, 10, 11)
        treeNode.replace(0, 10, 12)

        assertEquals(listOf(10 to 12), treeNode.toList())
    }

    @Test
    fun removeTest() {
        treeNode.insert(0, 10, 11)
        treeNode.insert(1, 20, 21)
        treeNode.remove(0)

        assertEquals(listOf(20 to 21), treeNode.toList())
    }
}