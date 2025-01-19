package com.ornek.cartrackingsystem.ui.main

object MainContract {
    data class UiState(
        val isLoading: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val statistics: Statistics = Statistics(),
        val hasLocationPermission: Boolean = false
    )

    data class Statistics(
        val totalVehicles: Int = 0,
        val availableVehicles: Int = 0,
        val inUseVehicles: Int = 0,
        val inMaintenanceVehicles: Int = 0
    )

    data class Vehicle(
        val plate: String,
        val brand: String,
        val model: String,
        val status: VehicleStatus,
        val location: String
    )

    enum class VehicleStatus {
        AVAILABLE,
        IN_USE,
        IN_MAINTENANCE
    }

    sealed interface UiAction {
        data object SignOutClicked : UiAction
        data class LocationClicked(val vehicle: Vehicle) : UiAction
    }

    sealed interface UiEffect {
        data object NavigateToLogin : UiEffect
        data class NavigateToMap(val vehicle: Vehicle) : UiEffect
        data class ShowError(val message: String) : UiEffect
        data class ShowMessage(val message: String) : UiEffect
    }
}