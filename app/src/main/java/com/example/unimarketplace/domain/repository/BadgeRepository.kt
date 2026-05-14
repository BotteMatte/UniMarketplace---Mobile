package com.example.unimarketplace.domain.repository

import com.example.unimarketplace.domain.model.Badge
import com.example.unimarketplace.domain.model.BadgeType
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getBadgesByUtente(userId: Long): Flow<List<Badge>>
    suspend fun checkAndAwardBadge(userId: Long, badgeType: BadgeType): Boolean
    suspend fun hasBadge(userId: Long, badgeId: String): Boolean
}
