package com.stopstone.whathelook.domain.usecase.user

import android.util.Log
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.domain.repository.UserRepository
import javax.inject.Inject

class GetUserPostsUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(kakaoId: String, lastPostId: Long?): PostListResponse {
        val postList =  repository.getUserPosts(kakaoId, lastPostId)
        Log.d("PostListRepositoryImpl", "getPostList: $postList")
        return postList
    }
}