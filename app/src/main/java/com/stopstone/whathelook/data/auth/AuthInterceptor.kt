package com.stopstone.whathelook.data.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.stopstone.whathelook.data.local.TokenManager
import com.stopstone.whathelook.view.login.LogInActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val request = if (!accessToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        var response = chain.proceed(request)

        if (response.code == 401) {
            Log.d("AuthInterceptor", "Received 401 error. Attempting to refresh token.")
            response.close()

            val newAccessToken = runBlocking {
                refreshToken()
            }

            return if (newAccessToken != null) {
                // 새 토큰으로 요청 재시도
                chain.proceed(
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                )
            } else {
                response
            }
        }

        return response
    }

    private fun refreshToken(): String? {
        return tokenManager.getRefreshToken()
    }
}