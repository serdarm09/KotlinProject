package com.ornek.cartrackingsystem.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ornek.cartrackingsystem.ui.main.MainActivity
import com.ornek.cartrackingsystem.ui.login.LoginContract.UiEffect
import com.ornek.cartrackingsystem.ui.theme.CarTrackingSystemTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CarTrackingSystemTheme {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(key1 = Unit) {
                    viewModel.uiEffect.collect { effect ->
                        when (effect) {
                            is UiEffect.NavigateToMain -> {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                            is UiEffect.ShowError -> {
                                Toast.makeText(
                                    this@LoginActivity,
                                    effect.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                LoginScreen(
                    uiState = uiState,
                    onAction = viewModel::onAction
                )
            }
        }
    }
}