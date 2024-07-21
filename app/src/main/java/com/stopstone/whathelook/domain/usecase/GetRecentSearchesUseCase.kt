package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.RecentSearch
import com.stopstone.whathelook.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class GetRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    operator fun invoke(): Flow<List<RecentSearch>> = repository.getRecentSearches()
}