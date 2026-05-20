package com.example.unimarketplace

import android.app.Application
import com.example.unimarketplace.data.local.UniMarketDatabase
import com.example.unimarketplace.data.repository.AnnuncioRepositoryImpl
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.usecase.GetAnnuncioByIdUseCase

class UniMarketApp : Application() {

    // db
    val database by lazy { UniMarketDatabase.getInstance(this) }

    // repositories
    val annuncioRepository: AnnuncioRepository by lazy {
        AnnuncioRepositoryImpl(database.annuncioDao())
    }

    // casi d'uso
    val getAnnuncioByIdUseCase by lazy {
        GetAnnuncioByIdUseCase(annuncioRepository)
    }
}
