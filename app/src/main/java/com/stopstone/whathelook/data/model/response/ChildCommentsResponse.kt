package com.stopstone.whathelook.data.model.response

data class ChildCommentsResponse(
    val content: List<Comment>,
    val pageable: Pageable,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)