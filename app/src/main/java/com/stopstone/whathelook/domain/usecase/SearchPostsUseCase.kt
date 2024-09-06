package com.stopstone.whathelook.domain.usecase

import android.util.Log
import com.stopstone.whathelook.data.model.response.SearchResponse
import com.stopstone.whathelook.domain.repository.SearchRepository
import javax.inject.Inject

class SearchPostsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(search: String, lastPostId: Long? = null, size: Int = 10, sortBy: String = "recent", category: String? = null): SearchResponse {
        Log.d("SearchPostsUseCase", "유스케이스 실행: 검색어='$search'")
        val result = searchRepository.searchPosts(search, lastPostId, size, sortBy, category)
        Log.d("SearchPostsUseCase", "검색 완료: ${result.posts.content.size}개의 결과")
        return result
    }
}