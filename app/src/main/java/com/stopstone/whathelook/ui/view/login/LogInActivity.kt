package com.stopstone.whathelook.ui.view.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stopstone.whathelook.databinding.ActivityLogInBinding
import com.stopstone.whathelook.ui.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class LogInActivity : AppCompatActivity() {
    private val binding: ActivityLogInBinding by lazy { ActivityLogInBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSignInKakao.setOnClickListener {
//            loginWithKakao()
            startActivity(
                Intent(this, MainActivity::class.java)
            ).also {
                finish()
            }
        }
    }

}
