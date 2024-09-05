package com.stopstone.whathelook.view.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.usecase.common.DeletePostUseCase
import com.stopstone.whathelook.domain.usecase.mypage.GetUserCommentsUseCase
import com.stopstone.whathelook.domain.usecase.mypage.GetUserInfoUseCase
import com.stopstone.whathelook.domain.usecase.post.UpdateLikeStateUseCase
import com.stopstone.whathelook.domain.usecase.user.GetUserPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val updateLikeStateUseCase: UpdateLikeStateUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val getUserCommentsUseCase: GetUserCommentsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UserInfo?>(null)
    val uiState: SharedFlow<UserInfo?> = _uiState.asStateFlow()

    init {
        fetchUserInfo()
    }

    fun fetchUserInfo() = viewModelScope.launch {
        runCatching {
            getUserInfoUseCase()
        }.onSuccess { userInfo ->
            _uiState.value = userInfo
        }.onFailure { e ->
            Log.e("MyPageViewModel", "Error fetching user info", e)
        }
    }

    private val _posts = MutableStateFlow<List<PostListItem>>(emptyList())
    val posts: StateFlow<List<PostListItem>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var lastPostId: Long? = null
    private var hasMorePosts = true

    fun loadPostList() {
        if (_isLoading.value) return
        viewModelScope.launch {
            val userId = getUserId()
            _isLoading.value = true
            try {
                val response = getUserPostsUseCase(userId.toString(), null)
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

    fun loadMorePosts() {
        if (_isLoading.value || !hasMorePosts) return
        viewModelScope.launch {
            val userId = getUserId()
            _isLoading.value = true
            try {
                val response = getUserPostsUseCase(userId.toString(), lastPostId)
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
        runCatching {
            deletePostUseCase(postItem.id)
            fetchUserInfo()
        }
            .onSuccess {
                Log.d("HomeViewModel", "삭제 성공: $postItem")
                _posts.value = _posts.value.filter { it.id != postItem.id }
            }
            .onFailure {
                Log.e("HomeViewModel", "삭제 실패: $postItem", it)
            }
    }


    fun loadCommentList() {
        if (_isLoading.value) return
        viewModelScope.launch {
            val userId = getUserId()
            _isLoading.value = true
            try {
                val response = getUserCommentsUseCase(userId.toString(), null)
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

    fun loadMoreComments() {
        if (_isLoading.value || !hasMorePosts) return
        viewModelScope.launch {
            val userId = getUserId()
            _isLoading.value = true
            try {
                val response = getUserCommentsUseCase(userId.toString(), lastPostId)
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

}


