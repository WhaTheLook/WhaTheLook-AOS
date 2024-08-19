package com.stopstone.whathelook.data.repository.bookmark

import android.util.Log
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.data.api.BookmarkService
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.domain.repository.bookmark.BookmarkRepository
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkService: BookmarkService,
) : BookmarkRepository {
    override suspend fun getBookmarks(): PostListResponse {
        return bookmarkService.getBookmarkedPosts(getKakaoId().toString(), lastPostId = null)
    }

    private fun getKakaoId(): Long? {
        // 카카오 로그인 정보 가져오기
        var kakaoId: Long? = null
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.i("KakaoLogin", "사용자 정보 요청 성공")
                Log.i("KakaoLogin", "사용자 아이디: ${user.id}")
                kakaoId = user.id
            }
        }
        return kakaoId
    }
}