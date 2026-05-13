package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.CarrelloDao
import com.example.unimarketplace.data.local.entity.CarrelloEntity
import com.example.unimarketplace.domain.repository.CarrelloRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CarrelloRepositoryImpl(private val carrelloDao: CarrelloDao) : CarrelloRepository {

    override suspend fun aggiungiAlCarrello(utenteId: Long, annuncioId: Long) {
        carrelloDao.insertArticolo(CarrelloEntity(utenteId = utenteId, annuncioId = annuncioId))
    }

    override suspend fun rimuoviDalCarrello(utenteId: Long, annuncioId: Long) {
        carrelloDao.deleteByUtenteEAnnuncio(utenteId, annuncioId)
    }

    override fun getCarrelloByUtente(utenteId: Long): Flow<List<Long>> {
        return carrelloDao.getCarrelloByUtente(utenteId).map { list ->
            list.map { it.annuncioId }
        }
    }

    override suspend fun svuotaCarrello(utenteId: Long) {
        carrelloDao.svuotaCarrello(utenteId)
    }
}
