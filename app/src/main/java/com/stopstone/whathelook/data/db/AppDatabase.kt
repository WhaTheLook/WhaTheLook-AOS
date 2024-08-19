package com.stopstone.whathelook.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stopstone.whathelook.data.model.entity.RecentSearch

@Database(entities = [RecentSearch::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}