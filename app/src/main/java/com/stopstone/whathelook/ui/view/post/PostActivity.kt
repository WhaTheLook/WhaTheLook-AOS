package com.stopstone.whathelook.ui.view.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.ChipGroup
import com.stopstone.whathelook.databinding.ActivityPostBinding
import com.stopstone.whathelook.ui.adapter.PostImageAdapter
import com.stopstone.whathelook.ui.viewmodel.PostImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    private val viewModel: PostImageViewModel by viewModels()
    private lateinit var imageAdapter: PostImageAdapter

    private var postTitle = ""
    private var postContent = ""

    private var isValidTitle = false
    private var isValidContent = false
    private var isValidChip = false
    private var isValidImage = false

    private val IMAGE_PICK_CODE = 1000
    private val MAX_IMAGES = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTextInput()
        setSubmitPaymentInfo()

        setupImageSelection()
        setupRecyclerView()
        observeViewModel()

        binding.toolbarPost.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setTextInput() {
        binding.etPostItemTitle.doAfterTextChanged {
            postTitle = it?.toString() ?: ""
            isValidTitle = isValidText(postTitle)
            updateButtonEnableState()
        }
        binding.etPostItemContent.doAfterTextChanged {
            postContent = it?.toString() ?: ""
            isValidContent = isValidText(postContent)
            updateButtonEnableState()
        }

        binding.categoryChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            isValidChip = checkedIds.isNotEmpty()
            updateButtonEnableState()
        }

        lifecycleScope.launch {
            viewModel.selectedImages.collect {
                isValidImage = it.isNotEmpty()
                updateButtonEnableState()
            }
        }
        updateButtonEnableState()
    }


    private fun isValidText(text: String): Boolean {
        return text.isNotBlank()
    }

    private fun updateButtonEnableState() {
        binding.btnRegisterCompleteButton.isEnabled =
            isValidTitle && isValidContent && isValidChip && isValidImage
    }

    private fun setSubmitPaymentInfo() {
        binding.btnRegisterCompleteButton.setOnClickListener {
            finish()
            TODO("게시글 정보 객체 생성")
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = PostImageAdapter { uri ->
            viewModel.removeImage(uri)
        }
        binding.rvPostPhotoList.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(this@PostActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedImages.collect { images ->
                imageAdapter.submitList(images)
            }
        }
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

    private fun openImagePicker() {
        if (viewModel.selectedImages.value.size >= MAX_IMAGES) {
            Toast.makeText(this, "이미 $MAX_IMAGES 장의 이미지를 선택하셨습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "이미지 선택"), IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            data?.let { intent ->
                val currentCount = viewModel.selectedImages.value.size
                if (currentCount >= MAX_IMAGES) {
                    Toast.makeText(this, "이미 $MAX_IMAGES 장의 이미지를 선택하셨습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

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
                    Toast.makeText(this, "최대 $MAX_IMAGES 장의 이미지를 선택하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}