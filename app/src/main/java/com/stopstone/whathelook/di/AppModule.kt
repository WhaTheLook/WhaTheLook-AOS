package com.stopstone.whathelook.di

import android.content.ContentResolver
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stopstone.whathelook.BuildConfig
import com.stopstone.whathelook.data.api.BookmarkService
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.api.UserService
import com.stopstone.whathelook.data.local.TokenManager
import com.stopstone.whathelook.data.auth.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    @Named("BaseOkHttpClient")
    fun provideBaseOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("BaseRetrofit")
    fun provideBaseRetrofit(@Named("BaseOkHttpClient") okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginService(@Named("BaseRetrofit") retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager,
        @ApplicationContext context: Context,
        loginService: LoginService
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager, loginService, context)
    }

    @Provides
    @Singleton
    @Named("AuthenticatedOkHttpClient")
    fun provideAuthenticatedOkHttpClient(
        @Named("BaseOkHttpClient") baseOkHttpClient: OkHttpClient,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return baseOkHttpClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthenticatedRetrofit")
    fun provideAuthenticatedRetrofit(
        @Named("AuthenticatedOkHttpClient") authenticatedOkHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(authenticatedOkHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserService(@Named("AuthenticatedRetrofit") retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideApiService(@Named("AuthenticatedRetrofit") retrofit: Retrofit): PostApiService {
        return retrofit.create(PostApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookmarkService(@Named("AuthenticatedRetrofit") retrofit: Retrofit): BookmarkService {
        return retrofit.create(BookmarkService::class.java)
    }

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }
}