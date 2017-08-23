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

    override fun toString(): String = "{\"id\":$id,\"email\":\"$email\",\"first_name\":\"$firstName\"," +
            "\"last_name\":\"$lastName\",\"gender\":\"$gender\",\"birth_date\":$birthDate}"
}