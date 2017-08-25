package io.highload.web

/**
 *
 */
val HTTP_OK = "HTTP/1.1 200 OK\r\n".toByteArray()
val HTTP_ERROR = "HTTP/1.1 400 Error\r\n".toByteArray()
val HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n".toByteArray()
val CONTENT_TYPE_JSON = "Content-Type: application/json;charset=utf-8\r\n".toByteArray()
val CONTENT_LENGTH = "Content-Length: ".toByteArray()
val LINE = "\r\n".toByteArray()
val LINELINE = "\r\n\r\n".toByteArray()

val POST = "POST".toByteArray()
val GET = "GET".toByteArray()

val EMPTY_JSON = "{}".toByteArray()

val VISITS = "{\"visits\":[".toByteArray()
val DELIMITER = ",".toByteArray()
val VISITS_END = "]}".toByteArray()