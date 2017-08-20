package io.highload.persistence

/**
 *
 */
class BTree<K, V>(val comparator: Comparator<K>, val nodeSize: Int = 64) : Iterable<Pair<K, V>> {
    private var delimiterKeys: Array<Any> = emptyArray()
    private var nodes: Array<BTreeNode<K, V>> = Array(1, { BTreeNode<K, V>(comparator, nodeSize) })

    fun put(key: K, value: V): Boolean {
        val node = findNode(key)
        val index = node.findIndex(key)
        if (index.exists) {
            node.replace(index.position, key, value)
        } else {
            node.insert(index.position, key, value)
        }
        return false
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

    @Suppress("UNCHECKED_CAST")
    private fun getKey(index: Int): K = delimiterKeys[index] as K

    private fun findNode(key: K): BTreeNode<K, V> {
        if (delimiterKeys.isEmpty()) {
            return nodes[0]
        }
        TODO()
    }


}