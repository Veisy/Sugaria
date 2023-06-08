package com.vyy.sekerimremake.features.settings.domain.repository

import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias GetUserResponse = Response<UserModel?>
typealias RequestMonitorResponse = Response<Boolean>
typealias MonitoredInvitationResponse = Response<List<HashMap<String, String>>>

interface SettingsRepository {
    fun getUser(): Flow<GetUserResponse>

    suspend fun requestMonitor(user: UserModel): RequestMonitorResponse
}