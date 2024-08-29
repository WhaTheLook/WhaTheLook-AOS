package com.stopstone.whathelook.di

import android.content.ContentResolver
import com.stopstone.whathelook.data.api.BookmarkService
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.db.RecentSearchDao
import com.stopstone.whathelook.data.repository.detail.DetailRepositoryImpl
import com.stopstone.whathelook.data.repository.login.LoginRepositoryImpl
import com.stopstone.whathelook.data.repository.post.PostListRepositoryImpl
import com.stopstone.whathelook.domain.repository.post.PostRepository
import com.stopstone.whathelook.data.repository.post.PostRepositoryImpl
import com.stopstone.whathelook.data.repository.search.RecentSearchRepositoryImpl
import com.stopstone.whathelook.data.repository.UserRepositoryImpl
import com.stopstone.whathelook.data.repository.bookmark.BookmarkRepositoryImpl
import com.stopstone.whathelook.domain.repository.detail.DetailRepository
import com.stopstone.whathelook.domain.repository.bookmark.BookmarkRepository
import com.stopstone.whathelook.domain.repository.login.LoginRepository
import com.stopstone.whathelook.domain.repository.post.PostListRepository
import com.stopstone.whathelook.domain.repository.search.RecentSearchRepository
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

    @Provides
    @Singleton
    fun provideBookmarkRepository(bookmarkService: BookmarkService): BookmarkRepository =
        BookmarkRepositoryImpl(bookmarkService)

    @Provides
    @Singleton
    fun provideDetailRepository(postApiService: PostApiService): DetailRepository =
        DetailRepositoryImpl(postApiService)

}