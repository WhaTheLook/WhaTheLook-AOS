package com.stopstone.whathelook.view.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.usecase.detail.CreateCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.DeleteCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.GetPostDetailUseCase
import com.stopstone.whathelook.domain.usecase.post.UpdateLikeStateUseCase
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
    private val updateLikeStateUseCase: UpdateLikeStateUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
) : ViewModel() {
    private val _postDetail = MutableStateFlow<PostListItem?>(null)
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
            }.onSuccess { response ->
                val newComment = Comment(
                    id = response.id,
                    author = response.author,
                    text = response.text,
                    date = response.date,
                    depth = response.depth,
                )
                _comments.value = listOf(newComment) + _comments.value // 새 댓글을 리스트의 맨 앞에 추가
                _postDetail.value = _postDetail.value?.copy(commentCount = _postDetail.value?.commentCount?.plus(1)!!)
                sendMessage.emit("댓글을 생성했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "createComment: $it")
            }
        }
    }

    fun updateLikeState(postItem: PostListItem) = viewModelScope.launch {
        val userId = getUserId()
        runCatching {
            val likeState = updateLikeStateUseCase(postItem, userId!!)
            _postDetail.value =
                _postDetail.value?.copy(likeYN = likeState.likeYN, likeCount = likeState.likeCount)
        }.onSuccess {
            Log.d("HomeViewModel", "좋아요 상태 변경 성공")
        }.onFailure { e ->
            Log.e("HomeViewModel", "좋아요 상태 변경 성공 실패", e)
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            runCatching {
                deleteCommentUseCase(commentId)
            }.onSuccess {
                _comments.value = _comments.value.filter { it.id != commentId } // 댓글 목록 업데이트
                _postDetail.value = _postDetail.value?.copy(commentCount = _postDetail.value?.commentCount?.minus(1)!!)
                sendMessage.emit("댓글을 삭제했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "deleteComment: $it")
            }
        }
    }

}
