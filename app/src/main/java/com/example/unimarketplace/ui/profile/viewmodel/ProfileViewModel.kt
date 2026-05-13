package com.example.unimarketplace.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
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
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _stats = MutableStateFlow(ProfileStats())
    val stats: StateFlow<ProfileStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            annuncioRepository.getAnnunciByVenditore(userId).collectLatest { annunci ->
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
}
