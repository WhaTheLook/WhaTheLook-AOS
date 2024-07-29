package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.LoginRequest
import com.stopstone.whathelook.data.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {
    @POST("user/token/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): TokenResponse
}