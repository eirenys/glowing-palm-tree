package io.highload.persistence

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 *
 */
class BTree<T>(val comparator: Comparator<T>, val nodeSize: Int = 4096) : Iterable<T> {
    private val mutex = ReentrantLock()
    private var lowest = BTreeNode(comparator, nodeSize)
    private var nodes: Array<BTreeNode<T>> = emptyArray()

    val size: Int get() = this.count()

    fun put(value: T) {
        val nodeIndex = findNodeIndex(value)
        loadNode(nodeIndex)
        val node = getNode(nodeIndex)

        node.mutex.withLock {
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
        }
    }

    fun get(value: T): T? {
        val nodeIndex = findNodeIndex(value)
        loadNode(nodeIndex)
        val node = getNode(nodeIndex)
        val index = node.findIndex(value)
        return if (index.exists) node[index.position] else null
    }

    fun remove(value: T) {
        val nodeIndex = findNodeIndex(value)
        loadNode(nodeIndex)
        val node = getNode(nodeIndex)
        val index = node.findIndex(value)
        if (index.exists) {
            node.remove(index.position)
        }
    }

    fun subMap(left: T, right: T): List<T> {
        val list = mutableListOf<T>()
        val ni1 = findNodeIndex(left)
        val ni2 = findNodeIndex(right)

        loadNode(ni1)
        val node1 = getNode(ni1)
        val ind = node1.findIndex(left).position
        for (j in ind..node1.size - 1) {
            val value = node1[j]
            if (comparator.compare(value, right) >= 0) {
                break
            }
            list.add(value)
        }


        for (i in (ni1 + 1)..ni2) {
            loadNode(i)
            val node = getNode(i)

            for (j in 0..node.size - 1) {
                val value = node[j]
                if (comparator.compare(value, right) >= 0) {
                    break
                }
                list.add(value)
            }
        }
        return list
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
        if (nodes.isEmpty()) {
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
        return if (greater) mid else mid - 1
    }

    private fun getNode(index: Int): BTreeNode<T> = if (index == -1) lowest else nodes[index]

    private fun putNode(index: Int, node: BTreeNode<T>) = mutex.withLock {
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
        ACTIVE_NODES.incrementAndGet()
    }

    private fun loadNode(index: Int) {
        if (index != -1) {
            val node = nodes[index]
            node.accessTime = System.currentTimeMillis()

            if (ACTIVE_NODES.get() > MAX_ACTIVE_NODES) {
                nodes.filter { it.loaded }.sortedBy { it.accessTime }.take(8).forEach {
                    it.mutex.withLock {
                        if (it.loaded) {
                            it.unload()
                            ACTIVE_NODES.decrementAndGet()
                        }
                    }
                }
            }
            if (!node.loaded) {
                node.mutex.withLock {
                    if (!node.loaded) {
                        node.load()
                        ACTIVE_NODES.incrementAndGet()
                    }
                }
            }
        }
    }

    companion object {
        private val MAX_ACTIVE_NODES = 512
        private val ACTIVE_NODES = AtomicInteger()
    }
}