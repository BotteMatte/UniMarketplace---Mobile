package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.UserDao
import com.example.unimarketplace.data.local.dao.UtenteDao
import com.example.unimarketplace.data.local.entity.UserEntity
import com.example.unimarketplace.data.local.entity.UtenteEntity
import com.example.unimarketplace.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val utenteDao: UtenteDao
) : UserRepository {

    override suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.login(email, password)
        if (user != null) {
            // Salva anche nella tabella utenti (per la foreign key degli annunci)
            val utenteEsistente = utenteDao.getUtenteById(user.id.toLong())
            if (utenteEsistente == null) {
                utenteDao.insertUtente(
                    UtenteEntity(
                        id = user.id.toLong(),
                        nome = user.fullName,
                        email = user.email,
                        matricola = "N/A"
                    )
                )
            }
        }
        return user
    }

    override suspend fun register(fullName: String, email: String, password: String): Boolean {
        // Verifica se l'email esiste già
        if (userDao.getUserByEmail(email) != null) return false

        val newUser = UserEntity(
            fullName = fullName,
            email = email,
            password = password
        )
        userDao.insertUser(newUser)
        return true
    }
}