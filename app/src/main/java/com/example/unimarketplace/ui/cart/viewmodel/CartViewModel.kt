package com.example.unimarketplace.ui.cart.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.data.local.SessionManager
import com.example.unimarketplace.domain.model.Annuncio
import com.example.unimarketplace.domain.repository.AnnuncioRepository
import com.example.unimarketplace.domain.repository.CarrelloRepository
import com.example.unimarketplace.domain.repository.NotificationRepository
import com.example.unimarketplace.util.BadgeManager
import com.example.unimarketplace.util.ReceiptGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CartViewModel(
    application: Application,
    private val carrelloRepository: CarrelloRepository,
    private val annuncioRepository: AnnuncioRepository,
    private val badgeManager: BadgeManager,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    private val _cartItems = MutableStateFlow<List<Annuncio>>(emptyList())
    val cartItems: StateFlow<List<Annuncio>> = _cartItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _checkoutSuccess = MutableSharedFlow<CheckoutResult>()
    val checkoutSuccess = _checkoutSuccess.asSharedFlow()

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            _isLoading.value = true
            carrelloRepository.getCarrelloByUtente(userId).collect { ids ->
                val annunci = ids.mapNotNull { id ->
                    try {
                        annuncioRepository.getAnnuncioById(id)
                    } catch (e: Exception) {
                        null
                    }
                }
                _cartItems.value = annunci
                _isLoading.value = false
            }
        }
    }

    fun rimuoviDalCarrello(annuncioId: Long) {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            carrelloRepository.rimuoviDalCarrello(userId, annuncioId)
        }
    }

    fun svuotaCarrello() {
        val userId = sessionManager.getUserId() ?: return
        viewModelScope.launch {
            carrelloRepository.svuotaCarrello(userId)
        }
    }

    fun procediAlPagamento() {
        val userId = sessionManager.getUserId() ?: return
        val currentItems = _cartItems.value
        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val acquirenteNome = sessionManager.getUserName() ?: "Utente"
                val acquirenteEmail = sessionManager.getUserEmail() ?: "N/D"
                val receiptGenerator = ReceiptGenerator(getApplication())

                // Lista degli item per la ricevuta
                val receiptItems = mutableListOf<ReceiptGenerator.ReceiptItem>()
                var totale = 0.0

                currentItems.forEach { annuncio ->
                    // Segna come venduto
                    annuncioRepository.updateAnnuncio(annuncio.copy(isVenduto = true, compratoreId = userId))

                    // Badge venditore
                    badgeManager.checkVendite(annuncio.venditoreId)

                    // Notifiche
                    notificationRepository.addNotification(
                        userId = annuncio.venditoreId,
                        title = "Annuncio acquistato! 🎉",
                        message = "'${annuncio.titolo}' è stato acquistato da $acquirenteNome per €${String.format("%.2f", annuncio.prezzo)}",
                        type = "sale",
                        relatedId = annuncio.id
                    )

                    notificationRepository.addNotification(
                        userId = userId,
                        title = "Acquisto completato! ✅",
                        message = "Hai acquistato '${annuncio.titolo}' da ${annuncio.venditoreNome} per €${String.format("%.2f", annuncio.prezzo)}",
                        type = "cart",
                        relatedId = annuncio.id
                    )

                    // Aggiungi al riepilogo
                    receiptItems.add(
                        ReceiptGenerator.ReceiptItem(
                            annuncio = annuncio,
                            venditoreNome = annuncio.venditoreNome
                        )
                    )
                    totale += annuncio.prezzo
                }

                // genera ricevuta
                val receiptUri = receiptGenerator.generateReceipt(
                    ReceiptGenerator.ReceiptData(
                        acquirenteNome = acquirenteNome,
                        acquirenteEmail = acquirenteEmail,
                        items = receiptItems,
                        totale = totale
                    )
                )

                // badge compratore
                val updatedAllAnnunci = annuncioRepository.getAllAnnunci().first()
                badgeManager.checkAcquisti(userId, updatedAllAnnunci)

                // svuota carrello
                carrelloRepository.svuotaCarrello(userId)

                _checkoutSuccess.emit(CheckoutResult.Success(receiptUri))
            } catch (e: Exception) {
                _checkoutSuccess.emit(CheckoutResult.Error(e.message ?: "Errore durante il pagamento"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class CheckoutResult {
        data class Success(val receiptUri: Uri?) : CheckoutResult()
        data class Error(val message: String) : CheckoutResult()
    }
}