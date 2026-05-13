package com.example.unimarketplace.ui.marketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.PreferitiRepository
import com.example.unimarketplace.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val repository: AnnuncioRepository,
    private val preferitiRepository: PreferitiRepository,
    private val carrelloRepository: CarrelloRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _allAnnunci = MutableStateFlow<List<Annuncio>>(emptyList())

    private val _annunciFiltrati = MutableStateFlow<List<Annuncio>>(emptyList())
    val annunciFiltrati: StateFlow<List<Annuncio>> = _annunciFiltrati.asStateFlow()

    private val _preferitiIds = MutableStateFlow<Set<Long>>(emptySet())
    val preferitiIds: StateFlow<Set<Long>> = _preferitiIds.asStateFlow()

    private val _carrelloIds = MutableStateFlow<Set<Long>>(emptySet())
    val carrelloIds: StateFlow<Set<Long>> = _carrelloIds.asStateFlow()

    private val _categoriaSelezionata = MutableStateFlow("Tutte")
    val categoriaSelezionata: StateFlow<String> = _categoriaSelezionata.asStateFlow()

    private val _condizioniSelezionate = MutableStateFlow("Tutte")
    val condizioniSelezionate: StateFlow<String> = _condizioniSelezionate.asStateFlow()

    private val _prezzoMassimo = MutableStateFlow(200f)
    val prezzoMassimo: StateFlow<Float> = _prezzoMassimo.asStateFlow()

    private val _queryRicerca = MutableStateFlow("")
    val queryRicerca: StateFlow<String> = _queryRicerca.asStateFlow()

    private val _errorEvent = MutableStateFlow<String?>(null)
    val errorEvent: StateFlow<String?> = _errorEvent.asStateFlow()

    init {
        caricaAnnunci()
        osservaPreferiti()
        osservaCarrello()
    }

    private fun caricaAnnunci() {
        viewModelScope.launch {
            repository.getAllAnnunci().collect { lista ->
                _allAnnunci.value = lista
                applicaFiltri()
            }
        }
    }

    private fun osservaPreferiti() {
        val utenteId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            preferitiRepository.getPreferitiByUtente(utenteId).collectLatest { ids ->
                _preferitiIds.value = ids.toSet()
            }
        }
    }

    private fun osservaCarrello() {
        val utenteId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            carrelloRepository.getCarrelloByUtente(utenteId).collectLatest { ids ->
                _carrelloIds.value = ids.toSet()
            }
        }
    }

    fun togglePreferito(annuncioId: Long) {
        val utenteId = sessionManager.getUserId() ?: return
        val annuncio = _allAnnunci.value.find { it.id == annuncioId }
        
        if (annuncio?.venditoreId == utenteId) {
            _errorEvent.value = "Non puoi aggiungere ai preferiti un tuo articolo"
            return
        }

        viewModelScope.launch {
            preferitiRepository.togglePreferito(utenteId, annuncioId)
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }

    // NUOVO: metodo pubblico per ricaricare (chiamato quando si torna alla home)
    fun refreshAnnunci() {
        viewModelScope.launch {
            repository.getAllAnnunci().collect { lista ->
                _allAnnunci.value = lista
                applicaFiltri()
            }
        }
    }

    fun setCategoria(categoria: String) {
        _categoriaSelezionata.value = categoria
        applicaFiltri()
    }

    fun setCondizioni(condizioni: String) {
        _condizioniSelezionate.value = condizioni
        applicaFiltri()
    }

    fun setPrezzoMassimo(prezzo: Float) {
        _prezzoMassimo.value = prezzo
        applicaFiltri()
    }

    fun setQueryRicerca(query: String) {
        _queryRicerca.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.searchAnnunci(query).collect { risultati ->
                    _annunciFiltrati.value = risultati
                }
            }
        } else {
            applicaFiltri()
        }
    }

    private fun applicaFiltri() {
        val tutti = _allAnnunci.value
        val categoria = _categoriaSelezionata.value
        val condizioni = _condizioniSelezionate.value
        val prezzoMax = _prezzoMassimo.value

        val filtrati = tutti.filter { annuncio ->
            val matchCategoria = categoria == "Tutte" ||
                    annuncio.categoria.displayName.equals(categoria, ignoreCase = true)
            val matchCondizioni = condizioni == "Tutte" ||
                    annuncio.condizioni.name.equals(condizioni, ignoreCase = true)
            val matchPrezzo = annuncio.prezzo <= prezzoMax
            matchCategoria && matchCondizioni && matchPrezzo
        }

        _annunciFiltrati.value = filtrati
    }
}