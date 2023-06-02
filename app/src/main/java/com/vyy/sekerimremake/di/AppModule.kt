package com.vyy.sekerimremake.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vyy.sekerimremake.features.auth.data.AuthRepositoryImp
import com.vyy.sekerimremake.features.auth.domain.usecase.AuthUseCases
import com.vyy.sekerimremake.features.auth.domain.usecase.GetAuthStateUseCase
import com.vyy.sekerimremake.features.auth.domain.usecase.GetUserInfoUseCase
import com.vyy.sekerimremake.features.chart.data.repository.ChartRepositoryImp
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.domain.use_case.AddDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.features.chart.domain.use_case.DeleteDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.GetChartUseCase
import com.vyy.sekerimremake.utils.FirestoreConstants.USERS
import com.vyy.sekerimremake.utils.FirestoreConstants.USER_CHARTS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ) = AuthRepositoryImp(auth = auth, usersRef = db.collection(USERS))

    @Provides
    fun provideChartRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): ChartRepository = ChartRepositoryImp(auth = auth, userChartsRef = db.collection(USER_CHARTS))

    @Provides
    fun provideAuthUseCases(
        repo: AuthRepositoryImp
    ) = AuthUseCases(
        getAuthState = GetAuthStateUseCase(repo),
        getUserInfoUseCase = GetUserInfoUseCase(repo)
    )

    @Provides
    fun provideChartUseCases(
        repo: ChartRepository
    ) = ChartUseCases(
        getChartUserCase = GetChartUseCase(repo),
        addDayUseCase = AddDayUseCase(repo),
        deleteDayUseCase = DeleteDayUseCase(repo)
    )
}