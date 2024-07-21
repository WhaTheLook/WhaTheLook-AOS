package com.stopstone.whathelook.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stopstone.whathelook.data.model.RecentSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 5")
    fun getRecentSearches(): Flow<List<RecentSearch>>

    @Query("SELECT * FROM recent_searches WHERE query = :query LIMIT 1")
    suspend fun findSearchByQuery(query: String): RecentSearch?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearch)

    @Update
    suspend fun updateSearch(search: RecentSearch)

    @Delete
    suspend fun deleteSearch(search: RecentSearch)

    @Query("DELETE FROM recent_searches")
    suspend fun deleteAllSearches()
}