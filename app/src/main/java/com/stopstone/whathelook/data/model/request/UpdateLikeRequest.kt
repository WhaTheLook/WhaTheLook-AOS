package com.stopstone.whathelook.data.model.request

data class UpdateLikeRequest(
    val postId: Long,
    val userId: Long?,
)