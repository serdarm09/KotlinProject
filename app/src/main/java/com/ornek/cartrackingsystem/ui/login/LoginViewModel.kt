package com.ornek.cartrackingsystem.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ornek.cartrackingsystem.common.Resource
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
        // Eğer kullanıcı zaten giriş yapmışsa ana sayfaya yönlendir
        if (authRepository.isUserLoggedIn()) {
            viewModelScope.launch {
                _uiEffect.send(UiEffect.NavigateToMain)
            }
        }
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.LoginClicked -> login(action.email, action.password)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                updateUiState { copy(isLoading = true) }
                
                when (val result = authRepository.signIn(email, password)) {
                    is Resource.Success -> {
                        updateUiState { copy(isLoading = false) }
                        _uiEffect.send(UiEffect.NavigateToMain)
                    }
                    is Resource.Error -> {
                        updateUiState { copy(isLoading = false) }
                        _uiEffect.send(UiEffect.ShowError(result.exception.message ?: "Giriş yapılırken bir hata oluştu"))
                    }
                }
            } catch (e: Exception) {
                updateUiState { copy(isLoading = false) }
                _uiEffect.send(UiEffect.ShowError(e.message ?: "Beklenmeyen bir hata oluştu"))
            }
        }
    }

    private fun updateUiState(update: UiState.() -> UiState) {
        _uiState.update(update)
    }
}