package io.highload.persistence

/**
 *
 */
class BTree<K, V>(val comparator: Comparator<K>, val nodeSize: Int = 64) : Iterable<Pair<K, V>> {
    private var nodes: Array<BTreeNode<K, V>> = Array(1, { BTreeNode<K, V>(comparator, nodeSize) })

    fun put(key: K, value: V): Boolean {
        val nodeIndex = findNodeIndex(key)
        val node = nodes[nodeIndex]
        val index = node.findIndex(key)
        if (index.exists) {
            node.replace(index.position, key, value)
        } else {
            if (node.freeSpace > 0) {
                node.insert(index.position, key, value)
            } else {
                if (index.position == 0) {
                    val new = BTreeNode<K, V>(comparator, nodeSize)
                    new.insert(0, key, value)
                    putNode(nodeIndex, new)
                } else if (index.position == nodeSize) {
                    val new = BTreeNode<K, V>(comparator, nodeSize)
                    new.insert(0, key, value)
                    putNode(nodeIndex + 1, new)
                } else {
                    val splitted = node.split(index.position)
                    putNode(nodeIndex + 1, splitted)
                    val index2 = node.findIndex(key)
                    assert(node.freeSpace > 0)
                    node.insert(index2.position, key, value)
                }
            }
        }

        return index.exists
    }

    override fun iterator(): Iterator<Pair<K, V>> {
        return object : Iterator<Pair<K, V>> {
            var index = 0
            var inner = nodes[0].iterator()

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

            override fun next(): Pair<K, V> = inner.next()
        }
    }

    private fun findNodeIndex(key: K): Int {
        if (nodes.size == 1) {
            return 0
        }
        TODO()
    }


    private fun putNode(index: Int, node: BTreeNode<K, V>) {
        @Suppress("UNCHECKED_CAST")
        val new = arrayOfNulls<BTreeNode<K, V>>(nodes.size + 1) as Array<BTreeNode<K, V>>

        System.arraycopy(nodes, 0, new, 0, index)
        System.arraycopy(nodes, index, new, 0, nodes.size - index)
        new[index] = node
        nodes = new
    }
}