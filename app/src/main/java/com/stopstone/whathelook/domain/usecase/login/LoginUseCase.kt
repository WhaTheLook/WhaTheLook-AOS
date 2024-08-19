package com.stopstone.whathelook.domain.usecase.login

import com.stopstone.whathelook.domain.model.Tokens
import com.stopstone.whathelook.domain.repository.login.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(kakaoAccessToken: String): Tokens {
        return loginRepository.login(kakaoAccessToken)
    }
}