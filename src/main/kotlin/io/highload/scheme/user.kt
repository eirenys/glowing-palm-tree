package io.highload.scheme

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 */
class Users {
    var users = emptyList<User>()
}

class User {
    private var fields = 0

    var id: Int = 0
        set(value) {
            fields = fields or 1
            field = value
        }

    var email: String = ""
        set(value) {
            fields = fields or 2
            field = value
        }

    @JsonProperty("first_name")
    var firstName: String = ""
        set(value) {
            fields = fields or 4
            field = value
        }

    @JsonProperty("last_name")
    var lastName: String = ""
        set(value) {
            fields = fields or 8
            field = value
        }

    var gender: Char = 'm'
        set(value) {
            fields = fields or 16
            field = value
        }

    @JsonProperty("birth_date")
    var birthDate: Long = 0
        set(value) {
            fields = fields or 32
            field = value
        }

    fun checkEntity() {
        check(fields == ALL)
    }

    fun modify(other: User) {
        other.ifHaveValue(1) { id = other.id}
        other.ifHaveValue(2) { email = other.email}
        other.ifHaveValue(4) { firstName = other.firstName}
        other.ifHaveValue(8) { lastName = other.lastName}
        other.ifHaveValue(16) { gender = other.gender}
        other.ifHaveValue(32) { birthDate = other.birthDate}
    }

    private inline fun ifHaveValue(flag: Int, block: () -> Unit) {
        if ((fields and flag) == flag) {
            block()
        }
    }

    companion object {
        private const val ALL = 0x3F
    }
}