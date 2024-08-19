package com.stopstone.whathelook.view.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    init {
        viewModelScope.launch {
            recentSearchRepository.getRecentSearches().collect {
                _recentSearches.value = it
            }
        }
    }

    fun addSearch(query: String) {
        viewModelScope.launch {
            recentSearchRepository.addSearch(query)
        }
    }

    fun deleteSearch(search: RecentSearch) {
        viewModelScope.launch {
            recentSearchRepository.deleteSearch(search)
        }
    }

    fun clearAllSearches() {
        viewModelScope.launch {
            recentSearchRepository.deleteAllSearches()
        }
    }
}