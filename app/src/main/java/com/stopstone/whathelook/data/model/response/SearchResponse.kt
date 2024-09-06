package com.stopstone.whathelook.data.model.response

data class SearchResponse(
    val total: Int,
    val posts: SearchPostList
)

data class SearchPostList(
    val content: List<PostListItem>,
    val pageable: Pageable,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)