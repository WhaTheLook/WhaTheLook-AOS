package com.stopstone.whathelook.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PostListResponse(
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val content: List<PostListItem>,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val pageable: Pageable,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

@Parcelize
data class PostListItem(
    val id: Long,
    val author: Author,
    val title: String,
    val content: String,
    val category: String,
    val date: String,
    val likeCount: Int,
    val likeYN: Boolean,
    val commentCount: Int,
    val hashtags: List<String>,
    val photoUrls: List<String>,
    val comments: List<Comment>? = null
) : Parcelable

@Parcelize
data class Author(
    val kakaoId: String,
    val name: String,
    val profileImage: String
) : Parcelable

@Parcelize
data class Comment(
    val id: Long,
    val author: Author,
    val text: String,
    val date: String,
    val depth: Int,
    val replyComment: List<Comment>? = null
): Parcelable

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

data class Pageable(
    val offset: Long,
    val sort: Sort,
    val paged: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val unpaged: Boolean
)