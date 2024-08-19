package com.stopstone.whathelook.domain.usecase.search

import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository

class ClearRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke() = repository.deleteAllSearches()
}