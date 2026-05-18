package com.example.unimarketplace.ui.marketplace.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.UserRepository
import com.example.unimarketplace.util.BadgeManager

class CreateAnnuncioViewModelFactory(
    private val application: Application,
    private val repository: AnnuncioRepository,
    private val userRepository: UserRepository,
    private val badgeManager: BadgeManager,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateAnnuncioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateAnnuncioViewModel(application, repository, userRepository, badgeManager, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
