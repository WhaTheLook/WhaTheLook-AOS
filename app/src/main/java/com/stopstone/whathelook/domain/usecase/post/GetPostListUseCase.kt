package com.stopstone.whathelook.domain.usecase.post

import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.domain.repository.post.PostListRepository

class GetPostListUseCase(private val repository: PostListRepository) {
    suspend operator fun invoke(category: String): PostListResponse {
        return repository.getPostList(category)
    }
}