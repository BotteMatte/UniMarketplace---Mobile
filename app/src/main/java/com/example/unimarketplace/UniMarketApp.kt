package com.example.unimarketplace

import android.app.Application
import com.example.unimarketplace.data.local.UniMarketDatabase
import com.example.unimarketplace.data.repository.AnnuncioRepositoryImpl
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.usecase.GetAnnuncioByIdUseCase

class UniMarketApp : Application() {

    // Database
    val database by lazy { UniMarketDatabase.getInstance(this) }

    // Repositories
    val annuncioRepository: AnnuncioRepository by lazy {
        AnnuncioRepositoryImpl(database.annuncioDao())
    }

    // Use Cases
    val getAnnuncioByIdUseCase by lazy {
        GetAnnuncioByIdUseCase(annuncioRepository)
    }
}
