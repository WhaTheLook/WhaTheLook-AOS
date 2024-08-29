package com.stopstone.whathelook.view.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.domain.usecase.detail.CreateCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.GetPostDetailUseCase
import com.stopstone.whathelook.utils.KakaoUserUtil.getUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
) : ViewModel() {
    private val _postDetail = MutableStateFlow<PostDetailResponse?>(null)
    val postDetail = _postDetail.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val sendMessage = MutableSharedFlow<String>()
    val message = sendMessage.asSharedFlow()

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            runCatching {
                getPostDetailUseCase(postId)
            }.onSuccess { response ->
                _postDetail.value = response
                _comments.value = response.comments ?: emptyList()
                Log.d("DetailViewModel", "getPostDetail: $response")
                Log.d("DetailViewModel", "Comments: ${_comments.value}")
                Log.d("DetailViewModel", "Comment count: ${_comments.value.size}")
                _comments.value.forEachIndexed { index, comment ->
                    Log.d("DetailViewModel", "Comment $index: $comment")
                    Log.d("DetailViewModel", "Comment $index author: ${comment.author}")
                }
            }.onFailure {
                Log.e("DetailViewModel", "getPostDetail: $it")
            }
        }
    }

    fun createComment(id: Long, comment: String, parentId: Long? = null) {
        viewModelScope.launch {
            val userId = getUserId()!!
            runCatching {
                createCommentUseCase(id, userId, parentId, comment)
            }.onSuccess {
                Log.d("DetailViewModel", "createComment: $it")
                sendMessage.emit("댓글을 생성했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "createComment: $it")
            }
        }
    }
}