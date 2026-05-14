package com.example.unimarketplace.data.local.dao

import androidx.room.*
import com.example.unimarketplace.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifiche WHERE user_id = :userId ORDER BY timestamp DESC")
    fun getNotificationsByUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifiche WHERE user_id = :userId AND is_read = 0")
    fun getUnreadCount(userId: Long): Flow<Int>

    @Query("UPDATE notifiche SET is_read = 1 WHERE user_id = :userId AND is_read = 0")
    suspend fun markAllAsRead(userId: Long)

    @Query("DELETE FROM notifiche WHERE user_id = :userId")
    suspend fun deleteAllByUser(userId: Long)
}