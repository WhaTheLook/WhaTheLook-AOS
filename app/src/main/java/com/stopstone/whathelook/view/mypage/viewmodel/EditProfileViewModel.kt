package com.stopstone.whathelook.view.mypage.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.domain.usecase.mypage.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isChanged = MutableStateFlow(false)
    val isChanged: StateFlow<Boolean> = _isChanged.asStateFlow()

    private val _updateResult = MutableStateFlow<Result<String>?>(null)
    val updateResult: StateFlow<Result<String>?> = _updateResult.asStateFlow()

    private var isNameChanged = false
    private var isImageChanged = false

    fun onNameChanged() {
        isNameChanged = true
        checkForChanges()
    }

    fun onImageChanged() {
        isImageChanged = true
        checkForChanges()
    }

    private fun checkForChanges() {
        _isChanged.value = isNameChanged || isImageChanged
    }

    fun updateProfile(kakaoId: String, name: String?, email: String?, profileImageUri: Uri?) {
        viewModelScope.launch {
            val jsonData = JSONObject().apply {
                put("kakaoId", kakaoId)
                if (isNameChanged) name?.let { put("name", it) }
                email?.let { put("email", it) }
            }

            val jsonPart = jsonData.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart = if (isImageChanged && profileImageUri != null) {
                val inputStream = context.contentResolver.openInputStream(profileImageUri)
                val file = File(context.cacheDir, "temp_profile_image")
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
            } else null

            val result = updateUserUseCase(jsonPart, imagePart)
            _updateResult.value = result
        }
    }
}