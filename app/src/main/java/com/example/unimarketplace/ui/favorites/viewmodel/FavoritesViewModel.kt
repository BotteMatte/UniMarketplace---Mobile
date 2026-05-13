package com.example.unimarketplace.ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.PreferitiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val preferitiRepository: PreferitiRepository,
    private val annuncioRepository: AnnuncioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _favoriteItems = MutableStateFlow<List<Annuncio>>(emptyList())
    val favoriteItems: StateFlow<List<Annuncio>> = _favoriteItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            preferitiRepository.getPreferitiByUtente(userId).collect { ids ->
                val annunci = ids.mapNotNull { id ->
                    try {
                        annuncioRepository.getAnnuncioById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
                _favoriteItems.value = annunci
                _isLoading.value = false
            }
        }
    }

    fun removeFavorite(annuncioId: Long) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            preferitiRepository.togglePreferito(userId, annuncioId)
        }
    }
}
