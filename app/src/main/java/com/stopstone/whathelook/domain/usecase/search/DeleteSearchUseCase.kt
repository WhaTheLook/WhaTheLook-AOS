package com.stopstone.whathelook.domain.usecase.search

import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository

class DeleteSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(search: RecentSearch) = repository.deleteSearch(search)
}