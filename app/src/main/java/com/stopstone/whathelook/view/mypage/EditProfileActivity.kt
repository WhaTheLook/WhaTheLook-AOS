package com.stopstone.whathelook.view.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.stopstone.whathelook.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(
            layoutInflater
        )
    }
    private val args: EditProfileActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Glide.with(this)
            .load(args.user.profileImage)
            .into(binding.ivEditProfileImage)
        binding.etEditProfileNickname.hint = args.user.name

        binding.toolbarEditProfile.setNavigationOnClickListener {
            finish()
        }
    }
}