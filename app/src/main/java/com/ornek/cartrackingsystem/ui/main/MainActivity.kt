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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.ornek.cartrackingsystem.ui.login.LoginActivity
import com.ornek.cartrackingsystem.ui.main.MainContract.UiEffect
import com.ornek.cartrackingsystem.ui.map.MapActivity
import com.ornek.cartrackingsystem.ui.theme.CarTrackingSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            showLocationPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        // Kullanıcı giriş yapmamışsa LoginActivity'ye yönlendir
        if (!viewModel.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

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
                                startActivity(MapActivity.newIntent(this@MainActivity, effect.vehicle))
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
        // Her öne geldiğinde kullanıcı kontrolü yap
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
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showLocationPermissionDeniedDialog() {
        Toast.makeText(
            this,
            "Konum izni olmadan araç konumları görüntülenemez",
            Toast.LENGTH_LONG
        ).show()

        // Ayarlar sayfasına yönlendir
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }
} 