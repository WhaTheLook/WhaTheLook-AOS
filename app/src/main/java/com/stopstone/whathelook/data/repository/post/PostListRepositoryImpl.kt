package com.stopstone.whathelook.data.repository.post

import android.util.Log
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.request.UpdateLikeRequest
import com.stopstone.whathelook.data.model.response.UpdateLikeResponse
import com.stopstone.whathelook.domain.repository.post.PostListRepository

class PostListRepositoryImpl(
    private val postApiService: PostApiService
): PostListRepository {
    override suspend fun getPostList(category: String): PostListResponse {
        val postList = postApiService.getPostList(category =  category)
        Log.d("PostListRepositoryImpl", "getPostList: $postList")
        return postList
    }

    override suspend fun updateLikeState(postItem: PostListItem, userId: Long): UpdateLikeResponse {
        val likeResponse = postApiService.updateLike(
            UpdateLikeRequest(postItem.id, userId)
        )
        Log.d("PostListRepositoryImpl", "updateLikeState: $likeResponse")
        return likeResponse
    }


}