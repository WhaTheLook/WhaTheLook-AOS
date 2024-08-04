package com.stopstone.whathelook.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.CreatePostModel
import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.data.model.PostListResponse
import com.stopstone.whathelook.data.repository.MockRepository
import com.stopstone.whathelook.domain.repository.PostListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListRepository: PostListRepository,
) : ViewModel() {
    private val _posts = MutableStateFlow<List<PostListItem>>(emptyList())
    val posts: StateFlow<List<PostListItem>> = _posts.asStateFlow()


    fun loadPosts() {
        viewModelScope.launch {
            val posts = getPostListRepository.getPostList()
            _posts.value = posts.content
            Log.d("HomeViewModel", "Loaded posts: $posts")
        }
    }
}