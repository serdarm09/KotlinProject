package com.ornek.cartrackingsystem.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ornek.cartrackingsystem.ui.theme.FirebaseSamplesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseSamplesTheme {
                LoginScreen(
                    onNavigateToMain = {
                        finish()
                    }
                )
            }
        }
    }
} 