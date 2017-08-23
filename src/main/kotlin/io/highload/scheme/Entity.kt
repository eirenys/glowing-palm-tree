package io.highload.scheme

/**
 *
 */
abstract class Entity(size: Int) {
    protected val values = arrayOfNulls<Any?>(size)

    fun tryGet(index: Int): Any? = values[index]

    operator fun set(index: Int, value: Any?) {
        values[index] = value
    }

    fun checkEntity() {
        values.forEach { checkNotNull(it) }
    }

    fun modify(entity: Entity) {
        for (index in 0..values.size - 1) {
            values[index] = entity.values[index]
        }
    }
}