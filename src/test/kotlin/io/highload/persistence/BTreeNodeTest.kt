package io.highload.persistence

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertFails

/**
 *
 */
class BTreeNodeTest {
    val treeNode = BTreeNode<Int>(Comparator { o1, o2 -> o1.compareTo(o2) }, 4)

    @Test
    fun getKeyValueTest() {
        treeNode.insert(0, 1)
        assertEquals(1, treeNode[0])
    }

    @Test
    fun findIndexOnEmptyNodeTest() {
        assertEquals(KeyIndex(false, 0), treeNode.findIndex(111))
    }

    @Test
    fun findIndexOnSingleElementNodeTest() {
        treeNode.insert(0, 10)

        assertEquals(KeyIndex(false, 0), treeNode.findIndex(0))
        assertEquals(KeyIndex(true, 0), treeNode.findIndex(10))
        assertEquals(KeyIndex(false, 1), treeNode.findIndex(20))
    }

    @Test
    fun findIndexOnTwoElementsNodeTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 20)

        assertEquals(KeyIndex(false, 0), treeNode.findIndex(0))
        assertEquals(KeyIndex(true, 0), treeNode.findIndex(10))
        assertEquals(KeyIndex(false, 1), treeNode.findIndex(15))
        assertEquals(KeyIndex(true, 1), treeNode.findIndex(20))
        assertEquals(KeyIndex(false, 2), treeNode.findIndex(25))
    }

    @Test
    fun findIndexTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 20)
        treeNode.insert(2, 30)

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
        treeNode.insert(0, 10)
        assertEquals(listOf(10), treeNode.toList())
    }

    @Test
    fun insertOutboundTest() {
        assertFails {
            treeNode.insert(1, 10)
        }
    }

    @Test
    fun insertOverflowTest() {
        assertFails {
            treeNode.insert(0, 10)
            treeNode.insert(1, 20)
            treeNode.insert(2, 30)
            treeNode.insert(3, 40)
            treeNode.insert(4, 50)
        }
    }

    @Test
    fun insertAtStartTest() {
        treeNode.insert(0, 30)
        treeNode.insert(0, 20)
        treeNode.insert(0, 10)
        assertEquals(listOf(10, 20, 30), treeNode.toList())
    }

    @Test
    fun insertAtCenterTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 30)
        treeNode.insert(1, 20)
        assertEquals(listOf(10, 20, 30), treeNode.toList())
    }

    @Test
    fun insertAtEndTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 20)
        treeNode.insert(2, 30)
        assertEquals(listOf(10, 20, 30), treeNode.toList())
    }

    @Test
    fun replaceTest() {
        treeNode.insert(0, 10)
        treeNode.replace(0, 10)

        assertEquals(listOf(10), treeNode.toList())
    }

    @Test
    fun removeTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 20)
        treeNode.remove(0)

        assertEquals(listOf(20), treeNode.toList())
    }

    @Test
    fun splitTest() {
        treeNode.insert(0, 10)
        treeNode.insert(1, 20)
        treeNode.insert(2, 30)
        treeNode.insert(3, 40)
        val new = treeNode.split(1)

        assertEquals(listOf(10), treeNode.toList())
        assertEquals(listOf(20, 30, 40), new.toList())
    }

    @Test
    fun unloadLoadTest() {
        treeNode.insert(0, 10)
        treeNode.unload()
        treeNode.load()
        assertEquals(listOf(10), treeNode.toList())
    }
}