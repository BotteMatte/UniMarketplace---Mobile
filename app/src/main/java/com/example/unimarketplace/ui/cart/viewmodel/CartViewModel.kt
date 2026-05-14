package com.example.unimarketplace.ui.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.NotificationRepository
import com.example.unimarketplace.util.BadgeManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(
    private val carrelloRepository: CarrelloRepository,
    private val annuncioRepository: AnnuncioRepository,
    private val badgeManager: BadgeManager,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<Annuncio>>(emptyList())
    val cartItems: StateFlow<List<Annuncio>> = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _checkoutSuccess = MutableSharedFlow<Boolean>()
    val checkoutSuccess = _checkoutSuccess.asSharedFlow()

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            carrelloRepository.getCarrelloByUtente(userId).collect { ids ->
                val annunci = ids.mapNotNull { id ->
                    try {
                        annuncioRepository.getAnnuncioById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
                _cartItems.value = annunci
                _isLoading.value = false
            }
        }
    }

    fun rimuoviDalCarrello(annuncioId: Long) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            carrelloRepository.rimuoviDalCarrello(userId, annuncioId)
        }
    }

    fun svuotaCarrello() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            carrelloRepository.svuotaCarrello(userId)
        }
    }

    fun procediAlPagamento() {
        val userId = sessionManager.getUserId() ?: return
        val currentItems = _cartItems.value
        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val acquirenteNome = sessionManager.getUserName() ?: "Utente"

                currentItems.forEach { annuncio ->
                    // Segna l'annuncio come venduto
                    annuncioRepository.updateAnnuncio(annuncio.copy(isVenduto = true, compratoreId = userId))

                    // Check badge per il venditore
                    badgeManager.checkVendite(annuncio.venditoreId)

                    // Notifica al VENDITORE
                    notificationRepository.addNotification(
                        userId = annuncio.venditoreId,
                        title = "Annuncio acquistato! 🎉",
                        message = "'${annuncio.titolo}' è stato acquistato da $acquirenteNome per €${String.format("%.2f", annuncio.prezzo)}",
                        type = "sale",
                        relatedId = annuncio.id
                    )

                    // Notifica al COMPRATORE
                    notificationRepository.addNotification(
                        userId = userId,
                        title = "Acquisto completato! ✅",
                        message = "Hai acquistato '${annuncio.titolo}' da ${annuncio.venditoreNome} per €${String.format("%.2f", annuncio.prezzo)}",
                        type = "cart",
                        relatedId = annuncio.id
                    )
                }

                // Check badge per il compratore
                val updatedAllAnnunci = annuncioRepository.getAllAnnunci().first()
                badgeManager.checkAcquisti(userId, updatedAllAnnunci)

                // Svuota il carrello
                carrelloRepository.svuotaCarrello(userId)
                _checkoutSuccess.emit(true)
            } catch (e: Exception) {
                _checkoutSuccess.emit(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}