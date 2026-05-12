package com.example.unimarketplace.ui.marketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Date

class CreateAnnuncioViewModel(
    private val repository: AnnuncioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _createResult = MutableSharedFlow<CreateResult>()
    val createResult = _createResult.asSharedFlow()

    fun createAnnuncio(
        titolo: String,
        descrizione: String,
        prezzo: Double,
        categoria: Categoria,
        condizioni: Condizioni,
        immagini: List<String>
    ) {
        val userId = sessionManager.getUserId()
        val userName = sessionManager.getUserName()

        if (userId == null || userName == null) {
            viewModelScope.launch {
                _createResult.emit(CreateResult.Error("Devi essere loggato per creare un annuncio."))
            }
            return
        }

        if (titolo.isBlank() || descrizione.isBlank() || prezzo <= 0) {
            viewModelScope.launch {
                _createResult.emit(CreateResult.Error("Compila tutti i campi correttamente."))
            }
            return
        }

        val annuncio = Annuncio(
            id = 0, // Auto-generate
            titolo = titolo,
            descrizione = descrizione,
            prezzo = prezzo,
            categoria = categoria,
            condizioni = condizioni,
            immagini = immagini,
            dataPubblicazione = Date().time,
            venditoreId = userId,
            venditoreNome = userName,
            isVenduto = false
        )

        viewModelScope.launch {
            try {
                repository.insertAnnuncio(annuncio)
                _createResult.emit(CreateResult.Success("Annuncio creato con successo!"))
            } catch (e: Exception) {
                e.printStackTrace()
                _createResult.emit(CreateResult.Error("Errore: ${e.message}"))
            }
        }
    }

    sealed class CreateResult {
        data class Success(val message: String) : CreateResult()
        data class Error(val message: String) : CreateResult()
    }
}
