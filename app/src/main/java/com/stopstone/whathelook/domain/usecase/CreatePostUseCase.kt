package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.CreatePostModel
import com.stopstone.whathelook.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(createPostModel: CreatePostModel): Result<String> {
        return postRepository.createPost(createPostModel)
    }
}