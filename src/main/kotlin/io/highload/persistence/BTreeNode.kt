package io.highload.persistence

import java.io.RandomAccessFile
import java.util.*

/**
 *
 */
class BTreeNode<T>(val comparator: Comparator<T>, capacity: Int) : Iterable<T> {
    private var size = 0
    private val values = arrayOfNulls<Any?>(capacity)
    private val buffer = arrayOfNulls<Any?>(capacity)

    val freeSpace: Int get() = values.size - size
    val min: T get() = this[0]

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var index = 0
            override fun hasNext(): Boolean = (index < size)

            override fun next(): T {
                val i = index
                index++
                return this@BTreeNode[i]
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(index: Int): T = values[index] as T

    fun findIndex(value: T): KeyIndex {
        var low = 0
        var high = size - 1

        var mid = 0
        var greater = false

        while (low <= high) {
            mid = (low + high) ushr 1
            val cmp = comparator.compare(value, this[mid])
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

    fun insert(index: Int, value: T) {
        if (index > size || size == values.size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        System.arraycopy(values, index, buffer, 0, size - index)
        values[index] = value
        System.arraycopy(buffer, 0, values, index + 1, size - index)

        size++
    }

    fun replace(index: Int, value: T) {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        values[index] = value
    }

    fun remove(index: Int) {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        System.arraycopy(values, index + 1, buffer, 0, size - index - 1)
        System.arraycopy(buffer, 0, values, index, size - index - 1)

        size--
    }

    /**
     * Разделение нода, текущий нод получает значения до индекса не включительно, а новый всё остальное
     * При идексе == 0, текущий станет пустым, а новый получит все элементы
     */
    fun split(index: Int): BTreeNode<T> {
        if (index >= size) {
            throw ArrayIndexOutOfBoundsException(index)
        }

        val new = BTreeNode(comparator, values.size)
        System.arraycopy(values, index, new.values, 0, size - index)
        new.size = size - index
        size = index

        return new
    }

    fun later() {
        val file = RandomAccessFile("asfas", "rwd")
        file.read()
    }

    override fun toString(): String = min.toString()
}