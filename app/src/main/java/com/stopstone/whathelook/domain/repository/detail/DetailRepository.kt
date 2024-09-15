package com.stopstone.whathelook.domain.repository.detail

import com.stopstone.whathelook.data.model.response.ChildCommentsResponse
import com.stopstone.whathelook.data.model.response.CommentResponse
import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse

interface DetailRepository {
    suspend fun getPostDetail(postId: Long): PostListItem
    suspend fun createComment(
        postId: Long,
        userId: Long,
        parentId: Long?,
        text: String,
        targetId: Long?
    ): CommentResponse
    suspend fun deleteComment(commentId: Long): String
    suspend fun updateComment(commentId: Long, text: String): String
    suspend fun getChildComments(
        postId: Long,
        parentId: Long,
        lastCommentId: Long?,
        size: Int
    ): ChildCommentsResponse
    suspend fun acceptComment(postId: Long, commentId: Long): String
}