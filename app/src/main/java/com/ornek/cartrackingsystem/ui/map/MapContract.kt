package com.ornek.cartrackingsystem.ui.map

import org.osmdroid.util.GeoPoint
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle

object MapContract {
    data class UiState(
        val isLoading: Boolean = false,
        val vehicle: Vehicle? = null,
        val vehicleLocation: GeoPoint? = null,
        val userLocation: GeoPoint? = null,
        val hasLocationPermission: Boolean = false
    )

    sealed interface UiAction {
        data object BackClicked : UiAction
        data class UserLocationChanged(val location: GeoPoint) : UiAction
        data class VehicleLocationChanged(val location: GeoPoint) : UiAction
    }

    sealed interface UiEffect {
        data object NavigateBack : UiEffect
        data class ShowError(val message: String) : UiEffect
        data class ShowMessage(val message: String) : UiEffect
    }
} 