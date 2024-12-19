package com.ornek.cartrackingsystem.ui.login

object LoginContract {
    data class UiState(
        val isLoading: Boolean = false
    )

    sealed interface UiAction {
        object LoginClicked : UiAction
    }

    sealed interface UiEffect {
        object NavigateToMain : UiEffect
        data class ShowError(val message: String) : UiEffect
    }
}