package com.example.unimarketplace.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.unimarketplace.data.local.entity.UtenteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UtenteDao {
    @Insert
    suspend fun insertUtente(utenteEntity: UtenteEntity): Long

    @Update
    suspend fun updateUtente(utenteEntity: UtenteEntity)

    @Delete
    suspend fun deleteUtente(utenteEntity: UtenteEntity)

    @Query("SELECT * FROM utenti WHERE id = :id")
    suspend fun getUtenteById(id: Long): UtenteEntity?

    @Query("SELECT * FROM utenti WHERE email = :email")
    suspend fun getUtenteByEmail(email: String): UtenteEntity?

    @Query("SELECT * FROM utenti")
    fun getAllUtenti(): Flow<List<UtenteEntity>>
}
