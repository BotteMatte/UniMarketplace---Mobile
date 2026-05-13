package com.example.unimarketplace.ui.marketplace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val repository: AnnuncioRepository
) : ViewModel() {

    private val _allAnnunci = MutableStateFlow<List<Annuncio>>(emptyList())

    private val _annunciFiltrati = MutableStateFlow<List<Annuncio>>(emptyList())
    val annunciFiltrati: StateFlow<List<Annuncio>> = _annunciFiltrati.asStateFlow()

    private val _categoriaSelezionata = MutableStateFlow("Tutte")
    val categoriaSelezionata: StateFlow<String> = _categoriaSelezionata.asStateFlow()

    private val _condizioniSelezionate = MutableStateFlow("Tutte")
    val condizioniSelezionate: StateFlow<String> = _condizioniSelezionate.asStateFlow()

    private val _prezzoMassimo = MutableStateFlow(200f)
    val prezzoMassimo: StateFlow<Float> = _prezzoMassimo.asStateFlow()

    private val _queryRicerca = MutableStateFlow("")
    val queryRicerca: StateFlow<String> = _queryRicerca.asStateFlow()

    init {
        caricaAnnunci()
    }

    private fun caricaAnnunci() {
        viewModelScope.launch {
            repository.getAllAnnunci().collect { lista ->
                _allAnnunci.value = lista
                applicaFiltri()
            }
        }
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
                    annuncio.categoria.name.equals(categoria, ignoreCase = true)
            val matchCondizioni = condizioni == "Tutte" ||
                    annuncio.condizioni.name.equals(condizioni, ignoreCase = true)
            val matchPrezzo = annuncio.prezzo <= prezzoMax
            matchCategoria && matchCondizioni && matchPrezzo
        }

        _annunciFiltrati.value = filtrati
    }
}