package com.stopstone.whathelook.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.domain.usecase.GetPostListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListUseCase: GetPostListUseCase,
) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostListItem>>(emptyList())
    val posts: StateFlow<List<PostListItem>> = _posts.asStateFlow()


    fun loadPostList(category: String) = viewModelScope.launch {
        val posts = getPostListUseCase.invoke(category).content
        _posts.value = posts
        Log.d("HomeViewModel", "Loaded posts: $posts")
    }
}