package com.stopstone.whathelook.util

import android.util.Log
import com.stopstone.whathelook.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = "Bearer ${tokenManager.getAccessToken()}"

        val newRequest = if (tokenManager.getAccessToken() != null) {
            Log.d("AuthInterceptor", "Adding Authorization Header")
            originalRequest.newBuilder()
                .header("Authorization", accessToken)
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
