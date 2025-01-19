package com.ornek.cartrackingsystem.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ornek.cartrackingsystem.ui.login.LoginActivity
import com.ornek.cartrackingsystem.ui.main.MainContract.UiEffect
import com.ornek.cartrackingsystem.ui.theme.CarTrackingSystemTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewModel.onLocationPermissionGranted()
            }
            else -> {
                showLocationPermissionSettings()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        setContent {
            CarTrackingSystemTheme {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = Unit) {
                    viewModel.uiEffect.collect { effect ->
                        when (effect) {
                            is UiEffect.NavigateToLogin -> {
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                finish()
                            }
                            is UiEffect.NavigateToMap -> {
                                // TODO: Harita ekranına git
                                Toast.makeText(
                                    this@MainActivity,
                                    "Harita özelliği yakında eklenecek: ${effect.vehicle.plate}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiEffect.ShowError -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    effect.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is UiEffect.ShowMessage -> {
                                Toast.makeText(
                                    this@MainActivity,
                                    effect.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                MainScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkLocationPermission() {
        when {
            hasLocationPermission() -> {
                viewModel.onLocationPermissionGranted()
            }
            else -> {
                requestLocationPermission()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showLocationPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }
} 