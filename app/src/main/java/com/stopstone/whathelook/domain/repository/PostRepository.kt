package com.stopstone.whathelook.domain.repository

import android.net.Uri
import com.stopstone.whathelook.data.model.Post

interface PostRepository {
    suspend fun createPost(post: Post): Result<String>
    suspend fun uploadImages(images: List<Uri>): List<String>
}