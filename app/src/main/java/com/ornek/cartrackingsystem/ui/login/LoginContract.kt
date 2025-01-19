package com.ornek.cartrackingsystem.ui.login

object LoginContract {
    data class UiState(
        val isLoading: Boolean = false,
        val isLoggedIn: Boolean = false,
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null
    )

    sealed interface UiAction {
        data class EmailChanged(val email: String) : UiAction
        data class PasswordChanged(val password: String) : UiAction
        data object LoginClicked : UiAction
    }

    sealed interface UiEffect {
        data object NavigateToMain : UiEffect
        data class ShowError(val message: String) : UiEffect
    }
}