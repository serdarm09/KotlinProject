package com.ornek.cartrackingsystem.ui.car

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CarInfoScreen(
    plateNumber: String,
    generalInfo: String,
    locationInfo: String,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Araç Bilgileri", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Plaka: $plateNumber")
        Text(text = "Genel Bilgiler: $generalInfo")
        Text(text = "Konum Bilgileri: $locationInfo")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            onSignOut() // Çıkış yapma fonksiyonunu çağırıyoruz
        }) {
            Text("Çıkış Yap")
        }
    }
} 