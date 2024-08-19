package com.stopstone.whathelook.view.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.databinding.ActivityLogInBinding
import com.stopstone.whathelook.view.MainActivity
import com.stopstone.whathelook.view.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {
    private val binding: ActivityLogInBinding by lazy { ActivityLogInBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeLoginState()
        setListeners()

    }

    /**
     * 카카오 로그인 수행
     */
    private suspend fun performKakaoLogin() {
        try {
            val token = if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                loginWithKakaoTalk()
            } else {
                loginWithKakaoAccount()
            }
            viewModel.login(token.accessToken)
        } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                return
            }
            try {
                val token = loginWithKakaoAccount()
                viewModel.login(token.accessToken)
            } catch (e: Throwable) {
                Toast.makeText(this, "카카오 로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when(state) {
                    true -> navigateToMainActivity()
                    false -> Toast.makeText(this@LogInActivity, "로그인에 실패 하였습니다.", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LogInActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 카카오톡으로 로그인
     */
    private suspend fun loginWithKakaoTalk(): OAuthToken = suspendCoroutine { continuation ->
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            when {
                error != null -> continuation.resumeWithException(error)
                token != null -> continuation.resume(token)
                else -> continuation.resumeWithException(RuntimeException("KakaoTalk login failed"))
            }
        }
    }

    /**
     * 카카오계정으로 로그인
     */
    private suspend fun loginWithKakaoAccount(): OAuthToken = suspendCoroutine { continuation ->
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            when {
                error != null -> continuation.resumeWithException(error)
                token != null -> continuation.resume(token)
                else -> continuation.resumeWithException(RuntimeException("KakaoAccount login failed"))
            }
        }
    }

    private fun setListeners() {
        binding.btnSignInKakao.setOnClickListener {
            lifecycleScope.launch {
                performKakaoLogin()
            }
        }
    }
}