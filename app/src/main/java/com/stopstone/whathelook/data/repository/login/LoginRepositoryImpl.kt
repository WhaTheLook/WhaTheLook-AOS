package com.stopstone.whathelook.data.repository.login

import com.stopstone.whathelook.data.api.LoginService
import com.stopstone.whathelook.data.model.request.LoginRequest
import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.repository.login.LoginRepository
import javax.inject.Inject
/*
* 로그인 후 토큰을 발행하는 API 요청
* */
class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun login(kakaoAccessToken: String): Tokens {
        val response = loginService.login(LoginRequest(kakaoAccessToken))
        return Tokens(response.accessToken, response.refreshToken)
    }
}