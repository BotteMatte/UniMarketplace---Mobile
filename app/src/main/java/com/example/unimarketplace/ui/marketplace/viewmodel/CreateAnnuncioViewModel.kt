package com.example.unimarketplace.ui.marketplace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.util.location.LocationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class CreateAnnuncioViewModel(
    application: Application,
    private val repository: AnnuncioRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val locationHelper = LocationHelper(application)

    private val _createResult = MutableSharedFlow<CreateResult>()
    val createResult = _createResult.asSharedFlow()

    private val _posizione = MutableStateFlow<com.example.unimarketplace.util.location.Posizione?>(null)
    val posizione = _posizione.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation = _isLoadingLocation.asStateFlow()

    init {
        getCurrentLocation()
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            try {
                val pos = locationHelper.getCurrentPosition()
                _posizione.value = pos
            } catch (e: Exception) {
                _createResult.emit(CreateResult.Error("Errore nel rilevamento della posizione: ${e.message}"))
            } finally {
                _isLoadingLocation.value = false
            }
        }
    }

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

        val posizioneAttuale = _posizione.value

        val annuncio = Annuncio(
            id = 0,
            titolo = titolo,
            descrizione = descrizione,
            prezzo = prezzo,
            categoria = categoria,
            condizioni = condizioni,
            immagini = immagini,
            dataPubblicazione = Date().time,
            venditoreId = userId,
            venditoreNome = userName,
            isVenduto = false,
            latitudine = posizioneAttuale?.latitudine ?: 0.0,
            longitudine = posizioneAttuale?.longitudine ?: 0.0,
            indirizzo = posizioneAttuale?.indirizzo ?: "",
            citta = posizioneAttuale?.citta ?: "",
            cap = posizioneAttuale?.cap ?: "",
            provincia = posizioneAttuale?.provincia ?: ""
        )

        viewModelScope.launch {
            try {
                repository.insertAnnuncio(annuncio)
                _createResult.emit(CreateResult.Success("Annuncio creato con successo!"))
            } catch (e: Exception) {
                _createResult.emit(CreateResult.Error("Errore: ${e.message}"))
            }
        }
    }

    sealed class CreateResult {
        data class Success(val message: String) : CreateResult()
        data class Error(val message: String) : CreateResult()
    }
}