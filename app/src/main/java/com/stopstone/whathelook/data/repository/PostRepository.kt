package com.stopstone.whathelook.data.repository

import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.data.model.User
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PostRepository @Inject constructor() {
    fun getDummyPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val users = listOf(
            User(1, "김철수", "https://example.com/profile1.jpg", "2023-01-01"),
            User(2, "이영희", "https://example.com/profile2.jpg", "2023-02-15"),
            User(3, "박지훈", "https://example.com/profile3.jpg", "2023-03-20"),
            User(4, "정수민", "https://example.com/profile4.jpg", "2023-04-10"),
            User(5, "홍길동", "https://example.com/profile5.jpg", "2023-05-05")
        )

        for (i in 1..20) {
            posts.add(
                Post(
                    id = i,
                    title = "게시글 제목 $i",
                    writer = users[i % users.size],
                    content = "이것은 게시글 ${i}의 내용입니다. 더미 데이터로 생성되었습니다.",
                    createdAt = sdf.format(Date(System.currentTimeMillis() - (i * 86400000))),
                    type = i % 2 == 0, // 짝수 번호는 true, 홀수 번호는 false
                    deleted = false
                )
            )
        }
        return posts
    }
}