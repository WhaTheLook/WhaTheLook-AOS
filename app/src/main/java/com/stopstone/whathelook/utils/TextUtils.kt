package com.stopstone.whathelook.utils

import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

fun TextView.setRelativeTimeText(dateString: String) {
    try {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val date = dateFormat.parse(dateString)

        val now = System.currentTimeMillis()
        val timeDifference = now - (date?.time ?: now)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifference)
        val days = TimeUnit.MILLISECONDS.toDays(timeDifference)

        val relativeTime = when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }

        text = relativeTime
    } catch (e: Exception) {
        Log.e("TextUtils", "Error parsing date: $dateString", e)
    }
}
