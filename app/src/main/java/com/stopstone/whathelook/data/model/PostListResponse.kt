package com.stopstone.whathelook.data.model

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

data class PostListItem(
    val id: Long,
    val author: Author,
    val title: String,
    val content: String,
    val category: String,
    val date: String,
    val likeCount: Int,
    val commentCount: Int,
    val hashtags: List<String>,
    val photoUrls: List<String>
)

data class Author(
    val kakaoId: String,
    val name: String,
    val profileImage: String
)

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