package com.example.unimarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.unimarketplace.data.local.entity.PreferitiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferitiDao {
    @Insert
    suspend fun insertPreferito(preferitiEntity: PreferitiEntity): Long

    @Delete
    suspend fun deletePreferito(preferitiEntity: PreferitiEntity)

    @Query("SELECT * FROM preferiti WHERE utente_id = :utenteId")
    fun getPreferitiByUtente(utenteId: Long): Flow<List<PreferitiEntity>>

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END as result FROM preferiti WHERE utente_id = :utenteId AND annuncio_id = :annuncioId")
    fun isPreferito(utenteId: Long, annuncioId: Long): Flow<Boolean>

    @Query("DELETE FROM preferiti WHERE utente_id = :utenteId AND annuncio_id = :annuncioId")
    suspend fun deleteByUtenteEAnnuncio(utenteId: Long, annuncioId: Long): Int
}
