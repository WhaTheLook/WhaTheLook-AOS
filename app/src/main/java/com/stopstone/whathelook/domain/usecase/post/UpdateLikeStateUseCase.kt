package com.stopstone.whathelook.domain.usecase.post

import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.UpdateLikeResponse
import com.stopstone.whathelook.domain.repository.post.PostListRepository
import javax.inject.Inject

class UpdateLikeStateUseCase @Inject constructor(
    private val repository: PostListRepository
) {
    suspend operator fun invoke(postItem: PostListItem, userId: Long): UpdateLikeResponse {
        return repository.updateLikeState(postItem, userId)
    }
}