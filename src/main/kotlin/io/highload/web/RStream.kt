package io.highload.web

import java.io.OutputStream

/**
 *
 */
class RStream(val out: OutputStream, val byteArray: ByteArray) {
    private var offset = 0

    fun write(arr: ByteArray) {
        var off = 0
        while (arr.size > off) {
            val length = minOf(arr.size - off, byteArray.size - offset)
            System.arraycopy(arr, off, byteArray, offset, length)
            off += length
            offset += length
            if (offset == byteArray.size) {
                out.write(byteArray)
                out.flush()
                offset = 0
            }
        }
    }

    fun flush() {
        out.write(byteArray, 0, offset)
        out.flush()
        offset = 0
    }
}