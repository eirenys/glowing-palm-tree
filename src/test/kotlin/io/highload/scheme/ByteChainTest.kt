package io.highload.scheme

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 *
 */
class ByteChainTest {
    val test1 = "test1".toByteArray()
    val test2 = "test2".toByteArray()

    @Test
    fun chainTest() {
        val res = ByteChain(test1, null)
        assertEquals("test1", res.toString())
    }

    @Test
    fun chainLinkTest() {
        val res = ByteChain(test1, null).link(test2)
        assertEquals("test2test1", res.toString())
    }

    @Test
    fun chainSizeTest() {
        val res = ByteChain(test1, null).link(test2)
        assertEquals("test2test1".length, res.size)
    }
}