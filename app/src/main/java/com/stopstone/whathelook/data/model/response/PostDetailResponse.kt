package com.stopstone.whathelook.data.model.response

data class PostDetailResponse(
    val id: Long,
    val author: Author,
    val title: String,
    val content: String,
    val category: String,
    val date: String,
    val likeCount: Int,
    val commentCount: Int,
    val likeYN: Boolean,
    val hashtags: List<String>,
    val photoUrls: List<String>,
    val comments: List<Comment>?
)