package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AnnuncioDetailViewModel(
    private val repository: AnnuncioRepository,
    private val carrelloRepository: CarrelloRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _annuncio = MutableStateFlow<Annuncio?>(null)
    val annuncio: StateFlow<Annuncio?> = _annuncio.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isOwnAnnuncio: StateFlow<Boolean> = _annuncio.map { 
        it?.venditoreId == sessionManager.getUserId() 
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isAddedToCart = MutableStateFlow(false)
    val isAddedToCart: StateFlow<Boolean> = _isAddedToCart.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadAnnuncio(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val annuncio = repository.getAnnuncioById(id)
                _annuncio.value = annuncio
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun aggiungiAlCarrello() {
        val currentAnnuncio = _annuncio.value ?: return
        val userId = sessionManager.getUserId()
        
        if (userId == null) {
            _errorMessage.value = "Devi effettuare il login per aggiungere al carrello"
            return
        }

        if (currentAnnuncio.venditoreId == userId) {
            _errorMessage.value = "Non puoi aggiungere al carrello un tuo articolo"
            return
        }

        viewModelScope.launch {
            carrelloRepository.aggiungiAlCarrello(userId, currentAnnuncio.id)
            _isAddedToCart.value = true
            _errorMessage.value = null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
