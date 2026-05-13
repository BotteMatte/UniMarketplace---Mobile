package com.example.unimarketplace.domain.model

data class Annuncio(
    val id: Long = 0,
    val titolo: String,
    val descrizione: String,
    val prezzo: Double,
    val categoria: Categoria,
    val condizioni: Condizioni,
    val immagini: List<String> = emptyList(),
    val dataPubblicazione: Long,
    val venditoreId: Long,
    val venditoreNome: String,
    val isVenduto: Boolean = false,
    // Nuovi campi per la geolocalizzazione
    val latitudine: Double = 0.0,
    val longitudine: Double = 0.0,
    val indirizzo: String = "",
    val citta: String = "",
    val cap: String = "",
    val provincia: String = ""
)