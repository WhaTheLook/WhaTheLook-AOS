package com.stopstone.whathelook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(kakaoAccessToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = loginUseCase.execute(kakaoAccessToken)
                _loginState.value = LoginState.Success(result)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Success(val tokens: Tokens) : LoginState()
    data class Error(val message: String) : LoginState()
}
