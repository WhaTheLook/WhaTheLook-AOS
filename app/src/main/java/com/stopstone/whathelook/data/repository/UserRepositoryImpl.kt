package com.stopstone.whathelook.data.repository

import android.util.Log
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserService,
) : UserRepository {
    override suspend fun getUserInfo(): UserInfo {
        val userInfo = userApi.getUserInfo()
        Log.d("UserRepositoryImpl", "Received user info: $userInfo")
        return userInfo
    }
}