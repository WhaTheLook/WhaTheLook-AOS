package com.stopstone.whathelook.domain.usecase.detail

import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val detailRepository: DetailRepository
) {
    suspend operator fun invoke(commentId: Long) {
        detailRepository.deleteComment(commentId)
    }
}