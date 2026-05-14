package com.example.unimarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "notifiche")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    val title: String,
    val message: String,
    val type: String, // "sale", "badge", "favorite", "cart"
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    val timestamp: Long,
    @ColumnInfo(name = "related_id") val relatedId: Long? = null
)