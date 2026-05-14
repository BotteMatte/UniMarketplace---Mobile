package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.BadgeDao
import com.example.unimarketplace.data.local.entity.BadgeEntity
import com.example.unimarketplace.domain.model.Badge
import com.example.unimarketplace.domain.model.BadgeType
import com.example.unimarketplace.domain.repository.BadgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class BadgeRepositoryImpl(
    private val badgeDao: BadgeDao
) : BadgeRepository {

    override fun getBadgesByUtente(userId: Long): Flow<List<Badge>> {
        return badgeDao.getBadgesByUtente(userId).map { entities ->
            entities.map { entity ->
                Badge(
                    type = BadgeType.entries.find { it.id == entity.badgeId } ?: BadgeType.NOVIZIO,
                    earnedTimestamp = entity.timestamp
                )
            }
        }
    }

    override suspend fun checkAndAwardBadge(userId: Long, badgeType: BadgeType): Boolean {
        if (badgeDao.hasBadge(userId, badgeType.id)) {
            return false
        }
        badgeDao.insertBadge(
            BadgeEntity(
                userId = userId,
                badgeId = badgeType.id,
                timestamp = Date().time
            )
        )
        return true
    }

    override suspend fun hasBadge(userId: Long, badgeId: String): Boolean {
        return badgeDao.hasBadge(userId, badgeId)
    }
}
