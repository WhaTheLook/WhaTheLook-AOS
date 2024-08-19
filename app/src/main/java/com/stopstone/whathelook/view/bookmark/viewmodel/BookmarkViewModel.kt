package com.stopstone.whathelook.view.bookmark.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.repository.bookmark.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
): ViewModel() {
    private val _bookmarks = MutableStateFlow<List<PostListItem>>(emptyList())
    val bookmarks = _bookmarks

    init {
        fetchBookmarks()
    }

    private fun fetchBookmarks() = viewModelScope.launch {
        val response = bookmarkRepository.getBookmarks()
        _bookmarks.value = response.content
    }
}