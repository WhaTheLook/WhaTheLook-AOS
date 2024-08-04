package com.stopstone.whathelook.data.repository

import android.util.Log
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.PostListResponse
import com.stopstone.whathelook.domain.repository.PostListRepository

class PostListRepositoryImpl(
    private val postApiService: PostApiService
): PostListRepository {
    override suspend fun getPostList(category: String): PostListResponse {
        val postList = postApiService.getPostList(category =  category)
        Log.d("PostListRepositoryImpl", "getPostList: $postList")
        return postList
    }
}