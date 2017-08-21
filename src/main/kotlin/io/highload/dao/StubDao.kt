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

    override suspend fun insert(user: User): Boolean = mutex.withLock {
        val contain = user.id in users
        if (!contain) {
            users.put(user.id, user)
        }
        contain
    }

    override suspend fun insert(location: Location): Boolean = mutex.withLock {
        val contain = location.id in locations
        if (!contain) {
            locations.put(location.id, location)
        }
        contain
    }

    override suspend fun insert(visit: Visit): Boolean = mutex.withLock {
        val contain = visit.id in visits
        if (!contain) {
            visits.put(visit.id, visit)
        }
        contain
    }

    override suspend fun updateUser(id: Int, block: () -> User): User? = mutex.withLock {
        users[id]?.let {
            val entity = block()
            entity.id = id
            users.put(id, entity)
        }
    }

    suspend override fun updateLocation(id: Int, block: () -> Location): Location? = mutex.withLock {
        locations[id]?.let {
            val entity = block()
            entity.id = id
            locations.put(id, entity)
        }
    }

    suspend override fun updateVisit(id: Int, block: () -> Visit): Visit? = mutex.withLock {
        visits[id]?.let {
            val entity = block()
            entity.id = id
            visits.put(id, entity)
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