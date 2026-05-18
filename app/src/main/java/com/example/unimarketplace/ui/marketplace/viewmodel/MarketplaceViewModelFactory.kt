package com.example.unimarketplace.ui.marketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.PreferitiRepository
import com.example.unimarketplace.domain.repository.UserRepository
import com.example.unimarketplace.data.local.SessionManager

class MarketplaceViewModelFactory(
    private val repository: AnnuncioRepository,
    private val preferitiRepository: PreferitiRepository,
    private val carrelloRepository: CarrelloRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketplaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MarketplaceViewModel(repository, preferitiRepository, carrelloRepository, userRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
