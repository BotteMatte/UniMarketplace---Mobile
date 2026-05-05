package com.example.unimarketplace.domain.model

/**
 * Item: Modello dati rappresentante un annuncio nel marketplace (Libro o Appunti).
 * Contiene informazioni come titolo, descrizione, prezzo, categoria, 
 * coordinate GPS per la posizione e URL dell'immagine acquisita tramite fotocamera.
 */
data class Item(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null
)
