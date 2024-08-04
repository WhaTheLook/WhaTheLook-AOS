package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.PostListResponse
import com.stopstone.whathelook.domain.repository.PostListRepository

class GetPostListUseCase(private val repository: PostListRepository) {
    suspend operator fun invoke(): PostListResponse {
        return repository.getPostList()
    }
}