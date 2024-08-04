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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostListRepository: PostListRepository,
) : ViewModel() {
    private val _posts = MutableLiveData<List<PostListItem>>()
    val posts: LiveData<List<PostListItem>> = _posts



    fun loadPosts() {
        viewModelScope.launch {
            val posts = getPostListRepository.getPostList()
            _posts.value = posts.content
            Log.d("HomeViewModel", "Loaded posts: $posts")
        }
    }
}