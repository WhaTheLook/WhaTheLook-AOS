package com.stopstone.whathelook.domain.usecase.detail

import android.util.Log
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import javax.inject.Inject

class AcceptCommentUseCase @Inject constructor(
    private val detailRepository: DetailRepository
) {
    suspend operator fun invoke(postId: Long, commentId: Long): String {
        return detailRepository.acceptComment(postId, commentId).also {
            Log.d("AcceptCommentUseCase", "댓글 채택 완료: 게시글 ID=$postId, 댓글 ID=$commentId")
        }
    }
}