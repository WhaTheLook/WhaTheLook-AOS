package com.stopstone.whathelook.util

import android.util.Log
import com.stopstone.whathelook.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val newRequest = if (!accessToken.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "Adding Authorization Header: Bearer ${accessToken.take(10)}...")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Log.w("AuthInterceptor", "No access token available")
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}