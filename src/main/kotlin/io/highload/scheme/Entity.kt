package io.highload.scheme

/**
 *
 */
abstract class Entity(size: Int) {
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
}