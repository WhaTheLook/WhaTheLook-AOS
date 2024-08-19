package com.stopstone.whathelook.data.repository

import android.util.Log
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.local.UserManager
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserService,
    private val userManager: UserManager,
) : UserRepository {
    override suspend fun getUserInfo(): UserInfo {
        userManager.clearUserInfo()
        val userInfo = userManager.getUserInfo()
        Log.d("UserRepositoryImpl", "getUserInfo: $userInfo")

        return userInfo!!
    }

    override fun getUserInfoFlow(): Flow<UserInfo?> = userManager.userInfoFlow

    override suspend fun clearUserInfo() {
        userManager.clearUserInfo()
    }
}