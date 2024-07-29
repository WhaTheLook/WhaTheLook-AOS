package com.stopstone.whathelook.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.UserInfo
import com.stopstone.whathelook.domain.model.User
import com.stopstone.whathelook.domain.usecase.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<User?>(null)
    val uiState: StateFlow<User?> = _uiState.asStateFlow()

    init {
        fetchUserInfo()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val result = getUserInfoUseCase()

                if (result.isSuccess) {
                    result.getOrNull()?.let { userInfo ->
                        val user = userInfo.toUser(userInfo)
                        _uiState.value = user
                    }
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "Error fetching user info", e)
            }
        }
    }

    fun UserInfo.toUser(userInfo: UserInfo): User {
        return User(
            name = userInfo.name,
            imageUrl = userInfo.profileImage
        )
    }
}
