package com.stopstone.whathelook.di

import com.stopstone.whathelook.BuildConfig
import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.repository.LoginRepositoryImpl
import com.stopstone.whathelook.data.repository.MockRepository
import com.stopstone.whathelook.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)


    @Provides
    @Singleton
    fun provideLoginRepository(loginService: LoginService): LoginRepository =
        LoginRepositoryImpl(loginService)

    @Provides
    @Singleton
    fun provideMockRepository(): MockRepository = MockRepository()
}