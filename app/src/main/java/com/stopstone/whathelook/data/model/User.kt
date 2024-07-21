package com.stopstone.whathelook.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    val id: Int,
    val name: String,
    val profileImageUrl: String,
    val date: String,
): Parcelable
