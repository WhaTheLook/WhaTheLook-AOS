package com.stopstone.whathelook.ui.view.home.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.Post
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

        val post = intent?.getParcelableExtra<Post>("post")
        if (post == null) {
            Toast.makeText(this, "포스트 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupUI(post)

        binding.toolbarPostDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI(post: Post) {
        val user = post.writer
        Glide.with(this)
            .load(user.profileImageUrl)
            .into(binding.ivUserProfile)

        binding.tvUserName.text = user.name
        binding.tvPostTimestamp.text = post.createdAt
        binding.tvPostContent.text = post.content
    }
}