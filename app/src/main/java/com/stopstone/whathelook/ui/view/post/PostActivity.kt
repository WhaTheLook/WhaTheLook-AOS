package com.stopstone.whathelook.ui.view.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stopstone.whathelook.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}