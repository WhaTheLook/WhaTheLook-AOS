package com.stopstone.whathelook.view.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.usecase.mypage.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UserInfo?>(null)
    val uiState: SharedFlow<UserInfo?> = _uiState.asStateFlow()

    init {
        fetchUserInfo()
    }

    fun fetchUserInfo() = viewModelScope.launch {
        runCatching {
            getUserInfoUseCase()
        }.onSuccess { userInfo ->
            _uiState.value = userInfo
        }.onFailure { e ->
            Log.e("MyPageViewModel", "Error fetching user info", e)
        }
    }
}
