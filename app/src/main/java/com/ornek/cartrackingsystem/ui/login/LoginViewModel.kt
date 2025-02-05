package com.ornek.cartrackingsystem.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ornek.cartrackingsystem.data.repository.AuthRepository
import com.ornek.cartrackingsystem.ui.login.LoginContract.UiAction
import com.ornek.cartrackingsystem.ui.login.LoginContract.UiEffect
import com.ornek.cartrackingsystem.ui.login.LoginContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect: Flow<UiEffect> = _uiEffect.receiveAsFlow()

    init {
        if (authRepository.isUserLoggedIn()) {
            viewModelScope.launch {
                _uiEffect.send(UiEffect.NavigateToMain)
            }
        }
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.EmailChanged -> {
                _uiState.update { it.copy(
                    email = action.email,
                    emailError = null
                ) }
            }
            is UiAction.PasswordChanged -> {
                _uiState.update { it.copy(
                    password = action.password,
                    passwordError = null
                ) }
            }
            is UiAction.LoginClicked -> login()
        }
    }

    private fun login() {
        val email = uiState.value.email
        val password = uiState.value.password

        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "E-posta boş olamaz") }
            return
        }

        if (!email.contains("@")) {
            _uiState.update { it.copy(emailError = "Geçerli bir e-posta adresi girin") }
            return
        }

        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Şifre boş olamaz") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(passwordError = "Şifre en az 6 karakter olmalı") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.signIn(email, password)
                _uiState.update { it.copy(isLoggedIn = true) }
                _uiEffect.send(UiEffect.NavigateToMain)
            } catch (e: Exception) {
                _uiEffect.send(UiEffect.ShowError(e.message ?: "Giriş yapılırken bir hata oluştu"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}