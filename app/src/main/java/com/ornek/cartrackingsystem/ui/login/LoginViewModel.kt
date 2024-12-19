package com.ornek.cartrackingsystem.ui.login

import androidx.lifecycle.ViewModel
import com.ornek.cartrackingsystem.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()