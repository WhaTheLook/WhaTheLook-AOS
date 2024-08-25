package com.stopstone.whathelook.domain.usecase.mypage

import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserInfo {
        return userRepository.getUserInfo()
    }
}