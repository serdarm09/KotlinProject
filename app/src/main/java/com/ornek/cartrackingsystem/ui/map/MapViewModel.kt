package com.ornek.cartrackingsystem.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle
import com.ornek.cartrackingsystem.ui.map.MapContract.UiAction
import com.ornek.cartrackingsystem.ui.map.MapContract.UiEffect
import com.ornek.cartrackingsystem.ui.map.MapContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect: Flow<UiEffect> = _uiEffect.receiveAsFlow()

    init {
        // Araç bilgilerini intent'ten al
        savedStateHandle.get<Vehicle>("vehicle")?.let { vehicle ->
            _uiState.update { it.copy(vehicle = vehicle) }
            // Araç konumunu GeoPoint'e çevir
            _uiState.update { 
                it.copy(
                    vehicleLocation = GeoPoint(
                        vehicle.location.latitude,
                        vehicle.location.longitude
                    )
                )
            }
        }
    }

    fun setVehicle(vehicle: Vehicle) {
        _uiState.update { currentState ->
            currentState.copy(
                vehicle = vehicle,
                vehicleLocation = GeoPoint(
                    vehicle.location.latitude,
                    vehicle.location.longitude
                )
            )
        }
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.BackClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(UiEffect.NavigateBack)
                }
            }
            is UiAction.UserLocationChanged -> {
                _uiState.update { it.copy(userLocation = action.location) }
            }
            is UiAction.VehicleLocationChanged -> {
                _uiState.update { it.copy(vehicleLocation = action.location) }
            }
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.update { it.copy(hasLocationPermission = true) }
    }
} 