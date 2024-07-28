package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.LoginRequest
import com.stopstone.whathelook.data.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {
    @Headers("Content-Type: application/json")
    @POST("user/token/login")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse
}