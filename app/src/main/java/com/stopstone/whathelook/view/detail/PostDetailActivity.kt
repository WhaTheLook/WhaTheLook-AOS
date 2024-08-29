package com.stopstone.whathelook.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ActivityPostDetailBinding
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText
import com.stopstone.whathelook.view.detail.adapter.CommentAdapter
import com.stopstone.whathelook.view.detail.viewmodel.DetailViewModel
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(
            layoutInflater
        )
    }
    private val adapter: PostListItemImageAdapter by lazy { PostListItemImageAdapter() }
    private val commentAdapter: CommentAdapter by lazy { CommentAdapter() }
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.rvPostImageList.adapter = adapter
        binding.rvPostCommentList.adapter = commentAdapter

        val postListItem = intent?.getParcelableExtra<PostListItem>("post")
        if (postListItem == null) {
            Toast.makeText(this, "포스트 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            viewModel.getPostDetail(postListItem.id)
            setupUI(postListItem)
        }

        binding.toolbarPostDetail.setNavigationOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            launch {
                viewModel.comments.collect {
                    commentAdapter.submitList(it)
                }
            }

            launch {
                viewModel.message.collect {
                    Toast.makeText(this@PostDetailActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnPostCommentSend.setOnClickListener {
            val comment = binding.etPostCommentEdit.text.toString()
            if (comment.isNotEmpty()) {
                viewModel.createComment(postListItem.id, comment)
                binding.etPostCommentEdit.text.clear()
            }
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
                tvPostDetailCommentCount.text = "$commentCount"
            }
        }
    }
}