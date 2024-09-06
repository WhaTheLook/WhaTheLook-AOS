package com.stopstone.whathelook.data.api

import com.stopstone.whathelook.data.model.request.RequestComment
import com.stopstone.whathelook.data.model.request.UpdateLikeRequest
import com.stopstone.whathelook.data.model.response.CommentResponse
import com.stopstone.whathelook.data.model.response.PostDetailResponse
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.data.model.response.PostListResponse
import com.stopstone.whathelook.data.model.response.SearchResponse
import com.stopstone.whathelook.data.model.response.UpdateLikeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApiService {
    @Multipart
    @POST("/post/create")
    suspend fun createPost(
        @Part("postRequest") postRequest: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): String

    @POST("/post/comment/create")
    suspend fun createComment(
        @Body requestComment: RequestComment
    ): CommentResponse

    @GET("/post/postList")
    suspend fun getPostList(
        @Query("lastPostId") lastPostId: Long? = null,
        @Query("category") category: String,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "recent"
    ): PostListResponse

    @GET("/post/postList/{search}")
    suspend fun searchPosts(
        @Path("search") search: String,
        @Query("lastPostId") lastPostId: Long? = null,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "recent",
        @Query("category") category: String? = null
    ): SearchResponse

    @GET("/post/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: Long
    ): PostListItem


    @POST("/post/like")
    suspend fun updateLike(
        @Body requestLike: UpdateLikeRequest
    ): UpdateLikeResponse

    @DELETE("/post/delete/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long
    ): String
}