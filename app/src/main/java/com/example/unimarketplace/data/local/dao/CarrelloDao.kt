package com.example.unimarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unimarketplace.data.local.entity.CarrelloEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarrelloDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticolo(carrelloEntity: CarrelloEntity): Long

    @Delete
    suspend fun deleteArticolo(carrelloEntity: CarrelloEntity)

    @Query("SELECT * FROM carrello WHERE utente_id = :utenteId")
    fun getCarrelloByUtente(utenteId: Long): Flow<List<CarrelloEntity>>

    @Query("DELETE FROM carrello WHERE utente_id = :utenteId AND annuncio_id = :annuncioId")
    suspend fun deleteByUtenteEAnnuncio(utenteId: Long, annuncioId: Long): Int

    @Query("DELETE FROM carrello WHERE utente_id = :utenteId")
    suspend fun svuotaCarrello(utenteId: Long)
}
