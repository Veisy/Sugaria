package com.vyy.sekerimremake.features.settings.domain.repository

import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias GetUserResponse = Response<UserModel?>

interface SettingsRepository {
    fun getUser(): Flow<GetUserResponse>
}