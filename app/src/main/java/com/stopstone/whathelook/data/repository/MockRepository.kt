package com.stopstone.whathelook.data.repository

import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.data.model.UserInfo
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MockRepository @Inject constructor() {
    fun getDummyPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val users = listOf(
            UserInfo(
                "2",
                "",
                "이영희",
                "https://thumbnail8.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/sfljdb3g/a0514217b99140b69bde6cb66d2ee914.jpg",
                "2023-02-15"
            ),
            UserInfo(
                "1",
                "",
                "김철수",
                "https://pbs.twimg.com/profile_images/1688763174714245120/htmgYD32_400x400.jpg",
                "2023-01-01"
            ),
            UserInfo(
                "3",
                "",
                "박지훈",
                "https://pbs.twimg.com/profile_images/1688763174714245120/htmgYD32_400x400.jpg",
                "2023-01-01"
            ),
            UserInfo(
                "4",
                "",
                "정수민",
                "https://thumbnail8.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/sfljdb3g/a0514217b99140b69bde6cb66d2ee914.jpg",
                "2023-01-01"
            ),
            UserInfo(
                "5",
                "",
                "홍길동",
                "https://pbs.twimg.com/profile_images/1688763174714245120/htmgYD32_400x400.jpg",
                "2023-01-01"
            )
        )

        for (i in 1..20) {
            posts.add(
                Post(
                    id = "$i",
                    kakaoId = "1234",
                    title = "게시글 제목 $i",
                    content = "이것은 게시글 ${i}의 내용입니다. 더미 데이터로 생성되었습니다.",
                    category = "질문하기",
                    hashtags = listOf("#해시태그1", "#해시태그2"),
                    imageUris = listOf()
                )
            )
        }
        return posts
    }
}