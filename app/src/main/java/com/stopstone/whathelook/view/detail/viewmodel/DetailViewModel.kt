package com.stopstone.whathelook.view.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.event.DetailEvent
import com.stopstone.whathelook.domain.usecase.common.DeletePostUseCase
import com.stopstone.whathelook.domain.usecase.detail.CreateCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.DeleteCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.GetPostDetailUseCase
import com.stopstone.whathelook.domain.usecase.detail.UpdateCommentUseCase
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
    private val updateCommentUseCase: UpdateCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {
    private val _postDetail = MutableStateFlow<PostListItem?>(null)
    val postDetail = _postDetail.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val sendMessage = MutableSharedFlow<String>()
    val message = sendMessage.asSharedFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

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

    private fun createComment(postId: Long, userId: Long, parentId: Long?, comment: String) {
        viewModelScope.launch {
            val userId = getUserId()!!
            runCatching {
                createCommentUseCase(postId, userId, parentId, comment)
            }.onSuccess { response ->
                val newComment = Comment(
                    id = response.id,
                    author = response.author,
                    text = response.text,
                    date = response.date,
                    depth = response.depth,
                )
                _comments.value = listOf(newComment) + _comments.value // 새 댓글을 리스트의 맨 앞에 추가
                _postDetail.value =
                    _postDetail.value?.copy(commentCount = _postDetail.value?.commentCount?.plus(1)!!)
                sendMessage.emit("댓글을 생성했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "createComment: $it")
            }
        }
    }


    private fun updateComment(commentId: Long, newText: String) {
        viewModelScope.launch {
            runCatching {
                updateCommentUseCase(commentId, newText)
            }.onSuccess {
                val updatedComments = _comments.value.map { comment ->
                    if (comment.id == commentId) comment.copy(text = newText) else comment
                }
                _comments.value = updatedComments
                sendMessage.emit("댓글을 수정했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "updateComment: $it")
                sendMessage.emit("댓글 수정에 실패했습니다.")
            }
        }
    }

    fun createOrUpdateComment(postId: Long, commentId: Long?, comment: String) {
        viewModelScope.launch {
            val userId = getUserId()!!
            if (commentId == null) {
                createComment(postId, userId, null, comment)
            } else {
                updateComment(commentId, comment)
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

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            deletePostUseCase(postId).collect { event ->
                _event.emit(event)
                when (event) {
                    is DetailEvent.FinishActivity -> sendMessage.emit("게시물이 삭제되었습니다.")
                }
            }
        }
    }

}
