package com.stopstone.whathelook.data.model

data class CreatePostRequestModel(
    val kakaoId: String,
    val title: String,
    val content: String,
    val hashtags: List<String>,
    val category: String,
)