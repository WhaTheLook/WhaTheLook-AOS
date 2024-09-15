package com.stopstone.whathelook.data.repository.detail

import android.util.Log
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.request.RequestComment
import com.stopstone.whathelook.data.model.request.RequestUpdateComment
import com.stopstone.whathelook.data.model.response.ChildCommentsResponse
import com.stopstone.whathelook.data.model.response.CommentResponse
import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService
) : DetailRepository {
    override suspend fun getPostDetail(postId: Long): PostListItem {
        return postApiService.getPostDetail(postId)
    }

    override suspend fun createComment(
        postId: Long,
        userId: Long,
        parentId: Long?,
        text: String,
        targetId: Long?
    ): CommentResponse {
        val requestComment = RequestComment(
            postId = postId,
            userId = userId,
            text = text,
            parentId = parentId,
            targetId = targetId
        )
        val response = postApiService.createComment(requestComment)
        Log.d("DetailRepositoryImpl", "createComment: $response")
        return response
    }

    override suspend fun deleteComment(commentId: Long): String {
        return postApiService.deleteComment(commentId)
    }

    override suspend fun updateComment(commentId: Long, text: String): String {
        val request = RequestUpdateComment(
            commentId = commentId,
            text = text
        )
        return postApiService.updateComment(commentId, request)
    }

    override suspend fun getChildComments(
        postId: Long,
        parentId: Long,
        lastCommentId: Long?,
        size: Int
    ): ChildCommentsResponse {
        return postApiService.getChildComments(postId, parentId, lastCommentId, size)
    }

    override suspend fun acceptComment(postId: Long, commentId: Long): String {
        return postApiService.acceptComment(postId, commentId).also {
            Log.d("DetailRepositoryImpl", "댓글 채택 요청 완료: 게시글 ID=$postId, 댓글 ID=$commentId")
        }
    }
}