package com.stopstone.whathelook.view.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.Author
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.event.DetailEvent
import com.stopstone.whathelook.domain.usecase.common.DeletePostUseCase
import com.stopstone.whathelook.domain.usecase.detail.AcceptCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.CreateCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.DeleteCommentUseCase
import com.stopstone.whathelook.domain.usecase.detail.GetChildCommentsUseCase
import com.stopstone.whathelook.domain.usecase.detail.GetPostDetailUseCase
import com.stopstone.whathelook.domain.usecase.detail.UpdateCommentUseCase
import com.stopstone.whathelook.domain.usecase.post.UpdateLikeStateUseCase
import com.stopstone.whathelook.utils.KakaoUserUtil.getUserId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val getChildCommentsUseCase: GetChildCommentsUseCase,
    private val acceptCommentUseCase: AcceptCommentUseCase,
) : ViewModel() {
    private val _postDetail = MutableStateFlow<PostListItem?>(null)
    val postDetail = _postDetail.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val sendMessage = MutableSharedFlow<String>()
    val message = sendMessage.asSharedFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    private val _parentComment = MutableStateFlow<Comment?>(null)
    val parentComment = _parentComment.asStateFlow()

    private val _childComments = MutableStateFlow<Map<Long, List<Comment>>>(emptyMap())
    val childComments = _childComments.asStateFlow()


    fun setReplyTarget(comment: Comment?) {
        _parentComment.value = comment
        Log.d("DetailViewModel", "답글 대상 설정: 이름=${comment?.author?.name ?: "없음"}, ID=${comment?.author?.kakaoId ?: "없음"}, 부모 댓글 ID=${comment?.id ?: "없음"}")
    }

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            runCatching {
                getPostDetailUseCase(postId)
            }.onSuccess { response ->
                _postDetail.value = response
                _comments.value = response.comments ?: emptyList()
                Log.d("DetailViewModel", "게시물 상세 정보 조회 성공: $response")
                Log.d("DetailViewModel", "댓글 목록: ${_comments.value}")
                Log.d("DetailViewModel", "댓글 수: ${_comments.value.size}")
                _comments.value.forEachIndexed { index, comment ->
                    Log.d("DetailViewModel", "댓글 $index: $comment")
                    Log.d("DetailViewModel", "댓글 $index 작성자: ${comment.author}")
                }
            }.onFailure {
                Log.e("DetailViewModel", "게시물 상세 정보 조회 실패: $it")
            }
        }
    }

    fun createOrUpdateComment(postId: Long, commentId: Long?, comment: String) {
        viewModelScope.launch {
            val userId = getUserId() ?: return@launch

            val parentComment = _parentComment.value
            val isReply = parentComment != null
            val parentId = parentComment?.id
            val targetId = parentComment?.author?.kakaoId

            val commentText = if (isReply && comment.startsWith("@")) {
                comment.substringAfter(" ").trim()
            } else {
                comment
            }

            Log.d("DetailViewModel", "댓글 생성 시도: 대댓글여부=$isReply, 부모댓글ID=$parentId, 대상사용자ID=$targetId, 댓글내용=$commentText, 답글대상=${parentComment?.author?.name}")

            if (commentId == null) {
                createComment(postId, userId, parentId, commentText, targetId, isReply)
            } else {
                updateComment(commentId, commentText)
            }

            // 댓글 작성/수정 후 처리
            _parentComment.value = null
        }
    }

    private fun createComment(postId: Long, userId: Long, parentId: Long?, comment: String, targetId: String?, isReply: Boolean) {
        viewModelScope.launch {
            runCatching {
                createCommentUseCase(postId, userId, parentId, comment, targetId?.toLong())
            }.onSuccess { response ->
                val newComment = Comment(
                    id = response.id,
                    author = response.author,
                    text = response.text,
                    date = response.date,
                    accept = response.accept,
                    children = response.children,
                    targetUser = response.targetUser
                )
                updateCommentsList(newComment, parentId)
                if (isReply) {
                    Log.d("DetailViewModel", "답글 입력 성공. 답글 ID: ${response.id}, 부모 댓글 ID: $parentId, 대상 사용자 ID: $targetId")
                    sendMessage.emit("답글이 입력되었습니다.")
                } else {
                    Log.d("DetailViewModel", "댓글 입력 성공. 댓글 ID: ${response.id}")
                    sendMessage.emit("댓글이 입력되었습니다.")
                }
            }.onFailure {
                Log.e("DetailViewModel", "댓글 생성 실패: ${it.message}")
                sendMessage.emit("댓글 생성에 실패했습니다.")
            }
        }
    }

    private fun updateCommentsList(newComment: Comment, parentId: Long?) {
        _comments.value = _comments.value.let { currentComments ->
            if (parentId == null) {
                Log.d("DetailViewModel", "일반 댓글 추가: ${newComment.id}")
                listOf(newComment) + currentComments
            } else {
                Log.d("DetailViewModel", "대댓글 추가: ${newComment.id}, 부모 댓글 ID: $parentId")
                currentComments.map { comment ->
                    if (comment.id == parentId) {
                        comment.copy(children = comment.children + newComment)
                    } else {
                        comment
                    }
                }
            }
        }

        _postDetail.value = _postDetail.value?.copy(
            commentCount = (_postDetail.value?.commentCount ?: 0) + 1
        )
        Log.d("DetailViewModel", "댓글 목록 업데이트 완료. 총 댓글 수: ${_postDetail.value?.commentCount}")
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
                Log.d("DetailViewModel", "댓글 수정 성공. 댓글 ID: $commentId, 새로운 내용: $newText")
                sendMessage.emit("댓글을 수정했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "댓글 수정 실패: $it")
                sendMessage.emit("댓글 수정에 실패했습니다.")
            }
        }
    }

    fun updateLikeState(postItem: PostListItem) = viewModelScope.launch {
        val userId = getUserId()
        runCatching {
            val likeState = updateLikeStateUseCase(postItem, userId!!)
            _postDetail.value = _postDetail.value?.copy(likeYN = likeState.likeYN, likeCount = likeState.likeCount)
            Log.d("DetailViewModel", "좋아요 상태 변경: 게시물 ID=${postItem.id}, 좋아요=${likeState.likeYN}, 좋아요 수=${likeState.likeCount}")
        }.onSuccess {
            Log.d("DetailViewModel", "좋아요 상태 변경 성공")
        }.onFailure { e ->
            Log.e("DetailViewModel", "좋아요 상태 변경 실패", e)
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            runCatching {
                deleteCommentUseCase(commentId)
            }.onSuccess {
                _comments.value = _comments.value.filter { it.id != commentId }
                _postDetail.value = _postDetail.value?.copy(commentCount = _postDetail.value?.commentCount?.minus(1)!!)
                Log.d("DetailViewModel", "댓글 삭제 성공. 댓글 ID: $commentId, 남은 댓글 수: ${_postDetail.value?.commentCount}")
                sendMessage.emit("댓글을 삭제했습니다.")
            }.onFailure {
                Log.e("DetailViewModel", "댓글 삭제 실패: $it")
            }
        }
    }

    fun getChildComments(postId: Long, parentId: Long, lastCommentId: Long? = null, size: Int = 10) {
        viewModelScope.launch {
            try {
                val response = getChildCommentsUseCase(postId, parentId, lastCommentId, size)
                _childComments.update { currentMap ->
                    val currentList = currentMap[parentId] ?: emptyList()
                    currentMap + (parentId to (currentList + response.content))
                }
                Log.d("DetailViewModel", "Child comments loaded for parent $parentId: ${response.content.size} comments, hasNext=${!response.last}")
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading child comments", e)
            }
        }
    }

    fun toggleCommentAccept(postId: Long, commentId: Long) {
        viewModelScope.launch {
            try {
                val result = acceptCommentUseCase(postId, commentId)
                Log.d("DetailViewModel", "댓글 채택 상태 변경 성공: $result")
                val newAcceptStatus = updateCommentAcceptStatus(commentId)
                val messageText = if (newAcceptStatus) {
                    "댓글이 채택되었습니다."
                } else {
                    "댓글 채택이 취소되었습니다."
                }
                sendMessage.emit(messageText)
                Log.d("DetailViewModel", "메시지 전송: $messageText")
            } catch (e: Exception) {
                Log.e("DetailViewModel", "댓글 채택 상태 변경 실패: ${e.message}")
                sendMessage.emit("댓글 채택 상태 변경에 실패했습니다.")
            }
        }
    }

    private fun updateCommentAcceptStatus(commentId: Long): Boolean {
        var newAcceptStatus = false
        _comments.update { comments ->
            comments.map { comment ->
                if (comment.id == commentId) {
                    newAcceptStatus = !comment.accept
                    Log.d("DetailViewModel", "댓글 채택 상태 업데이트: 댓글 ID=$commentId, 새로운 상태=$newAcceptStatus")
                    comment.copy(accept = newAcceptStatus)
                } else {
                    comment
                }
            }
        }
        return newAcceptStatus
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            deletePostUseCase(postId).collect { event ->
                _event.emit(event)
                when (event) {
                    is DetailEvent.FinishActivity -> {
                        Log.d("DetailViewModel", "게시물 삭제 성공. 게시물 ID: $postId")
                        sendMessage.emit("게시물이 삭제되었습니다.")
                    }
                }
            }
        }
    }
}