package com.example.unimarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(tableName = "preferiti",
    foreignKeys = [
        ForeignKey(entity = UtenteEntity::class, parentColumns = ["id"], childColumns = ["utenteId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AnnuncioEntity::class, parentColumns = ["id"], childColumns = ["annuncioId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["utenteId", "annuncioId"], unique = true)]
)
data class PreferitiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "utente_id") val utenteId: Long,
    @ColumnInfo(name = "annuncio_id") val annuncioId: Long
)
