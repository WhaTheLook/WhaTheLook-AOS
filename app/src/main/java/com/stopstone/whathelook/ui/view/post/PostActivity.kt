package com.stopstone.whathelook.ui.view.post

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.user.UserApiClient
import com.stopstone.whathelook.R
import com.stopstone.whathelook.databinding.ActivityPostBinding
import com.stopstone.whathelook.ui.adapter.PostImageAdapter
import com.stopstone.whathelook.ui.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    private val viewModel: PostViewModel by viewModels()
    private val adapter: PostImageAdapter by lazy { PostImageAdapter { viewModel.removeImage(it) } }
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupImagePickerLauncher()
        setupViews()
        observeViewModel()
    }

    private fun setupImagePickerLauncher() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let { intent ->
                        val currentCount = viewModel.selectedImages.value.size
                        val remainingSlots = MAX_IMAGES - currentCount

                        if (intent.clipData != null) {
                            val count = minOf(intent.clipData!!.itemCount, remainingSlots)
                            for (i in 0 until count) {
                                val imageUri = intent.clipData!!.getItemAt(i).uri
                                viewModel.addImage(imageUri)
                            }
                        } else {
                            intent.data?.let { uri ->
                                viewModel.addImage(uri)
                            }
                        }

                        if (viewModel.selectedImages.value.size >= MAX_IMAGES) {
                            Toast.makeText(
                                this,
                                "최대 $MAX_IMAGES 장의 이미지를 선택하셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun setupViews() {
        setupImageSelection()
        setupRecyclerView()
        setupTextInputs()
        setupChipGroup()
        setupSubmitButton()

        binding.toolbarPost.setNavigationOnClickListener {
            finish()
        }

        // kakaoId 설정 (예: 로그인 시 저장된 ID)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("PostActivity", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                viewModel.setKakaoId(user.id.toString())
            }
        }
    }

    private fun setupTextInputs() {
        binding.etPostItemTitle.doAfterTextChanged {
            viewModel.setPostTitle(it?.toString() ?: "")
        }

        binding.etPostItemContent.addTextChangedListener {
            viewModel.setPostContent(it?.toString() ?: "")
            highlightHashtags(it)
        }
    }

    private fun setupChipGroup() {
        binding.categoryChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedChipId = checkedIds.firstOrNull()
            val category = when (selectedChipId) {
                R.id.chip_answer -> "정보공유"
                R.id.chip_question -> "질문하기"
                else -> ""
            }
            viewModel.setCategory(category)
            viewModel.setChipSelected(checkedIds.isNotEmpty())
        }
    }

    private fun setupSubmitButton() {
        binding.btnRegisterCompleteButton.setOnClickListener {
            viewModel.extractHashtags()
            viewModel.createPost()
        }
    }

    private fun setupRecyclerView() {
        binding.rvPostPhotoList.adapter = adapter
    }

    private fun setupImageSelection() {
        binding.ivPostItemAddImage.setOnClickListener {
            if (viewModel.selectedImages.value.size < MAX_IMAGES) {
                openImagePicker()
            } else {
                Toast.makeText(this, "이미 $MAX_IMAGES 장의 이미지를 선택하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedImages.collect { images ->
                adapter.submitList(images)
            }
        }

        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect { isEnabled ->
                binding.btnRegisterCompleteButton.isEnabled = isEnabled
            }
        }

        lifecycleScope.launch {
            viewModel.hashtagList.collect { hashtags ->
                Log.d("PostActivity", "해시태그: $hashtags")
            }
        }

        lifecycleScope.launch {
            viewModel.postCreationResult.collect { result ->
                result.onSuccess { message ->
                    Toast.makeText(this@PostActivity, "게시물이 성공적으로 생성되었습니다: $message", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { error ->
                    val errorMessage = when (error) {
                        is HttpException -> "HTTP 오류: ${error.code()}"
                        is IOException -> "네트워크 오류: ${error.message}"
                        else -> "오류: ${error.message}"
                    }
                    Toast.makeText(this@PostActivity, "게시물 생성에 실패했습니다: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imagePickerLauncher.launch(Intent.createChooser(intent, "이미지 선택"))
    }

    private fun highlightHashtags(s: Editable?) {
        s?.let {
            val spans = it.getSpans(0, it.length, ForegroundColorSpan::class.java)
            for (span in spans) {
                it.removeSpan(span)
            }

            val words = it.split("\\s+".toRegex())
            var lastIndex = 0
            for (word in words) {
                if (word.startsWith("#")) {
                    val startIndex = it.indexOf(word, lastIndex)
                    val endIndex = startIndex + word.length
                    it.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                lastIndex += word.length + 1
            }
        }
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}