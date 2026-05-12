package com.example.unimarketplace.data.mapper

import com.example.unimarketplace.data.local.entity.AnnuncioEntity
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.Categoria
import com.example.unimarketplace.domain.model.Condizioni

fun AnnuncioEntity.toDomain(): Annuncio {
    val categoria = try {
        Categoria.valueOf(this.categoria)
    } catch (e: IllegalArgumentException) {
        Categoria.ALTRO
    }

    val condizioni = try {
        Condizioni.valueOf(this.condizioni)
    } catch (e: IllegalArgumentException) {
        Condizioni.USATO
    }

    return Annuncio(
        id = this.id,
        titolo = this.titolo,
        descrizione = this.descrizione,
        prezzo = this.prezzo,
        categoria = categoria,
        condizioni = condizioni,
        immagini = this.immagini,
        dataPubblicazione = this.dataPubblicazione,
        venditoreId = this.venditoreId,
        venditoreNome = this.venditoreNome,
        isVenduto = this.isVenduto
    )
}

fun Annuncio.toEntity(): AnnuncioEntity {
    return AnnuncioEntity(
        id = this.id,
        titolo = this.titolo,
        descrizione = this.descrizione,
        prezzo = this.prezzo,
        categoria = this.categoria.name,
        condizioni = this.condizioni.name,
        immagini = this.immagini,
        dataPubblicazione = this.dataPubblicazione,
        venditoreId = this.venditoreId,
        venditoreNome = this.venditoreNome,
        isVenduto = this.isVenduto
    )
}
