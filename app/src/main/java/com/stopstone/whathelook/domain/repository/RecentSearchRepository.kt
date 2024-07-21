package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.RecentSearch
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun getRecentSearches(): Flow<List<RecentSearch>>
    suspend fun addSearch(query: String)
    suspend fun deleteSearch(search: RecentSearch)
    suspend fun deleteAllSearches()
}