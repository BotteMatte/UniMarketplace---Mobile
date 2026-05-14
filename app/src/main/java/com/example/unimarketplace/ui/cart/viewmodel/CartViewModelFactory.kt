package com.example.unimarketplace.ui.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.NotificationRepository
import com.example.unimarketplace.util.BadgeManager

class CartViewModelFactory(
    private val carrelloRepository: CarrelloRepository,
    private val annuncioRepository: AnnuncioRepository,
    private val badgeManager: BadgeManager,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(carrelloRepository, annuncioRepository, badgeManager, notificationRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}