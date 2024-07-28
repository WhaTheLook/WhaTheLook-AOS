package com.stopstone.whathelook.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.data.repository.MockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MockRepository
) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    init {
        loadPosts()
    }

    private fun loadPosts() {
        _posts.value = repository.getDummyPosts()
    }
}