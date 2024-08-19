package com.stopstone.whathelook.view.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.usecase.post.GetPostListUseCase
import com.stopstone.whathelook.domain.usecase.post.UpdateLikeStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase,
    private val updateLikeStateUseCase: UpdateLikeStateUseCase,
) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostListItem>>(emptyList())
    val posts: StateFlow<List<PostListItem>> = _posts.asStateFlow()

    fun loadPostList(category: String) = viewModelScope.launch {
        val posts = getPostListUseCase.invoke(category).content
        _posts.value = posts
    }

    // 좋아요 클릭시 상태 반영하기
    fun updateLikeState(postItem: PostListItem) = viewModelScope.launch {
        val userId = getUserId()
        runCatching {
            val likeState = updateLikeStateUseCase(postItem, userId)
            updatePostsList(postItem.id, likeState.likeYN, likeState.likeCount)
        }.onSuccess {
            Log.d("HomeViewModel", "좋아요 상태 변경 성공")
        }.onFailure { e ->
            Log.e("HomeViewModel", "좋아요 상태 변경 성공 실패", e)
        }
    }

    // 좋아요 클릭시 상태 변경
    private fun updatePostsList(postId: Long, likeYN: Boolean, likeCount: Int) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likeYN = likeYN, likeCount = likeCount)
            } else {
                post
            }
        }
    }

    // 유저 아이디 API 호출로 가져오기
    private suspend fun getUserId(): Long = suspendCoroutine { continuation ->
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> continuation.resumeWithException(error)
                user != null -> continuation.resume(user.id!!)
                else -> continuation.resumeWithException(IllegalStateException("User is null"))
            }
        }
    }
}