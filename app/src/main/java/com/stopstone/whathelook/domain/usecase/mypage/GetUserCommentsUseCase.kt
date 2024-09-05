package com.stopstone.whathelook.domain.usecase.mypage

import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.domain.repository.UserRepository
import javax.inject.Inject

class GetUserCommentsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(kakaoId: String, lastPostId: Long?): PostListResponse {
        return repository.getUserComments(kakaoId, lastPostId)
    }
}