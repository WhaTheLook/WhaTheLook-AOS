package com.stopstone.whathelook.di

import com.stopstone.whathelook.domain.repository.RecentSearchRepository
import com.stopstone.whathelook.domain.repository.UserRepository
import com.stopstone.whathelook.domain.usecase.AddSearchUseCase
import com.stopstone.whathelook.domain.usecase.ClearRecentSearchesUseCase
import com.stopstone.whathelook.domain.usecase.DeleteSearchUseCase
import com.stopstone.whathelook.domain.usecase.GetRecentSearchesUseCase
import com.stopstone.whathelook.domain.usecase.GetUserInfoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetRecentSearchesUseCase(repository: RecentSearchRepository): GetRecentSearchesUseCase {
        return GetRecentSearchesUseCase(repository)
    }

    @Provides
    fun provideAddSearchUseCase(repository: RecentSearchRepository): AddSearchUseCase {
        return AddSearchUseCase(repository)
    }

    @Provides
    fun provideDeleteSearchUseCase(repository: RecentSearchRepository): DeleteSearchUseCase {
        return DeleteSearchUseCase(repository)
    }

    @Provides
    fun provideClearRecentSearchesUseCase(repository: RecentSearchRepository): ClearRecentSearchesUseCase {
        return ClearRecentSearchesUseCase(repository)
    }

    @Provides
    fun provideGetUserInfoUseCase(repository: UserRepository): GetUserInfoUseCase {
        return GetUserInfoUseCase(repository)
    }
}