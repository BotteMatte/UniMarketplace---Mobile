package com.example.unimarketplace.data.mapper

import com.example.unimarketplace.data.local.entity.PreferitiEntity
import com.example.unimarketplace.domain.model.Preferiti

fun PreferitiEntity.toDomain(): Preferiti {
    return Preferiti(
        id = this.id,
        utenteId = this.utenteId,
        annuncioId = this.annuncioId
    )
}

fun Preferiti.toEntity(): PreferitiEntity {
    return PreferitiEntity(
        id = this.id,
        utenteId = this.utenteId,
        annuncioId = this.annuncioId
    )
}
