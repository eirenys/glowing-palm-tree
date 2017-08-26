package io.highload.persistence

import kotlinx.coroutines.experimental.sync.Mutex

/**
 *
 */
class BTree<T>(val comparator: Comparator<T>, val nodeSize: Int = 64) : Iterable<T> {
    private val mutex = Mutex()
    private var lowest = BTreeNode(comparator, nodeSize)
    private var nodes: Array<BTreeNode<T>> = emptyArray()

    val size: Int get() = this.count()

    fun put(value: T): Boolean {
        val nodeIndex = findNodeIndex(value)
        loadNode(nodeIndex)
        val node = if (nodeIndex == -1) lowest else nodes[nodeIndex]
        val index = node.findIndex(value)
        if (index.exists) {
            node.replace(index.position, value)
        } else {
            if (node.freeSpace > 0) {
                node.insert(index.position, value)
            } else {
                if (index.position == 0) {
                    val new = BTreeNode(comparator, nodeSize)
                    new.insert(0, value)
                    putNode(nodeIndex, new)
                } else if (index.position == nodeSize) {
                    val new = BTreeNode(comparator, nodeSize)
                    new.insert(0, value)
                    putNode(nodeIndex + 1, new)
                } else {
                    val splitted = node.split(index.position)
                    putNode(nodeIndex + 1, splitted)
                    val index2 = node.findIndex(value)
                    assert(node.freeSpace > 0)
                    node.insert(index2.position, value)
                }
            }
        }

        return index.exists
    }

    operator fun get(value: T): T? {
        val nodeIndex = findNodeIndex(value)
        loadNode(nodeIndex)
        val node = if (nodeIndex == -1) lowest else nodes[nodeIndex]
        val index = node.findIndex(value)
        return if(index.exists) node[index.position] else null
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var index = -1
            var inner = lowest.iterator()

            override fun hasNext(): Boolean {
                while (index < nodes.size - 1) {
                    if (inner.hasNext()) {
                        return true
                    } else {
                        index++
                        inner = nodes[index].iterator()
                    }
                }
                return inner.hasNext()
            }

            override fun next(): T = inner.next()
        }
    }

    private fun findNodeIndex(value: T): Int {
        if (nodes.size == 0) {
            return -1
        }

        var low = 0
        var high = nodes.size - 1

        var mid = 0
        var greater = false

        while (low <= high) {
            mid = (low + high) ushr 1
            val cmp = comparator.compare(value, nodes[mid].min)
            if (cmp == 0) {
                return mid
            } else if (cmp > 0) {
                low = mid + 1
                greater = true
            } else {
                high = mid - 1
                greater = false
            }
        }
        return if (greater) mid else mid -1
    }


    private fun putNode(index: Int, node: BTreeNode<T>) {
        @Suppress("UNCHECKED_CAST")
        val new = arrayOfNulls<BTreeNode<T>>(nodes.size + 1) as Array<BTreeNode<T>>

        when (index) {
            -1 -> {
                System.arraycopy(nodes, 0, new, 1, nodes.size)
                new[0] = lowest
                lowest = node
            }
            0 -> {
                System.arraycopy(nodes, 0, new, 1, nodes.size)
                new[0] = node
            }
            else -> {
                System.arraycopy(nodes, 0, new, 0, index)
                System.arraycopy(nodes, index, new, index + 1, nodes.size - index)
                new[index] = node
            }
        }

        nodes = new
    }

    private fun loadNode(index: Int) {
    }
}