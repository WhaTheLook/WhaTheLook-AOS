package com.stopstone.whathelook.data.auth

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.local.TokenManager
import com.stopstone.whathelook.data.model.response.TokenResponse
import com.stopstone.whathelook.view.login.LogInActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val loginService: LoginService,
    private val context: Context
) : Interceptor {

    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d(TAG, "원본 요청 URL: ${originalRequest.url}")

        val accessToken = tokenManager.getAccessToken()
        Log.d(TAG, "저장된 액세스 토큰: $accessToken")

        val request = if (!accessToken.isNullOrEmpty()) {
            Log.d(TAG, "액세스 토큰이 존재합니다. 요청에 토큰을 추가합니다.")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Log.d(TAG, "액세스 토큰이 없습니다. 원본 요청을 그대로 사용합니다.")
            originalRequest
        }

        val response = chain.proceed(request)
        Log.d(TAG, "서버 응답 코드: ${response.code}")

        if (response.code == 401) {
            Log.w(TAG, "401 오류 발생. 토큰이 만료되었을 수 있습니다. 토큰 갱신을 시도합니다.")
            response.close()

            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                Log.d(TAG, "리프레시 토큰 존재: ${refreshToken.take(10)}...")
                val newTokens = runBlocking {
                    refreshToken(refreshToken)
                }

                if (newTokens != null) {
                    Log.i(TAG, "토큰 갱신 성공. 새 액세스 토큰: ${newTokens.accessToken.take(10)}...")
                    tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    Log.d(TAG, "새 토큰을 저장했습니다.")

                    Log.d(TAG, "새 토큰으로 원본 요청을 재시도합니다.")
                    return chain.proceed(
                        originalRequest.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                    )
                } else {
                    Log.e(TAG, "토큰 갱신 실패.")
                    performLogoutAndRedirect()
                }
            } else {
                Log.e(TAG, "리프레시 토큰이 존재하지 않습니다.")
                performLogoutAndRedirect()
            }
        }

        return response
    }

    private suspend fun refreshToken(refreshToken: String): TokenResponse? {
        return try {
            Log.d(TAG, "토큰 갱신 API 호출 시작")
            val result = loginService.refreshToken("Bearer $refreshToken")
            Log.d(TAG, "토큰 갱신 API 호출 성공")
            result
        } catch (e: Exception) {
            Log.e(TAG, "토큰 갱신 중 오류 발생", e)
            null
        }
    }

    private fun performLogoutAndRedirect() {
        Log.w(TAG, "로그아웃 처리 및 로그인 화면으로 이동합니다.")
        tokenManager.clearToken()
        Log.d(TAG, "저장된 토큰을 모두 삭제했습니다.")

        // UI 관련 작업은 메인 스레드에서 실행
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(context, LogInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
            Log.i(TAG, "로그인 화면으로 이동했습니다.")
        }
    }
}