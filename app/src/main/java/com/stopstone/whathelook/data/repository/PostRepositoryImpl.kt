package com.stopstone.whathelook.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.Gson
import com.stopstone.whathelook.data.api.ApiService
import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.data.model.PostRequest
import com.stopstone.whathelook.domain.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val contentResolver: ContentResolver
) : PostRepository {

    override suspend fun createPost(post: Post): Result<String> = withContext(Dispatchers.IO) {
        try {
            val postRequest = createPostRequest(post)
            val postRequestBody = createPostRequestBody(postRequest)
            val photoParts = createPhotoParts(post.imageUris)

            val response = apiService.createPost(postRequestBody, photoParts)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImages(images: List<Uri>): List<String> {
        return emptyList()
    }

    private fun createPostRequest(post: Post) = PostRequest(
        kakaoId = post.kakaoId,
        title = post.title,
        content = post.content,
        category = post.category,
        hashtags = post.hashtags
    )

    private fun createPostRequestBody(postRequest: PostRequest): RequestBody {
        val postRequestJson = Gson().toJson(postRequest)
        return postRequestJson.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private suspend fun createPhotoParts(imageUris: List<Uri>): List<MultipartBody.Part> = withContext(Dispatchers.IO) {
        imageUris.mapNotNull { uri ->
            try {
                contentResolver.openInputStream(uri)?.use { stream ->
                    val byteArray = stream.readBytes()
                    MultipartBody.Part.createFormData(
                        "photos",
                        "image_${System.currentTimeMillis()}.jpg",
                        byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}