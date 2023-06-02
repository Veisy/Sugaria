package com.vyy.sekerimremake.features.auth.domain.repository

import com.vyy.sekerimremake.features.auth.domain.model.UserInfoModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias AuthStateResponse = Flow<Boolean>
typealias UserInfoResponse = Response<UserInfoModel?>

interface AuthRepository {
    fun getUserInfo(): Flow<UserInfoResponse>
    fun getAuthState(): AuthStateResponse
}