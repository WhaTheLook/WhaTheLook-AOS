package com.stopstone.whathelook.data.model.request

data class UpdateUserRequest(
    val kakaoId: String,
    val name: String?,
    val email: String?,
    val profileImage: String?
)