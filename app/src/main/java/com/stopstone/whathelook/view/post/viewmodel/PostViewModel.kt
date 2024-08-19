package com.stopstone.whathelook.view.post.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.domain.usecase.post.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {


    private val _Create_postModelState = MutableStateFlow(CreatePostModel("", "", "", "", "", emptyList(), emptyList()))
    val createPostModelState: StateFlow<CreatePostModel> = _Create_postModelState.asStateFlow()

    val isSubmitEnabled = createPostModelState.map { post ->
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
        _Create_postModelState.value = CreatePostModel(
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
            val result = createPostUseCase(_Create_postModelState.value)
            _postCreationResult.emit(result)
        }
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}