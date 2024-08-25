package com.stopstone.whathelook.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadCircleImage(
    url: String?,
    placeholder: Int? = null,
    error: Int? = null
) {
    val requestOptions = RequestOptions()
        .circleCrop()
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
        }

    Glide.with(context)
        .load(url)
        .apply(requestOptions)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadCenterCropImage(
    url: String?,
    placeholder: Int? = null,
    error: Int? = null
) {
    val requestOptions = RequestOptions()
        .centerCrop()
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
        }

    Glide.with(context)
        .load(url)
        .apply(requestOptions)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}
