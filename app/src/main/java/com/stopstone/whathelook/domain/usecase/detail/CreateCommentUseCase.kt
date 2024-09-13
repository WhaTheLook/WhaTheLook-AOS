package com.stopstone.whathelook.domain.usecase.detail

import com.stopstone.whathelook.data.model.response.CommentResponse
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(
        postId: Long,
        userId: Long,
        parentId: Long?,
        text: String,
        targetId: Long?
    ): CommentResponse {
        return repository.createComment(postId, userId, parentId, text, targetId)
    }
}