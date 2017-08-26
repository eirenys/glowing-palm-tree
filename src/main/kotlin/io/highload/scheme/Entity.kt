package io.highload.scheme

import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

/**
 *
 */
abstract class Entity(size: Int) : Externalizable {
    protected val values = arrayOfNulls<Any?>(size)

    open fun toByteChain(next: ByteChain?): ByteChain = ByteChain(toString().toByteArray(), next)

    fun tryGet(index: Int): Any? = values[index]

    operator fun set(index: Int, value: Any?) {
        values[index] = value
    }

    fun checkEntity() {
        values.forEach { checkNotNull(it) }
    }

    fun modify(entity: Entity) {
        for (index in 0..values.size - 1) {
            val newValue = entity.values[index]
            if (newValue != null) {
                values[index] = newValue
            }
        }
    }

    fun toByteArray(): ByteArray = toByteChain(null).toByteArray()

    override fun readExternal(inp: ObjectInput) {
        for (i in 0..values.size - 1) {
            values[i] = inp.readObject()
        }
    }

    override fun writeExternal(out: ObjectOutput) {
        for (i in 0..values.size - 1) {
            out.writeObject(values[i])
        }
    }
}