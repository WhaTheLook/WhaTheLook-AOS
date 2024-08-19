package com.stopstone.whathelook.domain.repository.bookmark

import com.stopstone.whathelook.data.model.response.PostListResponse

interface BookmarkRepository {
    suspend fun getBookmarks(): PostListResponse
}