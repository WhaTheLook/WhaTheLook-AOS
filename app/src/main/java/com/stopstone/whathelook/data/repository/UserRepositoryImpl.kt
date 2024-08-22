package com.stopstone.whathelook.data.repository

import android.util.Log
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
) : UserRepository {
    override suspend fun getUserInfo(): UserInfo {
        val userInfo = userService.getUserInfo()
        Log.d("UserRepositoryImpl", "Received user info: $userInfo")
        return userInfo
    }

    override suspend fun updateUser(jsonPart: RequestBody, imagePart: MultipartBody.Part?): Result<String> = runCatching {
        userService.updateUser(jsonPart, imagePart)
    }.onSuccess {
        Log.d("UserRepositoryImpl", "User updated successfully")
    }.onFailure {
        Log.e("UserRepositoryImpl", "Error updating user", it)
        Log.e("UserRepositoryImpl", "Error updating user", it.cause)
        Log.e("UserRepositoryImpl", "Error updating user ${it.message}")
    }
}