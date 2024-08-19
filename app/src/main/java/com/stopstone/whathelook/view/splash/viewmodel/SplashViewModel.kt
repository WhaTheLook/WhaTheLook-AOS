package com.stopstone.whathelook.view.splash.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.local.TokenManager
import com.stopstone.whathelook.domain.usecase.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _tokenState = MutableStateFlow<Boolean?>(null)
    val tokenState: StateFlow<Boolean?> = _tokenState

    fun validateToken() = viewModelScope.launch {
        val hasToken = tokenManager.getAccessToken() != null
        _tokenState.value = hasToken
        Log.d("SplashViewModel", "Access Token exists: $hasToken")
        Log.d("SplashViewModel", "Access Token: ${tokenManager.getAccessToken()}")
    }
}
