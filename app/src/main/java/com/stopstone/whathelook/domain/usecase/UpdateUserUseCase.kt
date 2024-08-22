package com.stopstone.whathelook.domain.usecase

import com.stopstone.whathelook.domain.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(jsonPart: RequestBody, imagePart: MultipartBody.Part?): Result<String> {
        return userRepository.updateUser(jsonPart, imagePart)
    }
}
