package com.ornek.cartrackingsystem.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ornek.cartrackingsystem.common.Resource
import com.ornek.cartrackingsystem.data.repository.AuthRepository
import com.ornek.cartrackingsystem.data.repository.VehicleRepository
import com.ornek.cartrackingsystem.ui.main.MainContract.UiAction
import com.ornek.cartrackingsystem.ui.main.MainContract.UiEffect
import com.ornek.cartrackingsystem.ui.main.MainContract.UiState
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle
import com.ornek.cartrackingsystem.ui.main.MainContract.VehicleStatus
import com.ornek.cartrackingsystem.ui.main.MainContract.Statistics
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
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect: Flow<UiEffect> = _uiEffect.receiveAsFlow()

    init {
        loadVehicles()
    }

    fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            _uiState.update { it.copy(hasLocationPermission = true) }
            _uiEffect.send(UiEffect.ShowMessage("Konum izni verildi"))
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.SignOutClicked -> signOut()
            is UiAction.LocationClicked -> {
                if (uiState.value.hasLocationPermission) {
                    showVehicleLocation(uiAction.vehicle)
                } else {
                    viewModelScope.launch {
                        _uiEffect.send(UiEffect.ShowError("Konum izni gerekli"))
                    }
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _uiEffect.send(UiEffect.NavigateToLogin)
            } catch (e: Exception) {
                _uiEffect.send(UiEffect.ShowError(e.message ?: "Çıkış yapılırken bir hata oluştu"))
            }
        }
    }

    private fun showVehicleLocation(vehicle: Vehicle) {
        viewModelScope.launch {
            _uiEffect.send(UiEffect.NavigateToMap(vehicle))
        }
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            updateUiState { copy(isLoading = true) }
            
            when (val result = vehicleRepository.getVehicles()) {
                is Resource.Success -> {
                    val vehicles = result.data
                    val statistics = Statistics(
                        totalVehicles = vehicles.size,
                        availableVehicles = vehicles.count { it.status == VehicleStatus.AVAILABLE },
                        inUseVehicles = vehicles.count { it.status == VehicleStatus.IN_USE },
                        inMaintenanceVehicles = vehicles.count { it.status == VehicleStatus.IN_MAINTENANCE }
                    )
                    updateUiState { 
                        copy(
                            isLoading = false,
                            vehicles = vehicles,
                            statistics = statistics
                        )
                    }
                }
                is Resource.Error -> {
                    updateUiState { copy(isLoading = false) }
                    _uiEffect.send(UiEffect.ShowError(result.exception.message ?: "Araçlar yüklenirken bir hata oluştu"))
                }
            }
        }
    }

    private fun updateUiState(update: UiState.() -> UiState) {
        _uiState.update(update)
    }
}