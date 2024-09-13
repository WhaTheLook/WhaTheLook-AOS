package com.stopstone.whathelook.view.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.stopstone.whathelook.domain.event.DetailEvent
import com.stopstone.whathelook.utils.HashtagUtils
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText
import com.stopstone.whathelook.view.detail.adapter.CommentAdapter
import com.stopstone.whathelook.view.detail.adapter.OnCommentClickListener
import com.stopstone.whathelook.view.detail.viewmodel.DetailViewModel
import com.stopstone.whathelook.view.post.PostActivity
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity(), OnCommentClickListener {
    private val binding: ActivityPostDetailBinding by lazy {
        ActivityPostDetailBinding.inflate(layoutInflater)
    }
    private val adapter: PostListItemImageAdapter by lazy { PostListItemImageAdapter() }
    private val commentAdapter: CommentAdapter by lazy { CommentAdapter(this) }
    private val viewModel: DetailViewModel by viewModels()
    private var currentUserId: Long? = null
    private var currentEditingCommentId: Long? = null
    private var currentReplyToComment: Comment? = null
    private var replyPrefix: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarPostDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

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

            launch {
                viewModel.event.collect { event ->
                    when (event) {
                        is DetailEvent.FinishActivity -> finish()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_post_menu, menu)

        val deleteItem = menu.findItem(R.id.action_delete)
        val updateItem = menu.findItem(R.id.action_update)
        val postListItem = viewModel.postDetail.value

        if (currentUserId == null || postListItem == null) {
            deleteItem.isVisible = false
            updateItem.isVisible = false
            return true
        }

        val isAuthor = (currentUserId == postListItem.author.kakaoId.toLong())
        deleteItem.isVisible = isAuthor
        updateItem.isVisible = isAuthor

        if (isAuthor) {
            val redColor = ContextCompat.getColor(this, R.color.red_700)
            deleteItem.title = SpannableString(deleteItem.title).apply {
                setSpan(ForegroundColorSpan(redColor), 0, length, 0)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_delete -> {
                val postListItem = viewModel.postDetail.value
                postListItem?.let {
                    if (currentUserId == it.author.kakaoId.toLong()) {
                        viewModel.deletePost(it.id)
                    } else {
                        Toast.makeText(this, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            R.id.action_update -> {
                val postListItem = viewModel.postDetail.value
                postListItem?.let {
                    if (currentUserId == it.author.kakaoId.toLong()) {
                        val intent = Intent(this, PostActivity::class.java).apply {
                            putExtra(PostActivity.EXTRA_IS_EDIT_MODE, true)
                            putExtra(PostActivity.EXTRA_POST_ID, it.id)
                        }
                        startActivityForResult(intent, REQUEST_CODE_EDIT_POST)
                    } else {
                        Toast.makeText(this, "수정 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMenuClick(comment: Comment, view: View) {
        val popup = PopupMenu(this, view)

        if (currentUserId == comment.author.kakaoId.toLong()) {
            popup.inflate(R.menu.item_comment_menu)
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
                    binding.etPostCommentEdit.setText(comment.text)
                    binding.etPostCommentEdit.requestFocus()
                    showKeyboard()
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    override fun onReplyClick(comment: Comment) {
        showReplyUI(comment)
    }

    override fun onShowRepliesClick(comment: Comment) {
        TODO("Not yet implemented")
    }

    private fun showReplyUI(comment: Comment) {
        currentReplyToComment = comment
        replyPrefix = "@${comment.author.name} "

        binding.etPostCommentEdit.setText(replyPrefix)
        binding.etPostCommentEdit.requestFocus()

        // 커서를 @닉네임 뒤로 이동
        binding.etPostCommentEdit.setSelection(replyPrefix.length)
        Log.d("PostDetailActivity", "커서 이동: ${replyPrefix.length}")
        Log.d("PostDetailActivity", "커서 이동: ${replyPrefix}")


        // 닉네임을 회색으로 표시
        updateReplyPrefixSpan()

        showKeyboard()

        // TextWatcher 설정
        setupReplyTextWatcher()
    }

    private fun updateReplyPrefixSpan() {
        val spannable = SpannableString(binding.etPostCommentEdit.text)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.gray_500)),
            0,
            replyPrefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.etPostCommentEdit.setText(spannable)
        binding.etPostCommentEdit.setSelection(spannable.length)
    }

    private fun setupReplyTextWatcher() {
        binding.etPostCommentEdit.addTextChangedListener(object : TextWatcher {
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isDeleting && s?.toString()?.startsWith(replyPrefix) == false) {
                    // 닉네임이 부분적으로 삭제되었을 때 전체 삭제
                    binding.etPostCommentEdit.setText("")
                    currentReplyToComment = null
                } else if (!isDeleting && (s?.length ?: 0) >= replyPrefix.length) {
                    // 입력 중일 때 닉네임 부분의 스팬 유지
                    updateReplyPrefixSpan()
                }
            }
        })
    }

    private fun fetchCurrentUserId() {
        lifecycleScope.launch {
            try {
                currentUserId = KakaoUserUtil.getUserId()
                invalidateOptionsMenu()
            } catch (e: Exception) {
                Log.e("PostDetailActivity", "Failed to fetch current user ID", e)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_POST && resultCode == RESULT_OK) {
            viewModel.getPostDetail(viewModel.postDetail.value?.id ?: return)
            Toast.makeText(this, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_EDIT_POST = 1001
    }
}