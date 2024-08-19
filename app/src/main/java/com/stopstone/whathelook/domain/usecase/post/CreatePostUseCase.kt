package com.stopstone.whathelook.domain.usecase.post

import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.domain.repository.post.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(createPostModel: CreatePostModel): Result<String> {
        return postRepository.createPost(createPostModel)
    }
}