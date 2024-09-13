package com.stopstone.whathelook.data.model.response

data class CommentResponse(
    val id: Long,
    val author: Author,
    val text: String,
    val date: String,
    val accept: Boolean,
    val children: List<Comment> = emptyList(),
    val targetUser: Author? = null
)