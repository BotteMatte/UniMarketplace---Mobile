package com.example.unimarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

@Entity(tableName = "annunci",
    foreignKeys = [ForeignKey(entity = UtenteEntity::class, parentColumns = ["id"], childColumns = ["venditore_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("venditore_id"), Index("categoria")]
)
data class AnnuncioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titolo: String,
    val descrizione: String,
    val prezzo: Double,
    val categoria: String,
    val condizioni: String,
    val immagini: List<String> = emptyList(),
    @ColumnInfo(name = "data_pubblicazione") val dataPubblicazione: Long,
    @ColumnInfo(name = "venditore_id") val venditoreId: Long,
    @ColumnInfo(name = "venditore_nome") val venditoreNome: String,
    @ColumnInfo(name = "is_venduto") val isVenduto: Boolean = false
)
