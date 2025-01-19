package com.ornek.cartrackingsystem.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ornek.cartrackingsystem.ui.main.MainContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainContract.UiState,
    onAction: (MainContract.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Araç Takip Sistemi") },
                actions = {
                    IconButton(onClick = { onAction(MainContract.UiAction.SignOutClicked) }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    StatisticsSection(uiState.statistics)
                }

                items(uiState.vehicles) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onLocationClick = { onAction(MainContract.UiAction.LocationClicked(vehicle)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsSection(statistics: MainContract.Statistics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Araç İstatistikleri",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticItem("Toplam", statistics.totalVehicles)
                StatisticItem("Müsait", statistics.availableVehicles)
                StatisticItem("Kullanımda", statistics.inUseVehicles)
                StatisticItem("Bakımda", statistics.inMaintenanceVehicles)
            }
        }
    }
}

@Composable
private fun StatisticItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleCard(
    vehicle: MainContract.Vehicle,
    onLocationClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = vehicle.plate,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (vehicle.status) {
                        MainContract.VehicleStatus.AVAILABLE -> "Müsait"
                        MainContract.VehicleStatus.IN_USE -> "Kullanımda"
                        MainContract.VehicleStatus.IN_MAINTENANCE -> "Bakımda"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = onLocationClick,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Haritada Gör")
                }
            }
        }
    }
}