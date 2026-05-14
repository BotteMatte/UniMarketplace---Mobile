package com.example.unimarketplace.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_badges",
    primaryKeys = ["user_id", "badge_id"],
    foreignKeys = [
        ForeignKey(
            entity = UtenteEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class BadgeEntity(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "badge_id") val badgeId: String,
    val timestamp: Long
)
