package com.example.unimarketplace.ui.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(
    private val carrelloRepository: CarrelloRepository,
    private val annuncioRepository: AnnuncioRepository,
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
                // Segna ogni annuncio come venduto
                currentItems.forEach { annuncio ->
                    annuncioRepository.updateAnnuncio(annuncio.copy(isVenduto = true))
                }
                // Svuota il carrello
                carrelloRepository.svuotaCarrello(userId)
                _checkoutSuccess.emit(true)
            } catch (e: Exception) {
                // Gestione errore se necessaria
                _checkoutSuccess.emit(false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
