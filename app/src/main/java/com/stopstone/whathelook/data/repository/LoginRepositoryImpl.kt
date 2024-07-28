package com.stopstone.whathelook.data.repository

import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.model.LoginRequest
import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun login(kakaoAccessToken: String): Tokens {
        val response = loginService.login(LoginRequest(kakaoAccessToken))
        return Tokens(response.accessToken, response.refreshToken)
    }
}