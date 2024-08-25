package com.stopstone.whathelook.utils

import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object KakaoUserUtil {

    /**
     * Kakao SDK를 사용하여 현재 로그인한 사용자의 ID를 비동기적으로 가져옵니다.
     *
     * @return 사용자 ID (Long)
     * @throws Exception Kakao SDK에서 오류가 발생하거나 사용자 정보를 가져올 수 없는 경우
     */
    suspend fun getUserId(): Long? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            UserApiClient.instance.me { user, error ->
                when {
                    error != null -> continuation.resumeWithException(error)
                    user != null -> continuation.resume(user.id)
                    else -> continuation.resume(null)
                }
            }
        }
    }
}