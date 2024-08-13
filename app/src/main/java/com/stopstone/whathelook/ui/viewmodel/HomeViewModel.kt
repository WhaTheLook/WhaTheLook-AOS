package com.stopstone.whathelook.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.domain.usecase.GetPostListUseCase
import com.stopstone.whathelook.domain.usecase.UpdateLikeStateUseCase
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
        Log.d("HomeViewModel", "Loaded posts: $posts")
    }

    fun updateLikeState(postItem: PostListItem) = viewModelScope.launch {
        try {
            val userId = getUserId()
            val likeState = updateLikeStateUseCase.invoke(postItem, userId)
            updatePostsList(postItem.id, likeState.likeYN, likeState.likeCount)
            Log.d("HomeViewModel", "updateLikeState: $likeState")
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error updating like state", e)
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

    private fun updatePostsList(postId: Long, likeYN: Boolean, likeCount: Int) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likeYN = likeYN, likeCount = likeCount)
            } else {
                post
            }
        }
    }
}