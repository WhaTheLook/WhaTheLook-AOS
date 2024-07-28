package com.stopstone.whathelook.data.model

data class PostCreationDto(
    val author: String,
    val title: String,
    val content: String,
    val category: String,
    val hashtags: List<String>
)

sealed class PostCreationState {
    object Initial : PostCreationState()
    object Loading : PostCreationState()
    object Success : PostCreationState()
    data class Error(val message: String) : PostCreationState()
}