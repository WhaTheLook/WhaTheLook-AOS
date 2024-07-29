package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.data.model.UserInfo
import com.stopstone.whathelook.domain.repository.UserRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserInfo> {
        return userRepository.getUserInfo()
    }
}