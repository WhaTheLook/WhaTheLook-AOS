package com.stopstone.whathelook.ui.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stopstone.whathelook.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private val binding: ActivitySearchBinding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}