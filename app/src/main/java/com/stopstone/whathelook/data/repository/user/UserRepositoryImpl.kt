package com.stopstone.whathelook.data.repository.user

import android.util.Log
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun getUserPosts(kakaoId: String, lastPostId: Long?): PostListResponse {
        return userService.getUserPosts(
            kakaoId = kakaoId,
            size = 10,
            sortBy = "recent"
        )
    }

    override suspend fun getUserComments(kakaoId: String, lastPostId: Long?): PostListResponse {
        return userService.getUserComments(
            kakaoId = kakaoId,
            size = 10,
            sortBy = "recent"
        )
    }
}