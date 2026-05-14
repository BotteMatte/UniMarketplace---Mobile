package com.example.unimarketplace.util

import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.BadgeType
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.BadgeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first

class BadgeManager(
    private val badgeRepository: BadgeRepository,
    private val annuncioRepository: AnnuncioRepository
) {
    private val _newBadgeEarned = MutableSharedFlow<BadgeType>()
    val newBadgeEarned = _newBadgeEarned.asSharedFlow()

    suspend fun checkNovizio(userId: Long) {
        val annunci = annuncioRepository.getAnnunciByVenditore(userId).first()
        if (annunci.isNotEmpty()) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.NOVIZIO)) {
                _newBadgeEarned.emit(BadgeType.NOVIZIO)
            }
        }
    }

    suspend fun checkFotografo(userId: Long, hasImage: Boolean) {
        if (hasImage) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.FOTOGRAFO)) {
                _newBadgeEarned.emit(BadgeType.FOTOGRAFO)
            }
        }
    }

    suspend fun checkAnimaleNotturno(userId: Long, isDarkMode: Boolean) {
        if (isDarkMode) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.ANIMALE_NOTTURNO)) {
                _newBadgeEarned.emit(BadgeType.ANIMALE_NOTTURNO)
            }
        }
    }

    suspend fun checkVendite(userId: Long) {
        val annunci = annuncioRepository.getAnnunciByVenditore(userId).first()
        val venduti = annunci.count { it.isVenduto }

        if (venduti >= 1) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.MERCANTE)) {
                _newBadgeEarned.emit(BadgeType.MERCANTE)
            }
        }
        if (venduti >= 2) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.AFFARISTA)) {
                _newBadgeEarned.emit(BadgeType.AFFARISTA)
            }
        }
        
        checkRabazziere(userId)
    }

    suspend fun checkAcquisti(userId: Long, allAnnunci: List<Annuncio>) {
        val acquisti = allAnnunci.count { it.compratoreId == userId }

        if (acquisti >= 1) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.ROMPIGHIACCIO)) {
                _newBadgeEarned.emit(BadgeType.ROMPIGHIACCIO)
            }
        }
        
        checkRabazziere(userId)
    }

    private suspend fun checkRabazziere(userId: Long) {
        val annunciVenditore = annuncioRepository.getAnnunciByVenditore(userId).first()
        val vendite = annunciVenditore.count { it.isVenduto }
        
        val tuttiAnnunci = annuncioRepository.getAllAnnunci().first()
        val acquisti = tuttiAnnunci.count { it.compratoreId == userId }

        if (vendite >= 2 && acquisti >= 2) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.RABAZZIERE)) {
                _newBadgeEarned.emit(BadgeType.RABAZZIERE)
            }
        }
    }

    suspend fun checkDilettante(userId: Long) {
        val annunci = annuncioRepository.getAnnunciByVenditore(userId).first()
        if (annunci.size >= 2) {
            if (badgeRepository.checkAndAwardBadge(userId, BadgeType.DILETTANTE)) {
                _newBadgeEarned.emit(BadgeType.DILETTANTE)
            }
        }
    }
}
