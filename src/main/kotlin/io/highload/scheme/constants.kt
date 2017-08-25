package io.highload.scheme

/**
 *
 */

val JSON_START0 = "{".toByteArray()

val JSON_START = "{\"id\":".toByteArray()

val EMAIL = ",\"email\":\"".toByteArray()
val FIRST_NAME = "\",\"first_name\":\"".toByteArray()
val LAST_NAME = "\",\"last_name\":\"".toByteArray()
val GENDER = "\",\"gender\":\"".toByteArray()
val BIRTH_DATE = "\",\"birth_date\":".toByteArray()

val PLACE = ",\"place\":\"".toByteArray()
val COUNTRY = "\",\"country\":\"".toByteArray()
val CITY = "\",\"city\":\"".toByteArray()
val DISTANCE = "\",\"distance\":".toByteArray()

val LOCATION = ",\"location\":".toByteArray()
val USER = ",\"user\":".toByteArray()
val VISITED_AT = ",\"visited_at\":".toByteArray()
val MARK = ",\"mark\":".toByteArray()

val PLACE2 = "\"place\":\"".toByteArray()
val MARK2 = "\",\"mark\":".toByteArray()

val JSON_END = "}".toByteArray()

val MALE = "m".toByteArray()
val FEMALE = "f".toByteArray()


fun toByteArr(int: Int) = Integer.toString(int).toByteArray()

fun toByteArr(ch: Char) = when(ch) {
    'm' -> MALE
    'f' -> FEMALE
    else -> FEMALE
}