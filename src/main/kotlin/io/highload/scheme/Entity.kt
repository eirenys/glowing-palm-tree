package io.highload.scheme

/**
 *
 */
abstract class Entity(size: Int) {
    protected val values = arrayOfNulls<Any?>(size)

    abstract fun toByteChain(next: ByteChain?): ByteChain

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

    override fun toString(): String = toByteChain(null).toString()

    protected fun toByteArr(int: Int) = Integer.toString(int).toByteArray()

    protected fun toByteArr(ch: Char) = when(ch) {
        'm' -> MALE
        'f' -> FEMALE
        else -> FEMALE
    }
}