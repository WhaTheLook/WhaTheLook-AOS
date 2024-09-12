package com.stopstone.whathelook.data.repository.post

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.stopstone.whathelook.data.api.PostApiService
import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.data.model.entity.UpdatePostModel
import com.stopstone.whathelook.data.model.request.CreatePostRequestModel
import com.stopstone.whathelook.data.model.request.UpdatePostRequestModel
import com.stopstone.whathelook.domain.repository.post.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
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

    private fun createUpdatePostRequest(updatePostModel: UpdatePostModel) = UpdatePostRequestModel(
        id = updatePostModel.id,
        author = updatePostModel.author,
        title = updatePostModel.title,
        content = updatePostModel.content,
        category = updatePostModel.category,
        hashtags = updatePostModel.hashtags
    )

    private fun createPostRequestBody(createPostRequestModel: CreatePostRequestModel): RequestBody {
        val postRequestJson = Gson().toJson(createPostRequestModel)
        return postRequestJson.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private fun createUpdatePostRequestBody(updatePostRequestModel: UpdatePostRequestModel): RequestBody {
        val postRequestJson = Gson().toJson(updatePostRequestModel)
        return postRequestJson.toRequestBody("application/json".toMediaTypeOrNull())
    }

    private suspend fun createPhotoParts(imageUris: List<Uri>): List<MultipartBody.Part> = withContext(Dispatchers.IO) {
        imageUris.mapNotNull { uri ->
            try {
                // 만약 URI가 URL이라면, URL에서 이미지를 다운로드하여 처리
                if (uri.scheme == "http" || uri.scheme == "https") {
                    val url = URL(uri.toString())
                    val connection = url.openConnection()
                    connection.connect()

                    val inputStream = connection.getInputStream()
                    val byteArray = inputStream.readBytes()

                    MultipartBody.Part.createFormData(
                        "photos",
                        "image_${System.currentTimeMillis()}.jpg",
                        byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                } else {
                    // 로컬 파일 URI인 경우 처리
                    contentResolver.openInputStream(uri)?.use { stream ->
                        val byteArray = stream.readBytes()

                        MultipartBody.Part.createFormData(
                            "photos",
                            "image_${System.currentTimeMillis()}.jpg",
                            byteArray.toRequestBody("image/*".toMediaTypeOrNull())
                        )
                    }
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

    override suspend fun updatePost(updatePostModel: UpdatePostModel): String {
        Log.d("PostRepositoryImpl", "게시글 수정 요청: $updatePostModel")
        try {
            val postRequest = createUpdatePostRequest(updatePostModel)
            val postRequestBody = createUpdatePostRequestBody(postRequest)
            val photoParts = createPhotoParts(updatePostModel.imageUris)

            val response = postApiService.updatePost(postRequestBody, photoParts)
            Log.d("PostRepositoryImpl", "게시글 수정 응답: $response")
            return response
        } catch (e: Exception) {
            Log.e("PostRepositoryImpl", "게시글 수정 실패", e)
            throw e
        }
    }
}