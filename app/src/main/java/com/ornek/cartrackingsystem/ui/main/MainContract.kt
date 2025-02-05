package com.ornek.cartrackingsystem.ui.main

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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

    @Parcelize
    data class Location(
        val latitude: Double,
        val longitude: Double
    ) : Parcelable {
        override fun toString(): String = "$latitude,$longitude"

        companion object {
            fun fromString(locationStr: String): Location? {
                return try {
                    val (lat, lng) = locationStr.split(",").map { it.toDouble() }
                    if (lat in -90.0..90.0 && lng in -180.0..180.0) {
                        Location(lat, lng)
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    @Parcelize
    data class Vehicle(
        val plate: String,
        val brand: String,
        val model: String,
        val status: VehicleStatus,
        val location: Location
    ) : Parcelable {
        companion object {
            fun fromMap(data: Map<String, Any>): Vehicle? {
                return try {
                    val locationStr = data["location"] as? String ?: return null
                    val location = Location.fromString(locationStr) ?: return null
                    
                    Vehicle(
                        plate = data["plate"] as String,
                        brand = data["brand"] as String,
                        model = data["model"] as String,
                        status = VehicleStatus.valueOf(data["status"] as String),
                        location = location
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

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