package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.UserInfo
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {
    suspend fun getUserInfo(): UserInfo
    suspend fun updateUser(jsonPart: RequestBody, imagePart: MultipartBody.Part?): Result<String>
    suspend fun getUserPosts(kakaoId: String, lastPostId: Long?): PostListResponse
    suspend fun getUserComments(kakaoId: String, lastPostId: Long?): PostListResponse
}