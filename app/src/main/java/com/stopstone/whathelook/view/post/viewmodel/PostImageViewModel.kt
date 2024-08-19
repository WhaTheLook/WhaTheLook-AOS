package com.stopstone.whathelook.view.post.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PostImageViewModel @Inject constructor() : ViewModel() {
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    fun addImage(uri: Uri) {
        if (_selectedImages.value.size < 5) {
            _selectedImages.value += uri
        }
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value -= uri
    }
}