package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.data.model.response.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(): UserInfo
}