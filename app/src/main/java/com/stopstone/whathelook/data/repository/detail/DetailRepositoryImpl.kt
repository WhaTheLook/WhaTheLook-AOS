package com.stopstone.whathelook.data.repository.detail

import android.util.Log
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.request.RequestComment
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
        text: String
    ): CommentResponse {
        val requestComment = RequestComment(
            postId = postId,
            userId = userId,
            text = text
        )
        val response =  postApiService.createComment(requestComment)
        Log.d("DetailRepositoryImpl", "createComment: $response")
        return response
    }

    override suspend fun deleteComment(commentId: Long): String {
        return postApiService.deleteComment(commentId)
    }
}