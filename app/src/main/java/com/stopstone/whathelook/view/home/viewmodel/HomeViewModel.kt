package com.stopstone.whathelook.view.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.usecase.common.DeletePostUseCase
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
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostListItem>>(emptyList())
    val posts: StateFlow<List<PostListItem>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var lastPostId: Long? = null
    private var hasMorePosts = true

    fun loadPostList(category: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = getPostListUseCase(category, null, 20) // 초기 로드시 20개 항목
                _posts.value = response.content
                lastPostId = response.content.lastOrNull()?.id
                hasMorePosts = response.content.size == 20
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMorePosts(category: String) {
        if (_isLoading.value || !hasMorePosts) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = getPostListUseCase(category, lastPostId, 20)
                _posts.value += response.content
                lastPostId = response.content.lastOrNull()?.id
                hasMorePosts = response.content.size == 20
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

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


    private fun updatePostsList(postId: Long, likeYN: Boolean, likeCount: Int) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likeYN = likeYN, likeCount = likeCount)
            } else {
                post
            }
        }
    }

    private suspend fun getUserId(): Long = suspendCoroutine { continuation ->
        UserApiClient.instance.me { user, error ->
            when {
                error != null -> continuation.resumeWithException(error)
                user != null -> continuation.resume(user.id!!)
                else -> continuation.resumeWithException(IllegalStateException("User is null"))
            }
        }
    }

    fun deletePost(postItem: PostListItem) = viewModelScope.launch {
        Log.d("HomeViewModel", "삭제 요청: $postItem")
        runCatching { deletePostUseCase(postItem.id) }
            .onSuccess {
                Log.d("HomeViewModel", "삭제 성공: $postItem")
                _posts.value = _posts.value.filter { it.id != postItem.id }
            }
            .onFailure {
                Log.e("HomeViewModel", "삭제 실패: $postItem", it)
            }
    }
}