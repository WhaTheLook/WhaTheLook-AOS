package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.domain.repository.RecentSearchRepository

class AddSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(query: String) = repository.addSearch(query)
}