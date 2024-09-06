package com.stopstone.whathelook.view.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository
import com.stopstone.whathelook.domain.repository.SearchRepository
import com.stopstone.whathelook.domain.usecase.SearchPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.domain.usecase.common.DeletePostUseCase
import com.stopstone.whathelook.domain.usecase.post.UpdateLikeStateUseCase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository,
    private val searchPostsUseCase: SearchPostsUseCase,
    private val updateLikeStateUseCase: UpdateLikeStateUseCase,
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PostListItem>>(emptyList())
    val searchResults: StateFlow<List<PostListItem>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            recentSearchRepository.getRecentSearches().collect {
                _recentSearches.value = it
            }
        }
    }

    fun addSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.addSearch(query)
        }
    }

    fun deleteSearch(search: RecentSearch) {
        viewModelScope.launch {
            recentSearchRepository.deleteSearch(search)
        }
    }

    fun clearAllSearches() {
        viewModelScope.launch {
            recentSearchRepository.deleteAllSearches()
        }
    }

    fun searchPosts(query: String) {
        viewModelScope.launch {
            Log.d("SearchViewModel", "검색 요청: 쿼리='$query'")
            runCatching {
                searchPostsUseCase(search = query)
            }.onSuccess { response ->
                _searchResults.value = response.posts.content
                Log.d("SearchViewModel", "검색 성공: ${response.posts.content.size}개의 결과")
                response.posts.content.forEach { post ->
                    Log.d("SearchViewModel", "게시물: ID=${post.id}, 제목='${post.title}'")
                }
                addSearch(query)  // 성공적인 검색 후 최근 검색어에 추가
            }.onFailure { error ->
                Log.e("SearchViewModel", "검색 실패: ${error.message}", error)
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
        _searchResults.value = _searchResults.value.map { post ->
            if (post.id == postId) {
                post.copy(likeYN = likeYN, likeCount = likeCount)
            } else {
                post
            }
        }
    }

    fun deletePost(postItem: PostListItem) = viewModelScope.launch {
        Log.d("HomeViewModel", "삭제 요청: $postItem")
        runCatching { deletePostUseCase(postItem.id) }
            .onSuccess {
                Log.d("HomeViewModel", "삭제 성공: $postItem")
                _searchResults.value = _searchResults.value.filter { it.id != postItem.id }
            }
            .onFailure {
                Log.e("HomeViewModel", "삭제 실패: $postItem", it)
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

}