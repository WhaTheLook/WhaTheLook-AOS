package com.stopstone.whathelook.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val title: String,
    val writer: UserInfo,
    val content: String,
    val createdAt: String,
    val type: Boolean,
    val deleted: Boolean,
): Parcelable