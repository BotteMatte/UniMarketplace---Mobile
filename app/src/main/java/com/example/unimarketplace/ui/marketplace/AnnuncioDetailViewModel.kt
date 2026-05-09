package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.usecase.GetAnnuncioByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnnuncioDetailUiState(
    val annuncio: Annuncio? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPreferito: Boolean = false
)

class AnnuncioDetailViewModel(
    private val getAnnuncioByIdUseCase: GetAnnuncioByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnuncioDetailUiState())
    val uiState: StateFlow<AnnuncioDetailUiState> = _uiState.asStateFlow()

    fun loadAnnuncio(annuncioId: Long) {
        _uiState.value = AnnuncioDetailUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val annuncio = getAnnuncioByIdUseCase(annuncioId)
                _uiState.value = AnnuncioDetailUiState(
                    annuncio = annuncio,
                    isLoading = false,
                    error = if (annuncio == null) "Annuncio non trovato" else null
                )
            } catch (e: Exception) {
                _uiState.value = AnnuncioDetailUiState(
                    isLoading = false,
                    error = "Errore nel caricamento: ${e.localizedMessage}"
                )
            }
        }
    }
}
