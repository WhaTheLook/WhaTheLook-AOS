package com.stopstone.whathelook.view.detail

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.stopstone.whathelook.R
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ActivityPostDetailBinding
import com.stopstone.whathelook.utils.HashtagUtils
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText
import com.stopstone.whathelook.view.detail.adapter.CommentAdapter
import com.stopstone.whathelook.view.detail.adapter.OnCommentClickListener
import com.stopstone.whathelook.view.detail.viewmodel.DetailViewModel
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity(), OnCommentClickListener {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(
            layoutInflater
        )
    }
    private val adapter: PostListItemImageAdapter by lazy { PostListItemImageAdapter() }
    private val commentAdapter: CommentAdapter by lazy { CommentAdapter(this) }
    private val viewModel: DetailViewModel by viewModels()
    private var currentUserId: Long? = null
    private var currentEditingCommentId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchCurrentUserId()
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

            launch {
                viewModel.postDetail.collect {
                    it?.let {
                        setupUI(it)
                    }
                }
            }
        }

        binding.btnPostLike.setOnClickListener {
            viewModel.updateLikeState(postListItem)
        }

        binding.btnPostCommentSend.setOnClickListener {
            val comment = binding.etPostCommentEdit.text.toString()
            if (comment.isNotEmpty()) {
                val postId =
                    intent.getParcelableExtra<PostListItem>("post")?.id ?: return@setOnClickListener
                viewModel.createOrUpdateComment(postId, currentEditingCommentId, comment)
                hideKeyboard()
                binding.etPostCommentEdit.text.clear()
                currentEditingCommentId = null
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

                HashtagUtils.setClickableHashtags(
                    this@PostDetailActivity,
                    tvPostContent,
                    postListItem.content,
                    postListItem.hashtags
                )

                // 해시태그 목록 표시
                val hashtagContent = postListItem.hashtags.joinToString(" ")
                tvPostHashtags.text = hashtagContent
                HashtagUtils.setClickableHashtags(
                    this@PostDetailActivity,
                    tvPostHashtags,
                    hashtagContent,
                    postListItem.hashtags
                )
            }
        }
    }

    override fun onMenuClick(comment: Comment, view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.item_comment_menu)

        if (currentUserId != comment.author.kakaoId.toLong()) {
            popup.menu.removeItem(R.id.action_comment_delete)
            popup.menu.removeItem(R.id.action_comment_update)
        } else {
            val deleteItem = popup.menu.findItem(R.id.action_comment_delete)
            deleteItem?.let {
                val spanString = SpannableString(deleteItem.title.toString())
                spanString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this,
                            R.color.red_700
                        )
                    ), 0, spanString.length, 0
                )
                deleteItem.title = spanString
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_comment_delete -> {
                    viewModel.deleteComment(comment.id)
                    true
                }

                R.id.action_comment_update -> {
                    currentEditingCommentId = comment.id
                    binding.etPostCommentEdit.requestFocus()
                    showKeyboard()
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    private fun fetchCurrentUserId() = lifecycleScope.launch {
        try {
            currentUserId = KakaoUserUtil.getUserId()
        } catch (e: Exception) {
            Log.e("QuestionFragment", "Failed to fetch current user ID", e)
        }
    }

    private fun showKeyboard() {
        val view = binding.etPostCommentEdit
        view.requestFocus()
        view.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun hideKeyboard() {
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(binding.etPostCommentEdit.windowToken, 0)
    }
}