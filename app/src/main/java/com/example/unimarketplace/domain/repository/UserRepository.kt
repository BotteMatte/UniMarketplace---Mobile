package com.example.unimarketplace.domain.repository

import com.example.unimarketplace.data.local.entity.UserEntity

interface UserRepository {
    suspend fun login(email: String, password: String): UserEntity?
    suspend fun register(fullName: String, email: String, password: String): Boolean
    suspend fun ensureUserExists(userId: Long)
}
