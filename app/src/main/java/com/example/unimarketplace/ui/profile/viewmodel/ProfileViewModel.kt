package com.example.unimarketplace.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.model.Badge
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.BadgeRepository
import com.example.unimarketplace.util.BadgeManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileStats(
    val totalAds: Int = 0,
    val soldAds: Int = 0,
    val activeAds: Int = 0,
    val totalEarnings: Double = 0.0,
    val adsByCategory: Map<String, Int> = emptyMap()
)

class ProfileViewModel(
    private val annuncioRepository: AnnuncioRepository,
    private val badgeRepository: BadgeRepository,
    private val badgeManager: BadgeManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    private val _userAnnunci = MutableStateFlow<List<Annuncio>>(emptyList())
    val userAnnunci: StateFlow<List<Annuncio>> = _userAnnunci.asStateFlow()

    private val _userBadges = MutableStateFlow<List<Badge>>(emptyList())
    val userBadges: StateFlow<List<Badge>> = _userBadges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true

            launch {
                badgeRepository.getBadgesByUtente(userId).collectLatest { badges ->
                    _userBadges.value = badges
                }
            }

            annuncioRepository.getAnnunciByVenditore(userId).collectLatest { annunci ->
                _userAnnunci.value = annunci
                
                val total = annunci.size
                val sold = annunci.count { it.isVenduto }
                val active = total - sold
                val earnings = annunci.filter { it.isVenduto }.sumOf { it.prezzo }
                val byCategory = annunci.groupBy { it.categoria.name }
                    .mapValues { it.value.size }

                _stats.value = ProfileStats(
                    totalAds = total,
                    soldAds = sold,
                    activeAds = active,
                    totalEarnings = earnings,
                    adsByCategory = byCategory
                )
                _isLoading.value = false
            }
        }
    }

    fun segnaComeVenduto(annuncio: Annuncio) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            annuncioRepository.updateAnnuncio(annuncio.copy(isVenduto = true))
            badgeManager.checkVendite(userId)
        }
    }

    fun eliminaAnnuncio(annuncio: Annuncio) {
        viewModelScope.launch {
            annuncioRepository.deleteAnnuncio(annuncio)
        }
    }
}
