package com.stopstone.whathelook.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ActivityPostDetailBinding
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter

class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(
            layoutInflater
        )
    }
    private val adapter: PostListItemImageAdapter by lazy { PostListItemImageAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvPostImageList.adapter = adapter
        val postListItem = intent?.getParcelableExtra<PostListItem>("post")
        if (postListItem == null) {
            Toast.makeText(this, "포스트 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupUI(postListItem)

        binding.toolbarPostDetail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI(postListItem: PostListItem) {
        Glide.with(binding.root)
            .load(postListItem.author.profileImage)
            .circleCrop()
            .into(binding.ivUserProfile)
        binding.tvUserName.text = postListItem.author.name
        binding.tvPostTimestamp.text = postListItem.date
        binding.tvPostContent.text = postListItem.content
        adapter.submitList(postListItem.photoUrls)
        binding.tvPostLikeCount.text = postListItem.likeCount.toString()
        binding.tvPostCommentCount.text = postListItem.commentCount.toString()
    }
}