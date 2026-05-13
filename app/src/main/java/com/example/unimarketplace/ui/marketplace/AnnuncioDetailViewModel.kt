package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnnuncioDetailViewModel(
    private val repository: AnnuncioRepository
) : ViewModel() {

    private val _annuncio = MutableStateFlow<Annuncio?>(null)
    val annuncio: StateFlow<Annuncio?> = _annuncio.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
}