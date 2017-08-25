package io.highload.scheme

/**
 *
 */
class User : Entity(6) {
    val id: Int get() = values[0] as Int
    val email: String get() = values[1] as String
    val firstName: String get() = values[2] as String
    val lastName: String get() = values[3] as String
    val gender: Char get() = values[4] as Char
    val birthDate: Int get() = values[5] as Int

    override fun toByteChain(next: ByteChain?): ByteChain = ByteChain(JSON_END, next)
            .link(toByteArr(birthDate))
            .link(BIRTH_DATE)
            .link(toByteArr(gender))
            .link(GENDER)
            .link(lastName.toByteArray())
            .link(LAST_NAME)
            .link(firstName.toByteArray())
            .link(FIRST_NAME)
            .link(email.toByteArray())
            .link(EMAIL)
            .link(toByteArr(id))
            .link(JSON_START)
}