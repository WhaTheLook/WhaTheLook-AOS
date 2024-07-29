package com.stopstone.whathelook.data.repository

import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.model.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserService
) : UserRepository {
    override suspend fun getUserInfo(): Result<UserInfo> {
        return try {
            val response = userApi.getUserInfo()
            if (response.isSuccessful) {
                response.body()?.let { userInfo ->
                    Result.success(userInfo)
                } ?: Result.failure(Exception("Response body is null"))
            } else {
                Result.failure(Exception("Failed to fetch user info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}