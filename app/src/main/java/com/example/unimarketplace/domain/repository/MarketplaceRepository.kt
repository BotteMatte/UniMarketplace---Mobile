package com.example.unimarketplace.domain.repository

/**
 * MarketplaceRepository: Interfaccia del repository nel livello domain.
 * Definisce il contratto per l'accesso ai dati, permettendo al resto dell'app
 * di non dipendere direttamente da Room o da altre librerie di persistenza.
 */
interface MarketplaceRepository {
    // fun getItems(): Flow<List<Item>>
}
