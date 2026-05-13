package com.example.unimarketplace.domain.repository

import kotlinx.coroutines.flow.Flow

interface CarrelloRepository {
    suspend fun aggiungiAlCarrello(utenteId: Long, annuncioId: Long)
    suspend fun rimuoviDalCarrello(utenteId: Long, annuncioId: Long)
    fun getCarrelloByUtente(utenteId: Long): Flow<List<Long>>
    suspend fun svuotaCarrello(utenteId: Long)
}
