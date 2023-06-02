package com.vyy.sekerimremake.features.auth.domain.usecase

import com.vyy.sekerimremake.features.auth.domain.repository.AuthRepository

class GetUserInfoUseCase(
    private val repo: AuthRepository
) {
    operator fun invoke() = repo.getUserInfo()
}