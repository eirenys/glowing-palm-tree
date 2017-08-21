package io.highload.scheme

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 */
class Users {
    var users = emptyList<User>()
}

class User {
    var id: Int = 0
    var email: String = ""
    @JsonProperty("first_name") var firstName: String = ""
    @JsonProperty("last_name") var lastName: String = ""
    var gender: Char = 'm'
    @JsonProperty("birth_date") var birthDate: Long = 0
}