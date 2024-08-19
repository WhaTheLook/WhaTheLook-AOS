package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.UserInfo
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("user/info")
    @Headers("Content-Type: application/json")
    suspend fun getUserInfo(): UserInfo
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