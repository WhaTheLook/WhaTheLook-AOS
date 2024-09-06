package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.response.SearchResponse

interface SearchRepository {
    suspend fun searchPosts(
        search: String,
        lastPostId: Long? = null,
        size: Int = 10,
        sortBy: String = "recent",
        category: String? = null
    ): SearchResponse
}