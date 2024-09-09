package com.stopstone.whathelook.domain.usecase.common

import com.stopstone.whathelook.domain.event.DetailEvent
import com.stopstone.whathelook.domain.repository.post.PostListRepository
import com.stopstone.whathelook.domain.repository.post.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(postId: Long): Flow<DetailEvent> = flow {
        try {
            postRepository.deletePost(postId)
            emit(DetailEvent.FinishActivity)
        } catch (e: Exception) {
            // 에러 처리
        }
    }
}