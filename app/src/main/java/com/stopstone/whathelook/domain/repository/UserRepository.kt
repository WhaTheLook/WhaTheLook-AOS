package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(): Result<UserInfo>
}