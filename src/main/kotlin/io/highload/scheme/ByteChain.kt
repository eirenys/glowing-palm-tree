package io.highload.scheme

/**
 *
 */
class ByteChain(val array: ByteArray, val next: ByteChain? = null, val size: Int = array.size) {
    override fun toString(): String = String(toByteArray())

    fun link(previous: ByteArray): ByteChain {
        return ByteChain(previous, this, size + previous.size)
    }

    fun toByteArray(): ByteArray = if (next == null) {
        array
    } else {
        val result = ByteArray(size)
        var off = 0
        var n: ByteChain? = this
        while (n != null) {
            val size = n.array.size
            System.arraycopy(n.array, 0, result, off, size)
            off += size
            n = n.next
        }
        result
    }
}