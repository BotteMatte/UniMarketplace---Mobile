package com.example.unimarketplace.domain.model

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
