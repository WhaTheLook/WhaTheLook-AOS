package com.stopstone.whathelook.domain.usecase.detail

import com.stopstone.whathelook.data.model.response.ChildCommentsResponse
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class GetChildCommentsUseCase @Inject constructor(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(postId: Long, parentId: Long, lastCommentId: Long?, size: Int): ChildCommentsResponse {
        return repository.getChildComments(postId, parentId, lastCommentId, size)
    }
}