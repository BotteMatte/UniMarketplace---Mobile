package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.PreferitiRepository
import com.example.unimarketplace.domain.repository.UserRepository
import com.example.unimarketplace.util.BadgeManager

class AnnuncioDetailViewModelFactory(
    private val repository: AnnuncioRepository,
    private val carrelloRepository: CarrelloRepository,
    private val preferitiRepository: PreferitiRepository,
    private val userRepository: UserRepository,
    private val badgeManager: BadgeManager,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnuncioDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnuncioDetailViewModel(repository, carrelloRepository, preferitiRepository, userRepository, badgeManager, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
