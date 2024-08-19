package com.stopstone.whathelook.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.stopstone.whathelook.databinding.ActivitySplashBinding
import com.stopstone.whathelook.view.MainActivity
import com.stopstone.whathelook.view.login.LogInActivity
import com.stopstone.whathelook.view.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        splashScreen.setKeepOnScreenCondition { true }

        viewModel.validateToken()
        observeTokenState()
    }

    private fun observeTokenState() {
        lifecycleScope.launch {
            viewModel.tokenState.collect { hasToken ->
                Log.d("SplashActivity", "Token State: $hasToken")
                when (hasToken) {
                    true -> navigateTo(MainActivity::class.java)
                    false -> navigateTo(LogInActivity::class.java)
                    null -> { /* 초기 상태 또는 로딩 중, 필요하다면 여기에 로딩 표시 로직 추가 */ }
                }
            }
        }
    }

    private fun navigateTo(destinationActivity: Class<*>) {
        startActivity(Intent(this, destinationActivity))
        finish()
    }
}