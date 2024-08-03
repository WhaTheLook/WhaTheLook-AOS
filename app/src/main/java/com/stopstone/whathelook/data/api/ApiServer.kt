package com.stopstone.whathelook.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/post/create")
    suspend fun createPost(
        @Part("postRequest") postRequest: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): String
}