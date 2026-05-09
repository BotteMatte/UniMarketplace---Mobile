package com.example.unimarketplace.ui.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unimarketplace.domain.usecase.GetAnnuncioByIdUseCase

class AnnuncioDetailViewModelFactory(
    private val getAnnuncioByIdUseCase: GetAnnuncioByIdUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnuncioDetailViewModel::class.java)) {
            return AnnuncioDetailViewModel(getAnnuncioByIdUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
