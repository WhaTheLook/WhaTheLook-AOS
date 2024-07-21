package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.RecentSearch
import com.stopstone.whathelook.domain.repository.RecentSearchRepository

class DeleteSearchUseCase(private val repository: RecentSearchRepository) {
    suspend operator fun invoke(search: RecentSearch) = repository.deleteSearch(search)
}