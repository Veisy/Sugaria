package com.vyy.sekerimremake.features.auth.domain.usecase

data class AuthUseCases(
    val getAuthState: GetAuthStateUseCase,
    val getUserInfoUseCase: GetUserInfoUseCase
)