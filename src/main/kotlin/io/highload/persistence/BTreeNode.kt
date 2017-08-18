package io.highload.persistence

import java.util.*

/**
 *
 */
class BTreeNode<K, V>(val comparator: Comparator<K>, capacity: Int) : Iterable<Pair<K, V>> {
    private var size = 0
    private val keys: Array<Any?> = arrayOfNulls(capacity)
    private val values: Array<Any?> = arrayOfNulls(capacity)

    override fun iterator(): Iterator<Pair<K, V>> {
        return object : Iterator<Pair<K, V>> {
            var index = 0
            override fun hasNext(): Boolean = (index < size)

            @Suppress("UNCHECKED_CAST")
            override fun next(): Pair<K, V> {
                val i = index
                index++
                return keys[i] as K to values[i] as V
            }
        }
    }

    fun findIndex(key: K): Int {
        return 0
    }

    fun insert(index: Int, key: K, value: V) {
        if (index > size || size == keys.size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        val buffer = arrayOfNulls<Any?>(size)

        System.arraycopy(keys, index, buffer, 0, size - index)
        keys[index] = key
        System.arraycopy(buffer, 0, keys, index + 1, size - index)

        System.arraycopy(values, index, buffer, 0, size - index)
        values[index] = value
        System.arraycopy(buffer, 0, values, index + 1, size - index)

        size++
    }
}