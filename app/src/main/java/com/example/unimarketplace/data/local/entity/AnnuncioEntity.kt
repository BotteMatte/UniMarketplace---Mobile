package com.example.unimarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "annunci",
    foreignKeys = [ForeignKey(entity = UtenteEntity::class, parentColumns = ["id"], childColumns = ["venditoreId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("venditoreId"), Index("categoria")]
)
data class AnnuncioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titolo: String,
    val descrizione: String,
    val prezzo: Double,
    val categoria: String,
    val condizioni: String,
    val immagineUrl: String?,
    val dataPubblicazione: Long,
    val venditoreId: Long,
    val venditoreNome: String,
    val isVenduto: Boolean = false
)
