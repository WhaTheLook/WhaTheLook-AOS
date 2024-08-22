package com.stopstone.whathelook

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.stopstone.whathelook.data.local.TokenManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    @Inject lateinit var tokenManager: TokenManager
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.API_KEY)
        tokenManager.clearToken()
    }
}