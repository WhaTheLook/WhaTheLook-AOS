package com.stopstone.whathelook.ui.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.stopstone.whathelook.data.api.ApiService
import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.data.model.PostRequest
import com.stopstone.whathelook.domain.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {


    private val _postState = MutableStateFlow(Post("", "", "", "", "", emptyList(), emptyList()))
    val postState: StateFlow<Post> = _postState.asStateFlow()

    val isSubmitEnabled = postState.map { post ->
        post.title.isNotBlank() && post.content.isNotBlank() &&
                post.category.isNotBlank() && post.imageUris.isNotEmpty()
    }

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _postTitle = MutableStateFlow("")
    val postTitle: StateFlow<String> = _postTitle.asStateFlow()

    private val _postContent = MutableStateFlow("")
    val postContent: StateFlow<String> = _postContent.asStateFlow()

    private val _isChipSelected = MutableStateFlow(false)
    val isChipSelected: StateFlow<Boolean> = _isChipSelected.asStateFlow()

    private val _hashtagList = MutableStateFlow<List<String>>(emptyList())
    val hashtagList: StateFlow<List<String>> = _hashtagList.asStateFlow()

    private val _postCreationResult = MutableSharedFlow<Result<String>>()
    val postCreationResult: SharedFlow<Result<String>> = _postCreationResult.asSharedFlow()

    private val _kakaoId = MutableStateFlow("")
    val kakaoId: StateFlow<String> = _kakaoId.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()


    fun setPostTitle(title: String) {
        _postTitle.value = title
        updatePostState()
    }

    fun setPostContent(content: String) {
        _postContent.value = content
        updatePostState()
    }

    fun setCategory(category: String) {
        _category.value = category
        updatePostState()
    }

    fun addImage(uri: Uri) {
        if (_selectedImages.value.size < MAX_IMAGES) {
            _selectedImages.value += uri
            updatePostState()
        }
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value -= uri
        updatePostState()
    }

    private fun updatePostState() {
        _postState.value = Post(
            title = _postTitle.value,
            content = _postContent.value,
            category = _category.value,
            kakaoId = _kakaoId.value,
            hashtags = _hashtagList.value,
            imageUris = _selectedImages.value
        )
    }

    fun setChipSelected(selected: Boolean) {
        _isChipSelected.value = selected
    }

    fun setKakaoId(id: String) {
        _kakaoId.value = id
    }

    fun extractHashtags() {
        _hashtagList.value = _postContent.value.split("#")
            .filter { it.isNotBlank() }
            .map { "#$it" }
    }

    fun createPost() {
        viewModelScope.launch {
            val result = createPostUseCase(_postState.value)
            _postCreationResult.emit(result)
        }
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}