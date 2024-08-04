package com.stopstone.whathelook.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stopstone.whathelook.data.model.CreatePostModel
import com.stopstone.whathelook.data.repository.MockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MockRepository
) : ViewModel() {
    private val _posts = MutableLiveData<List<CreatePostModel>>()
    val posts: LiveData<List<CreatePostModel>> = _posts

    init {
        loadPosts()
    }

    private fun loadPosts() {
        _posts.value = repository.getDummyPosts()
    }
}