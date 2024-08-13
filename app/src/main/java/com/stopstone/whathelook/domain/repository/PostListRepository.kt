package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.data.model.PostListResponse
import com.stopstone.whathelook.data.model.UpdateLikeResponse

interface PostListRepository {
    suspend fun getPostList(category: String): PostListResponse
    suspend fun updateLikeState(postItem: PostListItem, userId: Long): UpdateLikeResponse
}