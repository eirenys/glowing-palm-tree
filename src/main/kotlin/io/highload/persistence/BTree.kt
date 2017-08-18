package io.highload.persistence

/**
 *
 */
class BTree<K, V>(val comparator: Comparator<K>, val nodeSize: Int = 64) : Iterable<Pair<K, V>> {
    private var delimiterKeys: Array<Any> = emptyArray()
    private var nodes: Array<BTreeNode<K, V>> = Array(1, { BTreeNode<K, V>(comparator, nodeSize) })

    fun put(key: K, value: V): Boolean {
        val node = findNode(key)
        node
        return false
    }

    override fun iterator(): Iterator<Pair<K, V>> {
//        return object : Iterator<Pair<K, V>> {
//            var index = 0
//
//            override fun hasNext(): Boolean = (index < size)
//
//            override fun next(): Pair<K, V> {
//                val node = nodes[index]!!
//                index++
//                return node.key to node.value
//            }
//        }
        TODO()
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