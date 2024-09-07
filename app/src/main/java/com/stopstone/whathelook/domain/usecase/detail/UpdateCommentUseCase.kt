package com.stopstone.whathelook.domain.usecase.detail

import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class UpdateCommentUseCase @Inject constructor(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(commentId: Long, newText: String) {
        repository.updateComment(commentId, newText)
    }
}