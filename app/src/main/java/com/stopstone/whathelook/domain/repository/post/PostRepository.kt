package com.stopstone.whathelook.domain.repository.post

import android.net.Uri
import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.data.model.entity.UpdatePostModel

interface PostRepository {
    suspend fun createPost(createPostModel: CreatePostModel): Result<String>
    suspend fun uploadImages(images: List<Uri>): List<String>
    suspend fun deletePost(postId: Long)
    suspend fun updatePost(updatePostModel: UpdatePostModel): String
}