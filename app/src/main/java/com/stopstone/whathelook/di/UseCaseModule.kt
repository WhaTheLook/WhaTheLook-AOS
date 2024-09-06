package com.stopstone.whathelook.di

import com.stopstone.whathelook.domain.repository.SearchRepository
import com.stopstone.whathelook.domain.repository.post.PostListRepository
import com.stopstone.whathelook.domain.usecase.SearchPostsUseCase
import com.stopstone.whathelook.domain.usecase.post.GetPostListUseCase
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

    @Provides
    @Singleton
    fun provideSearchPostsUseCase(searchRepository: SearchRepository): SearchPostsUseCase {
        return SearchPostsUseCase(searchRepository)
    }
}