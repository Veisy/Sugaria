package com.vyy.sekerimremake.features.settings.domain.use_cases

import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.features.settings.domain.repository.SettingsRepository

class RequestMonitorUseCase(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(user: UserModel) = repo.requestMonitor(user)
}