package com.stopstone.whathelook.di

import com.stopstone.whathelook.domain.repository.PostListRepository
import com.stopstone.whathelook.domain.usecase.GetPostListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetPostListUseCase(repository: PostListRepository): GetPostListUseCase =
        GetPostListUseCase(repository)
}