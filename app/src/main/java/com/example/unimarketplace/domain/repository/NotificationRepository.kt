package com.example.unimarketplace.domain.repository

import com.example.unimarketplace.data.local.dao.NotificationDao
import com.example.unimarketplace.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {

    fun getNotifications(userId: Long): Flow<List<NotificationEntity>> = dao.getNotificationsByUser(userId)

    fun getUnreadCount(userId: Long): Flow<Int> = dao.getUnreadCount(userId)

    suspend fun addNotification(
        userId: Long,
        title: String,
        message: String,
        type: String,
        relatedId: Long? = null
    ) {
        dao.insert(
            NotificationEntity(
                userId = userId,
                title = title,
                message = message,
                type = type,
                timestamp = System.currentTimeMillis(),
                relatedId = relatedId
            )
        )
    }

    suspend fun markAllAsRead(userId: Long) = dao.markAllAsRead(userId)

    suspend fun deleteAll(userId: Long) = dao.deleteAllByUser(userId)
}