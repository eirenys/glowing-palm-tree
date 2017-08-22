package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.*

/**
 *
 */
class StubDao : EntityDao() {
    private val mutex = Mutex()
    val users = TreeMap<Int, User>()
    val locations = TreeMap<Int, Location>()
    val visits = TreeMap<Int, Visit>()

    override suspend fun insert(user: User): Unit = mutex.withLock {
        if (user.id in users) {
            error("already inserted")
        }
        user.checkEntity()
        users.put(user.id, user)
    }

    override suspend fun insert(location: Location): Unit = mutex.withLock {
        if (location.id in locations) {
            error("already inserted")
        }
        location.checkEntity()
        locations.put(location.id, location)
    }

    override suspend fun insert(visit: Visit): Unit = mutex.withLock {
        if (visit.id in visits) {
            error("already inserted")
        }
        visit.checkEntity()
        visits.put(visit.id, visit)
    }

    override suspend fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[id]?.also {
            users.remove(it.id)
            val entity = block()
            it.modify(entity)
            users.put(it.id, it)
        }
    }

    suspend override fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[id]?.also {
            locations.remove(it.id)
            val entity = block()
            it.modify(entity)
            locations.put(it.id, it)
        }
    }

    suspend override fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[id]?.also {
            visits.remove(it.id)
            val entity = block()
            it.modify(entity)
            visits.put(it.id, it)
        }
    }

    override suspend fun findUser(id: Int): User? = mutex.withLock {
        return users[id]
    }

    override suspend fun findLocation(id: Int): Location? = mutex.withLock {
        return locations[id]
    }

    override suspend fun findVisit(id: Int): Visit? = mutex.withLock {
        return visits[id]
    }

}