package com.stopstone.whathelook.domain.repository

import com.stopstone.whathelook.domain.model.Tokens

interface LoginRepository {
    suspend  fun login(kakaoAccessToken: String): Tokens
}