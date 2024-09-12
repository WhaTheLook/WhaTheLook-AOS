package com.stopstone.whathelook.domain.usecase.post

import com.stopstone.whathelook.data.model.entity.UpdatePostModel
import com.stopstone.whathelook.domain.repository.post.PostRepository
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(updatePostModel: UpdatePostModel): String {
        return repository.updatePost(updatePostModel)
    }
}