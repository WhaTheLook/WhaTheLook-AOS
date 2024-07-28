package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend fun execute(kakaoAccessToken: String): Tokens {
        return loginRepository.login(kakaoAccessToken)
    }
}