package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.request.UpdateLikeRequest
import com.stopstone.whathelook.data.model.response.UpdateLikeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PostApiService {
    @Multipart
    @POST("/post/create")
    suspend fun createPost(
        @Part("postRequest") postRequest: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): String

    @GET("/post/postList")
    suspend fun getPostList(
        @Query("lastPostId") lastPostId: Long? = null,
        @Query("category") category: String,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "recent"

    ): PostListResponse


    @POST("/post/like")
    suspend fun updateLike(
        @Body requestLike: UpdateLikeRequest
    ): UpdateLikeResponse
}