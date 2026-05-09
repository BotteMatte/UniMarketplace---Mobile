package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.AnnuncioDao
import com.example.unimarketplace.data.mapper.toDomain
import com.example.unimarketplace.data.mapper.toEntity
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
class AnnuncioRepositoryImpl(
    private val annuncioDao: AnnuncioDao
) : AnnuncioRepository {

    override suspend fun getAnnuncioById(id: Long): Annuncio? {
        return annuncioDao.getAnnuncioById(id)?.toDomain()
    }

    override fun getAllAnnunci(): Flow<List<Annuncio>> {
        return annuncioDao.getAllAnnunci().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAnnunciByCategoria(categoria: String): Flow<List<Annuncio>> {
        return annuncioDao.getAnnunciByCategoria(categoria).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAnnunciByVenditore(venditoreId: Long): Flow<List<Annuncio>> {
        return annuncioDao.getAnnunciByVenditore(venditoreId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchAnnunci(query: String): Flow<List<Annuncio>> {
        return annuncioDao.searchAnnunci(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAnnuncio(annuncio: Annuncio): Long {
        return annuncioDao.insertAnnuncio(annuncio.toEntity())
    }

    override suspend fun updateAnnuncio(annuncio: Annuncio) {
        annuncioDao.updateAnnuncio(annuncio.toEntity())
    }

    override suspend fun deleteAnnuncio(annuncio: Annuncio) {
        annuncioDao.deleteAnnuncio(annuncio.toEntity())
    }
}
