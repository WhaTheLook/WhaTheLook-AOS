package com.stopstone.whathelook.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor() : ViewModel() {
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

    val isSubmitEnabled = combine(
        postTitle,
        postContent,
        isChipSelected,
        selectedImages
    ) { title, content, chipSelected, images ->
        title.isNotBlank() && content.isNotBlank() && chipSelected && images.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun setPostTitle(title: String) {
        _postTitle.value = title
    }

    fun setPostContent(content: String) {
        _postContent.value = content
    }

    fun setChipSelected(selected: Boolean) {
        _isChipSelected.value = selected
    }

    fun addImage(uri: Uri) {
        if (_selectedImages.value.size < MAX_IMAGES) {
            _selectedImages.value = _selectedImages.value + uri
        }
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value - uri
    }

    fun extractHashtags() {
        val hashtags = _postContent.value.split("#").filter { it.isNotBlank() }.map { "#$it" }
        _hashtagList.value = hashtags
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}