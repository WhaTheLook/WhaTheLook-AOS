package com.stopstone.whathelook.domain.repository

import android.net.Uri
import com.stopstone.whathelook.data.model.CreatePostModel

interface PostRepository {
    suspend fun createPost(createPostModel: CreatePostModel): Result<String>
    suspend fun uploadImages(images: List<Uri>): List<String>
}