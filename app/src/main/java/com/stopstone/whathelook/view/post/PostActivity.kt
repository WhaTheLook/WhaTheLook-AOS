package com.stopstone.whathelook.view.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.stopstone.whathelook.R
import com.stopstone.whathelook.databinding.ActivityPostBinding
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.view.custom.gallery.CustomGalleryActivity
import com.stopstone.whathelook.view.post.adapter.PostImageAdapter
import com.stopstone.whathelook.view.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostBinding
    private val viewModel: PostViewModel by viewModels()
    private val adapter: PostImageAdapter by lazy { PostImageAdapter(this::onDeletePhoto) }
    private var isEditMode = false
    private var editPostId: Long = -1

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImages = result.data?.getParcelableArrayListExtra<Uri>(CustomGalleryActivity.SELECTED_IMAGES)
            selectedImages?.forEach { uri ->
                viewModel.addImage(uri)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT_MODE, false)
        editPostId = intent.getLongExtra(EXTRA_POST_ID, -1)

        setupUI()
        setupObservers()

        if (isEditMode) {
            viewModel.getPostDetail(editPostId)
        }
    }

    private fun setupUI() {
        binding.toolbarPost.title = if (isEditMode) "게시글 수정" else "게시글 작성"
        binding.btnRegisterCompleteButton.text = if (isEditMode) "수정해요" else "작성해요"

        binding.toolbarPost.setNavigationOnClickListener { finish() }
        binding.ivPostItemAddImage.setOnClickListener { openGallery() }
        binding.rvPostPhotoList.adapter = adapter
        binding.btnRegisterCompleteButton.setOnClickListener {
            if (isEditMode) {
                viewModel.updatePost(editPostId)
            } else {
                viewModel.createPost()
            }
        }

        binding.etPostItemTitle.addTextChangedListener { viewModel.setPostTitle(it.toString()) }
        binding.etPostItemContent.addTextChangedListener { viewModel.setPostContent(it.toString()) }

        binding.categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val category = when (checkedId) {
                R.id.chip_question -> "질문하기"
                R.id.chip_answer -> "정보공유"
                else -> ""
            }
            viewModel.setCategory(category)
        }

        lifecycleScope.launch {
            val kakaoId = KakaoUserUtil.getUserId().toString()
            viewModel.setKakaoId(kakaoId)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            launch {
                viewModel.postDetail.collect { post ->
                    post?.let { updateUIWithPostData(it) }
                }
            }

            launch {
                viewModel.postCreationResult.collect { result ->
                    result.onSuccess {
                        Toast.makeText(this@PostActivity, "게시글이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }.onFailure { e ->
                        Toast.makeText(this@PostActivity, "게시글 생성 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            launch {
                viewModel.postUpdateResult.collect { result ->
                    runCatching { result }
                        .onSuccess {
                            Toast.makeText(this@PostActivity, "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        }.onFailure {
                            Toast.makeText(this@PostActivity, "게시글 수정 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            launch {
                viewModel.selectedImages.collect {
                    adapter.submitList(it)
                }
            }

            launch {
                viewModel.isSubmitEnabled.collect {
                    binding.btnRegisterCompleteButton.isEnabled = it
                }
            }
        }
    }

    private fun updateUIWithPostData(post: PostListItem) {
        binding.etPostItemTitle.setText(post.title)
        binding.etPostItemContent.setText(post.content)
        when (post.category) {
            "질문하기" -> binding.chipQuestion.isChecked = true
            "정보공유" -> binding.chipAnswer.isChecked = true
        }
        viewModel.setSelectedImages(post.photoUrls.map { Uri.parse(it) })
        adapter.submitList(post.photoUrls.map { Uri.parse(it) })

        // 카테고리 설정
        viewModel.setCategory(post.category)
        viewModel.setChipSelected(true)
    }

    private fun openGallery() {
        val intent = Intent(this, CustomGalleryActivity::class.java)
        galleryLauncher.launch(intent)
    }

    private fun onDeletePhoto(uri: Uri) {
        viewModel.removeImage(uri)
    }

    companion object {
        const val EXTRA_IS_EDIT_MODE = "extra_is_edit_mode"
        const val EXTRA_POST_ID = "extra_post_id"
    }
}