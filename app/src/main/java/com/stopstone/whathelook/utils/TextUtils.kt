package com.stopstone.whathelook.utils

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun TextView.setRelativeTimeText(dateString: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val date = sdf.parse(dateString) ?: return
    val now = Date()
    val diff = now.time - date.time
    val seconds = abs(diff) / 1000

    val relativeTime = when {
        seconds < 60 -> "방금 전"
        seconds < 60 * 60 -> "${seconds / 60}분 전"
        seconds < 60 * 60 * 24 -> "${seconds / (60 * 60)}시간 전"
        seconds < 60 * 60 * 24 * 30 -> "${seconds / (60 * 60 * 24)}일 전"
        seconds < 60 * 60 * 24 * 365 -> "${seconds / (60 * 60 * 24 * 30)}달 전"
        else -> "${seconds / (60 * 60 * 24 * 365)}년 전"
    }

    this.text = relativeTime
}
