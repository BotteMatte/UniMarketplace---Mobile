package com.example.unimarketplace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utenti")
data class UtenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val email: String,
    val matricola: String
)
