package com.stopstone.whathelook.di

import com.stopstone.whathelook.data.db.RecentSearchDao
import com.stopstone.whathelook.data.repository.RecentSearchRepositoryImpl
import com.stopstone.whathelook.domain.repository.RecentSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideRecentSearchRepository(dao: RecentSearchDao): RecentSearchRepository {
        return RecentSearchRepositoryImpl(dao)
    }
}