package com.example.unimarketplace.data.repository

import com.example.unimarketplace.data.local.dao.UserDao
import com.example.unimarketplace.data.local.entity.UserEntity
import com.example.unimarketplace.domain.repository.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
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
