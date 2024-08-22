package com.stopstone.whathelook.domain.repository.post

import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.UpdateLikeResponse

interface PostListRepository {
    suspend fun getPostList(category: String, lastPostId: Long?, size: Int): PostListResponse
    suspend fun updateLikeState(postItem: PostListItem, userId: Long): UpdateLikeResponse
}