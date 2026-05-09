package com.example.unimarketplace.domain.usecase

import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
class GetAnnuncioByIdUseCase(
    private val annuncioRepository: AnnuncioRepository
) {
    suspend operator fun invoke(id: Long): Annuncio? {
        return annuncioRepository.getAnnuncioById(id)
    }
}
