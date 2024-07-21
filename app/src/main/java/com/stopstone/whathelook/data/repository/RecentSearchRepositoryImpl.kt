package com.stopstone.whathelook.data.repository

import com.stopstone.whathelook.data.db.RecentSearchDao
import com.stopstone.whathelook.data.model.RecentSearch
import com.stopstone.whathelook.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(
    private val recentSearchDao: RecentSearchDao
) : RecentSearchRepository {

    override fun getRecentSearches(): Flow<List<RecentSearch>> =
        recentSearchDao.getRecentSearches()

    override suspend fun addSearch(query: String) {
        val existingSearch = recentSearchDao.findSearchByQuery(query)
        if (existingSearch != null) {
            recentSearchDao.updateSearch(existingSearch.copy(timestamp = System.currentTimeMillis()))
        } else {
            recentSearchDao.insertSearch(RecentSearch(query = query, timestamp = System.currentTimeMillis()))
        }
    }

    override suspend fun deleteSearch(search: RecentSearch) {
        recentSearchDao.deleteSearch(search)
    }

    override suspend fun deleteAllSearches() {
        recentSearchDao.deleteAllSearches()
    }
}