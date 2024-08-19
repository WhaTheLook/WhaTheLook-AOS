package com.stopstone.whathelook.domain.usecase.search

import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class GetRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.getRecentSearches()
}