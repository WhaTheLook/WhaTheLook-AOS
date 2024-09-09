package com.stopstone.whathelook.view.custom.gallery

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomGalleryViewModel @Inject constructor() : ViewModel() {
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images

    private val _selectedImages = MutableStateFlow<Set<Uri>>(emptySet())
    val selectedImages: StateFlow<Set<Uri>> = _selectedImages

    fun loadImages(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageList = mutableListOf<Uri>()
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    imageList.add(contentUri)
                }
            }
            _images.value = imageList
        }
    }

    fun toggleImageSelection(uri: Uri) {
        val currentSelection = _selectedImages.value.toMutableSet()
        if (currentSelection.contains(uri)) {
            currentSelection.remove(uri)
        } else if (currentSelection.size < CustomGalleryActivity.MAX_IMAGES) {
            currentSelection.add(uri)
        }
        _selectedImages.value = currentSelection
    }
}