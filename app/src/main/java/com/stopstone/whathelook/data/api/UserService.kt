package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface UserService {
    @GET("user/info")
    @Headers("Content-Type: application/json")
    suspend fun getUserInfo(): Response<UserInfo>
}