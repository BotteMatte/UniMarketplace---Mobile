package com.example.unimarketplace.domain.model

data class Annuncio(
    val id: Long,
    val titolo: String,
    val descrizione: String,
    val prezzo: Double,
    val categoria: Categoria,
    val condizioni: Condizioni,
    val immagini: List<String> = emptyList(),
    val dataPubblicazione: Long,
    val venditoreId: Long,
    val venditoreNome: String,
    val isVenduto: Boolean
)
