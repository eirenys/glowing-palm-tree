package io.highload.persistence

import java.util.*

/**
 *
 */
class BTreeNode<K, V>(val comparator: Comparator<K>, capacity: Int) : Iterable<Pair<K, V>> {
    private var size = 0
    private val keys = arrayOfNulls<Any?>(capacity)
    private val values = arrayOfNulls<Any?>(capacity)
    private val buffer = arrayOfNulls<Any?>(capacity)

    val freeSpace: Int get() = keys.size - size

    override fun iterator(): Iterator<Pair<K, V>> {
        return object : Iterator<Pair<K, V>> {
            var index = 0
            override fun hasNext(): Boolean = (index < size)

            override fun next(): Pair<K, V> {
                val i = index
                index++
                return getKey(i) to getValue(i)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getKey(index: Int): K = keys[index] as K

    @Suppress("UNCHECKED_CAST")
    fun getValue(index: Int): V = values[index] as V

    fun findIndex(key: K): KeyIndex {
        var low = 0
        var high = size - 1

        var mid = 0
        var greater = false

        while (low <= high) {
            mid = (low + high) ushr 1
            val cmp = comparator.compare(key, getKey(mid))
            if (cmp == 0) {
                return KeyIndex(true, mid)
            } else if (cmp > 0) {
                low = mid + 1
                greater = true
            } else {
                high = mid - 1
                greater = false
            }
        }
        val idx = if (greater) mid + 1 else mid
        return KeyIndex(false, idx)
    }

    fun insert(index: Int, key: K, value: V) {
        if (index > size || size == keys.size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        System.arraycopy(keys, index, buffer, 0, size - index)
        keys[index] = key
        System.arraycopy(buffer, 0, keys, index + 1, size - index)

        System.arraycopy(values, index, buffer, 0, size - index)
        values[index] = value
        System.arraycopy(buffer, 0, values, index + 1, size - index)

        size++
    }

    fun replace(index: Int, key: K, value: V) {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        keys[index] = key
        values[index] = value
    }

    fun remove(index: Int) {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        System.arraycopy(keys, index + 1, buffer, 0, size - index - 1)
        System.arraycopy(buffer, 0, keys, index, size - index - 1)
        System.arraycopy(values, index + 1, buffer, 0, size - index - 1)
        System.arraycopy(buffer, 0, values, index, size - index - 1)

        size--
    }

    /**
     * Разделение нода, текущий нод получает значения до индекса не включительно, а новый всё остальное
     * При идексе == 0, текущий станет пустым, а новый получит все элементы
     */
    fun split(index: Int): BTreeNode<K, V> {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        val new = BTreeNode<K, V>(comparator, keys.size)
        System.arraycopy(keys, index, new.keys, 0, size - index)
        System.arraycopy(values, index, new.values, 0, size - index)
        new.size = size - index
        size = index

        return new
    }
}