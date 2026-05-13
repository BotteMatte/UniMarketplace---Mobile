package com.example.unimarketplace.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.PreferitiRepository

class FavoritesViewModelFactory(
    private val preferitiRepository: PreferitiRepository,
    private val annuncioRepository: AnnuncioRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(preferitiRepository, annuncioRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
