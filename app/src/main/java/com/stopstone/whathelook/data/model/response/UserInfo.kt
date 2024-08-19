package com.stopstone.whathelook.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val kakaoId: String,
    val email: String,
    val name: String,
    val profileImage: String,
    val date: String
): Parcelable
