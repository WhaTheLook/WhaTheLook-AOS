package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.request.UpdateUserRequest
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.UserInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("user/info")
    @Headers("Content-Type: application/json")
    suspend fun getUserInfo(): UserInfo

    @Multipart
    @PUT("/user/update")
    suspend fun updateUser(
        @Part("userRequest") userRequest: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): String
}

interface BookmarkService {
    @GET("/user/{kakaoId}/likePost")
    suspend fun getBookmarkedPosts(
        @Path("kakaoId") kakaoId: String,
        @Query("lastPostId") lastPostId: Long?,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "recent"
    ): PostListResponse
}