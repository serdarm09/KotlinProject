package com.ornek.cartrackingsystem.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.ornek.cartrackingsystem.ui.main.MainContract.Vehicle
import com.ornek.cartrackingsystem.ui.theme.CarTrackingSystemTheme
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.util.GeoPoint

@AndroidEntryPoint
class MapActivity : ComponentActivity(), LocationListener {
    private val viewModel: MapViewModel by viewModels()
    private lateinit var locationManager: LocationManager
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Intent'ten vehicle bilgisini al
        intent.getParcelableExtra<Vehicle>("vehicle")?.let { vehicle ->
            viewModel.setVehicle(vehicle)
        }

        checkLocationPermission()

        setContent {
            CarTrackingSystemTheme {
                val uiState by viewModel.uiState.collectAsState()
                MapScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }
            else -> {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun startLocationUpdates() {
        try {
            // GPS provider'dan konum güncellemelerini al
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, // 5 saniyede bir güncelle
                10f,  // 10 metre hareket varsa güncelle
                this
            )
            
            // Network provider'dan konum güncellemelerini al
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                10f,
                this
            )
            
            // Son bilinen konumu al
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                onLocationChanged(location)
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onLocationChanged(location: Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        viewModel.onAction(MapContract.UiAction.UserLocationChanged(geoPoint))
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    companion object {
        fun newIntent(context: Context, vehicle: Vehicle) =
            Intent(context, MapActivity::class.java).apply {
                putExtra("vehicle", vehicle)
            }
    }
} 