package com.vyy.sekerimremake.features.auth.domain.repository

import kotlinx.coroutines.flow.Flow

typealias AuthStateResponse = Flow<Boolean>

interface AuthRepository {
    fun getAuthState(): AuthStateResponse
}