package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.domain.repository.RecentSearchRepository

class ClearRecentSearchesUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke() = repository.deleteAllSearches()
}