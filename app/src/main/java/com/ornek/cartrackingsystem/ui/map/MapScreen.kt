package com.ornek.cartrackingsystem.ui.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.ornek.cartrackingsystem.ui.map.MapContract.UiAction
import com.ornek.cartrackingsystem.ui.map.MapContract.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vehicleLocation = uiState.vehicleLocation
    val userLocation = uiState.userLocation

    // OSMDroid konfigürasyonu
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.vehicle?.plate ?: "Araç Konumu") },
                navigationIcon = {
                    IconButton(onClick = { onAction(UiAction.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Harita görünümü
            val mapView = remember {
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                }
            }

            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    // Araç konumu
                    uiState.vehicleLocation?.let { location ->
                        map.overlays.clear()
                        val marker = Marker(map).apply {
                            position = location
                            title = uiState.vehicle?.plate ?: "Araç"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(marker)
                        map.controller.setCenter(location)
                    }

                    // Kullanıcı konumu
                    uiState.userLocation?.let { location ->
                        val userMarker = Marker(map).apply {
                            position = location
                            title = "Konumunuz"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(userMarker)
                    }

                    map.invalidate()
                }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Harita kaynakları temizleme
    DisposableEffect(Unit) {
        onDispose {
            Configuration.getInstance().osmdroidTileCache?.let { cache ->
                cache.delete()
            }
        }
    }
}

private fun createMapView(context: Context): MapView {
    return MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
    }
}

private fun addMarker(
    mapView: MapView,
    context: Context,
    position: GeoPoint,
    title: String,
    snippet: String
) {
    val marker = Marker(mapView).apply {
        this.position = position
        this.title = title
        this.snippet = snippet
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
    mapView.overlays.add(marker)
} 