package com.example.unimarketplace.domain.repository

import com.example.unimarketplace.data.local.entity.PreferitiEntity
import kotlinx.coroutines.flow.Flow

interface PreferitiRepository {
    suspend fun togglePreferito(utenteId: Long, annuncioId: Long)
    fun isPreferito(utenteId: Long, annuncioId: Long): Flow<Boolean>
    fun getPreferitiByUtente(utenteId: Long): Flow<List<Long>>
}
