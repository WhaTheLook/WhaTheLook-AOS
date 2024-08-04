package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.PostListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("category") category: String,
    ): PostListResponse

}