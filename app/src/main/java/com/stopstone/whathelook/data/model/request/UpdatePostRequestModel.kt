package com.stopstone.whathelook.data.model.request

import android.net.Uri

data class UpdatePostRequestModel(
    val id: Long,
    val author: String,
    val title: String,
    val content: String,
    val category: String,
    val hashtags: List<String>,
)