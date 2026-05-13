package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.PreferitiDao
import com.example.unimarketplace.data.local.entity.PreferitiEntity
import com.example.unimarketplace.domain.repository.PreferitiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferitiRepositoryImpl(private val preferitiDao: PreferitiDao) : PreferitiRepository {

    override suspend fun togglePreferito(utenteId: Long, annuncioId: Long) {
        val isFav = preferitiDao.isPreferito(utenteId, annuncioId).first()
        if (isFav) {
            preferitiDao.deleteByUtenteEAnnuncio(utenteId, annuncioId)
        } else {
            preferitiDao.insertPreferito(PreferitiEntity(utenteId = utenteId, annuncioId = annuncioId))
        }
    }

    override fun isPreferito(utenteId: Long, annuncioId: Long): Flow<Boolean> {
        return preferitiDao.isPreferito(utenteId, annuncioId)
    }

    override fun getPreferitiByUtente(utenteId: Long): Flow<List<Long>> {
        return preferitiDao.getPreferitiByUtente(utenteId).map { list ->
            list.map { it.annuncioId }
        }
    }
}
