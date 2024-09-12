package com.stopstone.whathelook.data.model.entity

import android.net.Uri

data class UpdatePostModel(
    val id: Long,
    val author: String,
    val title: String,
    val content: String,
    val category: String,
    val hashtags: List<String>,
    val imageUris: List<Uri>
)