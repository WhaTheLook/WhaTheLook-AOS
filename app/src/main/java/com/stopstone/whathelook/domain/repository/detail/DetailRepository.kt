package com.stopstone.whathelook.domain.repository.detail

import com.stopstone.whathelook.data.model.response.CommentResponse
import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.data.model.response.PostListResponse

interface DetailRepository {
    suspend fun getPostDetail(postId: Long): PostDetailResponse
    suspend fun createComment(
        postId: Long,
        userId: Long,
        parentId: Long?,
        text: String
    ): CommentResponse
}