package com.stopstone.whathelook.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val imageUrl: String,
): Parcelable