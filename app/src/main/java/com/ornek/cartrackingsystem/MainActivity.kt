package com.ornek.cartrackingsystem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.ornek.cartrackingsystem.ui.login.LoginActivity
import com.ornek.cartrackingsystem.ui.main.MainScreen
import com.ornek.cartrackingsystem.ui.main.MainViewModel
import com.ornek.cartrackingsystem.ui.theme.CarTrackingSystemTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModel: MainViewModel

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

        setContent {
            CarTrackingSystemTheme {
                val uiState by viewModel.uiState.collectAsState()
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
}