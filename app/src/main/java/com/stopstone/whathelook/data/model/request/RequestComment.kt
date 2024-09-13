package com.stopstone.whathelook.data.model.request

data class RequestComment(
    val postId: Long,
    val userId: Long,
    val parentId: Long? = null,
    val text: String,
    val targetId: Long? = null
)