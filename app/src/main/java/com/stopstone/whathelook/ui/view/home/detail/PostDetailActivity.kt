package com.stopstone.whathelook.ui.view.home.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stopstone.whathelook.data.model.CreatePostModel
import com.stopstone.whathelook.databinding.ActivityPostDetailBinding

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val createPostModel = intent?.getParcelableExtra<CreatePostModel>("post")
        if (createPostModel == null) {
            Toast.makeText(this, "포스트 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupUI(createPostModel)

        binding.toolbarPostDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI(createPostModel: CreatePostModel) {
        binding.tvPostContent.text = createPostModel.content
    }
}