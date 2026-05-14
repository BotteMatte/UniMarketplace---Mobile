package com.example.unimarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unimarketplace.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("SELECT * FROM user_badges WHERE user_id = :userId")
    fun getBadgesByUtente(userId: Long): Flow<List<BadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_badges WHERE user_id = :userId AND badge_id = :badgeId)")
    suspend fun hasBadge(userId: Long, badgeId: String): Boolean
}
