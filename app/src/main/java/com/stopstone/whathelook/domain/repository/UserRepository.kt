package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.response.UserInfo
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun getUserInfo(): UserInfo
    suspend fun updateUser(jsonPart: RequestBody, imagePart: MultipartBody.Part?): Result<String>

}