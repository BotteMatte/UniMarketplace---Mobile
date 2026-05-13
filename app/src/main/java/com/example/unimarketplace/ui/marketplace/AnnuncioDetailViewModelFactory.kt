package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.domain.repository.AnnuncioRepository

class AnnuncioDetailViewModelFactory(
    private val repository: AnnuncioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnuncioDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnuncioDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}