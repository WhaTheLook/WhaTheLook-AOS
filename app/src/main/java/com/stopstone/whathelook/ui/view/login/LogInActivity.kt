package com.stopstone.whathelook.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.stopstone.whathelook.R
import com.stopstone.whathelook.databinding.ActivityLogInBinding
import com.stopstone.whathelook.ui.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class LogInActivity : AppCompatActivity() {
    private val binding: ActivityLogInBinding by lazy { ActivityLogInBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSignInKakao.setOnClickListener {
//            loginWithKakao()


        }
    }

}
