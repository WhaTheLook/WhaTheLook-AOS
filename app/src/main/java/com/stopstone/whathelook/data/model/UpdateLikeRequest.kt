package com.stopstone.whathelook.data.model

data class UpdateLikeRequest(
    val postId: Long,
    val userId: Long?,
)