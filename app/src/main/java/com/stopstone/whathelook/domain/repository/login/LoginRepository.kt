package com.stopstone.whathelook.domain.repository.login

import com.stopstone.whathelook.domain.model.Tokens

interface LoginRepository {
    suspend fun login(kakaoAccessToken: String): Tokens
}