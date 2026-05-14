package com.example.unimarketplace.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.BadgeRepository
import com.example.unimarketplace.util.BadgeManager

class ProfileViewModelFactory(
    private val annuncioRepository: AnnuncioRepository,
    private val badgeRepository: BadgeRepository,
    private val badgeManager: BadgeManager,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(annuncioRepository, badgeRepository, badgeManager, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
