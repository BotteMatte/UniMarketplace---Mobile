package com.example.unimarketplace.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unimarketplace.domain.repository.UserRepository
import com.example.unimarketplace.data.local.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authResult = MutableSharedFlow<AuthResult>()
    val authResult = _authResult.asSharedFlow()

    private val _currentUser = MutableStateFlow<String?>(sessionManager.getUserName())
    val currentUser = _currentUser.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            if (user != null) {
                _currentUser.value = user.fullName
                sessionManager.saveUser(user.id.toLong(), user.fullName, user.email)
                _authResult.emit(AuthResult.Success("Benvenuto, ${user.fullName}!"))
            } else {
                _authResult.emit(AuthResult.Error("Credenziali errate."))
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                _authResult.emit(AuthResult.Error("Compila tutti i campi."))
                return@launch
            }
            val success = repository.register(fullName, email, password)
            if (success) {
                _authResult.emit(AuthResult.Success("Registrazione completata! Ora puoi accedere."))
            } else {
                _authResult.emit(AuthResult.Error("L'email è già in uso."))
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        sessionManager.clearSession()
    }

    sealed class AuthResult {
        data class Success(val message: String) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
