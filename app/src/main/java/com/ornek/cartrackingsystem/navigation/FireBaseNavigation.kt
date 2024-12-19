package com.ornek.cartrackingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ornek.cartrackingsystem.ui.car.CarInfoScreen
import com.ornek.cartrackingsystem.ui.login.LoginScreen
import com.ornek.cartrackingsystem.ui.login.LoginViewModel
import com.ornek.cartrackingsystem.ui.main.MainScreen
import com.ornek.cartrackingsystem.ui.main.MainViewModel

@Composable
fun FirebaseNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {


        composable("car_info") {
            CarInfoScreen(
                plateNumber = "34ABC123",
                generalInfo = "Genel Bilgiler Burada",
                locationInfo = "Konum Bilgileri Burada",
                onSignOut = { navController.navigate("login") }
            )
        }

        composable("main") {
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect

        }
    }
}