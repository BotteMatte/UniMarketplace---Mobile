package com.example.unimarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.unimarketplace.data.local.entity.AnnuncioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnuncioDao {
    @Insert
    suspend fun insertAnnuncio(annuncioEntity: AnnuncioEntity): Long

    @Update
    suspend fun updateAnnuncio(annuncioEntity: AnnuncioEntity)

    @Delete
    suspend fun deleteAnnuncio(annuncioEntity: AnnuncioEntity)

    @Query("SELECT * FROM annunci WHERE id = :id")
    suspend fun getAnnuncioById(id: Long): AnnuncioEntity?

    @Query("SELECT * FROM annunci ORDER BY data_pubblicazione DESC")
    fun getAllAnnunci(): Flow<List<AnnuncioEntity>>

    @Query("SELECT * FROM annunci WHERE categoria = :categoria ORDER BY data_pubblicazione DESC")
    fun getAnnunciByCategoria(categoria: String): Flow<List<AnnuncioEntity>>

    @Query("SELECT * FROM annunci WHERE venditore_id = :venditoreId ORDER BY data_pubblicazione DESC")
    fun getAnnunciByVenditore(venditoreId: Long): Flow<List<AnnuncioEntity>>

    @Query("SELECT * FROM annunci WHERE titolo LIKE '%' || :query || '%' OR descrizione LIKE '%' || :query || '%' ORDER BY data_pubblicazione DESC")
    fun searchAnnunci(query: String): Flow<List<AnnuncioEntity>>
}
