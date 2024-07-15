package com.stopstone.whathelook.ui.view.post

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.ChipGroup
import com.stopstone.whathelook.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {
    private val binding: ActivityPostBinding by lazy { ActivityPostBinding.inflate(layoutInflater) }

    private var postTitle = ""
    private var postContent = ""

    private var isValidTitle = false
    private var isValidContent = false
    private var isValidChip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setTextInput()
        setSubmitPaymentInfo()
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
        updateButtonEnableState()
    }


    private fun isValidText(text: String): Boolean {
        return text.isNotBlank()
    }

    private fun updateButtonEnableState() {
        binding.btnRegisterCompleteButton.isEnabled =
            isValidTitle && isValidContent && isValidChip
    }

    private fun setSubmitPaymentInfo() {
        binding.btnRegisterCompleteButton.setOnClickListener {
            finish()
            TODO("게시글 정보 객체 생성")
        }
    }
}