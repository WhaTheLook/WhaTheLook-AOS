package com.stopstone.whathelook.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    val kakaoId: String,
    val title: String,
    val content: String,
    val category: String,
    val hashtags: List<String>,
    val imageUris: List<Uri>
): Parcelable