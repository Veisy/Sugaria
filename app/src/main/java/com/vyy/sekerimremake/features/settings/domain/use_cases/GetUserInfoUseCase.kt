package com.vyy.sekerimremake.features.settings.domain.use_cases

import com.vyy.sekerimremake.features.settings.domain.repository.SettingsRepository

class GetUserInfoUseCase(
    private val repo: SettingsRepository
) {
    operator fun invoke() = repo.getUserInfo()
}