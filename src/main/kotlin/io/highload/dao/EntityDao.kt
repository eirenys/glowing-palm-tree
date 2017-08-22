package io.highload.dao

import io.highload.scheme.Location
import io.highload.scheme.User
import io.highload.scheme.Visit
import io.highload.scheme.Visit2
import java.math.BigDecimal

/**
 *
 */
abstract class EntityDao {
    abstract suspend fun insert(user: User)

    abstract suspend fun insert(location: Location)

    abstract suspend fun insert(visit: Visit)

    abstract suspend fun updateUser(id: Int, block: () -> User): User?

    abstract suspend fun updateLocation(id: Int, block: () -> Location): Location?

    abstract suspend fun updateVisit(id: Int, block: () -> Visit): Visit?

    abstract suspend fun findUser(id: Int): User?

    abstract suspend fun findLocation(id: Int): Location?

    abstract suspend fun findVisit(id: Int): Visit?

    abstract suspend fun findVisits(userId: Int, country: String?, fromDate: Long?, toDate: Long?, toDistance: Int?): List<Visit2>?

    abstract suspend fun avg(locationId: Int, fromDate: Long?, toDate: Long?, fromBirth: Long?, toBirth: Long?, gender: Char?): BigDecimal?
}