package com.stopstone.whathelook.data.repository.post

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.Gson
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.data.model.request.CreatePostRequestModel
import com.stopstone.whathelook.domain.repository.post.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApiService: PostApiService,
    private val contentResolver: ContentResolver
) : PostRepository {

    override suspend fun createPost(createPostModel: CreatePostModel): Result<String> = withContext(Dispatchers.IO) {
        try {
            val postRequest = createPostRequest(createPostModel)
            val postRequestBody = createPostRequestBody(postRequest)
            val photoParts = createPhotoParts(createPostModel.imageUris)

            val response = postApiService.createPost(postRequestBody, photoParts)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImages(images: List<Uri>): List<String> {
        return emptyList()
    }

    private fun createPostRequest(createPostModel: CreatePostModel) = CreatePostRequestModel(
        kakaoId = createPostModel.kakaoId,
        title = createPostModel.title,
        content = createPostModel.content,
        category = createPostModel.category,
        hashtags = createPostModel.hashtags
    )

    private fun createPostRequestBody(createPostRequestModel: CreatePostRequestModel): RequestBody {
        val postRequestJson = Gson().toJson(createPostRequestModel)
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

    override suspend fun deletePost(postId: Long) = withContext(Dispatchers.IO) {
        val response = postApiService.deletePost(postId)
        if (response.isEmpty()) {
            throw Exception("Failed to delete post")
        }
    }
}