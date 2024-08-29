package com.stopstone.whathelook.domain.usecase.detail

import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class GetPostDetailUseCase @Inject constructor(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(postId: Long): PostDetailResponse {
        return repository.getPostDetail(postId)
    }
}