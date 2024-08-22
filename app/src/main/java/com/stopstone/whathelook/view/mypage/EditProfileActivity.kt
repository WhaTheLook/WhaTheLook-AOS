package com.stopstone.whathelook.view.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.stopstone.whathelook.databinding.ActivityEditProfileBinding
import com.stopstone.whathelook.view.mypage.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {
    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: EditProfileViewModel by viewModels()
    private val args: EditProfileActivityArgs by navArgs()

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        currentImageUri = Uri.parse(args.user.profileImage)

        Glide.with(this)
            .load(currentImageUri)
            .into(binding.ivEditProfileImage)

        binding.etEditProfileNickname.setText(args.user.name)

        binding.toolbarEditProfile.setNavigationOnClickListener {
            finish()
        }

        binding.etEditProfileNickname.addTextChangedListener {
            viewModel.onNameChanged()
        }

        binding.btnEditProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        binding.btnEditProfileButton.setOnClickListener {
            viewModel.updateProfile(
                kakaoId = args.user.kakaoId,
                name = binding.etEditProfileNickname.text.toString(),
                email = args.user.email,
                profileImageUri = currentImageUri
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.isChanged.collect { isChanged ->
                    binding.btnEditProfileButton.isEnabled = isChanged
                }
            }
            launch {
                viewModel.updateResult.collect { result ->
                    result?.fold(
                        onSuccess = {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "프로필이 성공적으로 업데이트되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        },
                        onFailure = { error ->
                            Toast.makeText(
                                this@EditProfileActivity,
                                "업데이트 실패: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                currentImageUri = uri
                viewModel.onImageChanged()
                Glide.with(this).load(uri).into(binding.ivEditProfileImage)
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }
}