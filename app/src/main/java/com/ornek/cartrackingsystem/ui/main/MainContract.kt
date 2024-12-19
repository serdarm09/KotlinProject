package com.ornek.cartrackingsystem.ui.main

import com.ornek.cartrackingsystem.ui.login.LoginActivity

object MainContract {
    data class UiState(
        val isLoading: Boolean = false
    )

    sealed interface UiAction {
        object SignOutClicked : UiAction
    }

    sealed interface UiEffect {
        object NavigateToLogin : UiEffect
        data class ShowError(val message: String) : UiEffect
    }
}