package com.stopstone.whathelook.data.repository.search

import android.util.Log
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.response.SearchResponse
import com.stopstone.whathelook.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService
) : SearchRepository {
    override suspend fun searchPosts(search: String, lastPostId: Long?, size: Int, sortBy: String, category: String?): SearchResponse {
        Log.d("SearchRepository", "검색 시작: 검색어='$search', 마지막 게시물 ID=$lastPostId, 크기=$size, 정렬=$sortBy, 카테고리=$category")
        val response = postApiService.searchPosts(search, lastPostId, size, sortBy, category)
        Log.d("SearchRepository", "검색 결과: ${response.posts.content.size}개의 게시물 찾음")
        return response
    }
}