package com.stopstone.whathelook.domain.usecase.search

import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository

class AddSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(query: String) = repository.addSearch(query)
}