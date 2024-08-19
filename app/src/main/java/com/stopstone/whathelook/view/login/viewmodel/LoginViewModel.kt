package com.stopstone.whathelook.view.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.local.TokenManager
import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.usecase.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState

    fun login(kakaoAccessToken: String) = viewModelScope.launch {
        try {
            val tokens = loginUseCase(kakaoAccessToken)
            tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
            _loginState.value = true
            Log.d("LoginViewModel", "카카오 로그인 성공")
        } catch (e: Exception) {
            _loginState.value = false
            Log.e("LoginViewModel", "카카오 로그인 실패")
        }
    }
}