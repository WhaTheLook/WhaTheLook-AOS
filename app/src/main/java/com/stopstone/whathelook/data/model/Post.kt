package com.stopstone.whathelook.data.model

data class Post(
    val id: Int,
    val title: String,
    val writer: User,
    val content: String,
    val createdAt: String,
    val type: Boolean,
    val deleted: Boolean,
)