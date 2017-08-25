package io.highload.scheme

/**
 *
 */
fun makeUser(id: Int) = User().also { user ->
    user[0] = id
    user[1] = "email $id"
    user[2] = "firstName $id"
    user[3] = "lastName $id"
    user[4] = 'm'
    user[5] = id + 1
}

fun makeLocation(id: Int) = Location().also { location ->
    location[0] = id
    location[1] = "place $id"
    location[2] = "country $id"
    location[3] = "city $id"
    location[4] = id + 1
}


fun makeVisit(id: Int, locId: Int, userId: Int, distance: Int, mark: Int) = Visit().also { vis ->
    vis[0] = id
    vis[1] = locId
    vis[2] = userId
    vis[3] = distance
    vis[4] = mark
}