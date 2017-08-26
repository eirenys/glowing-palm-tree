package io.highload.persistence

import kotlinx.coroutines.experimental.sync.Mutex
import java.io.*
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 *
 */
class BTreeNode<T>(val comparator: Comparator<T>, val capacity: Int) : Iterable<T> {
    private var values = arrayOfNulls<Any?>(capacity)
    private var buffer = arrayOfNulls<Any?>(capacity)
    private var isload = true
    private var savedMin: T? = null
    private val file: File by lazy {
        val id = "temp" + nextId.getAndIncrement()
        File.createTempFile(id, id)
    }

    val mutex = Mutex()
    var size = 0
    val loaded get() = isload
    var accessTime = System.currentTimeMillis()
    val freeSpace: Int get() = values.size - size
    val min: T get() = if (isload) this[0] else savedMin!!

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

    fun unload() {
        if (loaded) {
            ObjectOutputStream(FileOutputStream(file)).use {
                it.writeInt(size)
                for (i in 0..size - 1) {
                    it.writeObject(this[i])
                }

                savedMin = this[0]
                isload = false
                values = emptyArray()
                buffer = emptyArray()
                accessTime = Long.MAX_VALUE
            }
        }
    }

    fun load() {
        if (!loaded) {
            ObjectInputStream(FileInputStream(file)).use {
                size = it.readInt()
                values = arrayOfNulls(capacity)
                buffer = arrayOfNulls(capacity)
                for (i in 0..size - 1) {
                    values[i] = it.readObject()
                }
                isload = true
            }
        }
    }

    override fun toString(): String = min.toString()

    companion object {
        private val nextId = AtomicLong()
    }
}