package com.example.unimarketplace.domain.repository

import com.example.unimarketplace.domain.model.Annuncio
import kotlinx.coroutines.flow.Flow

interface AnnuncioRepository {
    suspend fun getAnnuncioById(id: Long): Annuncio?
    fun getAllAnnunci(): Flow<List<Annuncio>>
    fun getAnnunciByCategoria(categoria: String): Flow<List<Annuncio>>
    fun getAnnunciByVenditore(venditoreId: Long): Flow<List<Annuncio>>
    fun searchAnnunci(query: String): Flow<List<Annuncio>>
    suspend fun insertAnnuncio(annuncio: Annuncio): Long
    suspend fun updateAnnuncio(annuncio: Annuncio)
    suspend fun deleteAnnuncio(annuncio: Annuncio)
}
