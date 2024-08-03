package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(post: Post): Result<String> {
        return postRepository.createPost(post)
    }
}