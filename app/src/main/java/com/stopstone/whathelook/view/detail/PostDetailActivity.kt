package com.stopstone.whathelook.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ActivityPostDetailBinding
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText
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
        with(binding) {
            with(postListItem) {
                ivUserProfile.loadCircleImage(author.profileImage)
                tvUserName.text = author.name
                tvPostTimestamp.setRelativeTimeText(date)
                tvPostContent.text = content
                adapter.submitList(photoUrls)
                tvPostLikeCount.text = "$likeCount"
                btnPostLike.isSelected = likeYN
                tvPostCommentCount.text = "$commentCount"
            }
        }
    }
}