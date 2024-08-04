package com.stopstone.whathelook.di

import android.content.ContentResolver
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.db.RecentSearchDao
import com.stopstone.whathelook.data.repository.LoginRepositoryImpl
import com.stopstone.whathelook.data.repository.PostListRepositoryImpl
import com.stopstone.whathelook.domain.repository.PostRepository
import com.stopstone.whathelook.data.repository.PostRepositoryImpl
import com.stopstone.whathelook.data.repository.RecentSearchRepositoryImpl
import com.stopstone.whathelook.data.repository.UserRepositoryImpl
import com.stopstone.whathelook.domain.repository.LoginRepository
import com.stopstone.whathelook.domain.repository.PostListRepository
import com.stopstone.whathelook.domain.repository.RecentSearchRepository
import com.stopstone.whathelook.domain.repository.UserRepository
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

    @Provides
    @Singleton
    fun provideUserInfoRepository(api: UserService): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideLoginRepository(loginService: LoginService): LoginRepository =
        LoginRepositoryImpl(loginService)

    @Provides
    @Singleton
    fun providePostRepository(postApiService: PostApiService, contentResolver: ContentResolver): PostRepository =
        PostRepositoryImpl(postApiService, contentResolver)

    @Provides
    @Singleton
    fun providePostListRepository(postApiService: PostApiService): PostListRepository =
        PostListRepositoryImpl(postApiService)
}