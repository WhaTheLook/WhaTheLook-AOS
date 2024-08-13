package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.data.model.UpdateLikeResponse
import com.stopstone.whathelook.domain.repository.PostListRepository
import javax.inject.Inject

class UpdateLikeStateUseCase @Inject constructor(
    private val repository: PostListRepository
) {
    suspend operator fun invoke(postItem: PostListItem, userId: Long): UpdateLikeResponse {
        return repository.updateLikeState(postItem, userId)
    }
}