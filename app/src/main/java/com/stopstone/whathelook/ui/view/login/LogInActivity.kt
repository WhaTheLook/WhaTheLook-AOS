package com.stopstone.whathelook.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.databinding.ActivityLogInBinding
import com.stopstone.whathelook.ui.view.MainActivity
import com.stopstone.whathelook.ui.viewmodel.LoginState
import com.stopstone.whathelook.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {
    private val binding: ActivityLogInBinding by lazy { ActivityLogInBinding.inflate(layoutInflater) }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.validateToken()

        binding.btnSignInKakao.setOnClickListener {
            performKakaoLogin()
        }

        observeLoginState()
    }

    private fun performKakaoLogin() {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLogin", "카카오 로그인 실패", error)
            } else if (token != null) {
                viewModel.login(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // Show loading indicator
                    }
                    is LoginState.Success -> {
                        navigateToMainActivity()
                    }
                    is LoginState.Error -> {
                        Toast.makeText(this@LogInActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
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
}
