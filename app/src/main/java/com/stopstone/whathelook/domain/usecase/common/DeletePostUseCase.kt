package com.stopstone.whathelook.domain.usecase.common

import com.stopstone.whathelook.domain.repository.post.PostListRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repositoryImpl: PostListRepository
) {
    suspend operator fun invoke(postId: Long) {
        repositoryImpl.deletePost(postId)
    }
}