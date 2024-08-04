package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.PostListResponse

interface PostListRepository {
    suspend fun getPostList(): PostListResponse

}