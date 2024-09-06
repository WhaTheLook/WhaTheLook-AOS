package com.stopstone.whathelook.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.stopstone.whathelook.R

object HashtagUtils {
    fun setClickableHashtags(context: Context, textView: TextView, content: String, hashtags: List<String>) {
        val spannableString = SpannableString(content)

        hashtags.forEach { hashtag ->
            val startIndex = content.indexOf(hashtag)
            if (startIndex != -1) {
                val endIndex = startIndex + hashtag.length
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            Log.d("HashtagUtils", "해시태그 클릭됨: $hashtag")
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ContextCompat.getColor(context, R.color.gray_900)
                            ds.isUnderlineText = false
                        }
                    },
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        textView.text = spannableString
        textView.movementMethod = object : LinkMovementMethod() {
            override fun onTouchEvent(widget: TextView, buffer: Spannable, event: android.view.MotionEvent): Boolean {
                val result = super.onTouchEvent(widget, buffer, event)
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    widget.invalidate() // 터치 종료 시 TextView를 다시 그려 배경을 제거합니다.
                }
                return result
            }
        }
    }
}