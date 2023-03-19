package com.vyy.sekerimremake.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vyy.sekerimremake.features.auth.data.AuthRepositoryImp
import com.vyy.sekerimremake.features.auth.domain.usecase.AuthUseCases
import com.vyy.sekerimremake.features.auth.domain.usecase.GetAuthStateUseCase
import com.vyy.sekerimremake.features.chart.data.repository.ChartRepositoryImp
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.domain.use_case.AddDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.features.chart.domain.use_case.DeleteDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.GetChartUseCase
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.USERS
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
    fun provideUsersRef(
        db: FirebaseFirestore
    ) = db.collection(USERS)

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth
    ) = AuthRepositoryImp(auth = auth)

    @Provides
    fun provideChartRepository(
        auth: FirebaseAuth,
        usersRef: CollectionReference
    ): ChartRepository = ChartRepositoryImp(auth = auth, usersRef = usersRef)

    @Provides
    fun provideAuthUseCases(
        repo: AuthRepositoryImp
    ) = AuthUseCases(
        getAuthState = GetAuthStateUseCase(repo)
    )

    @Provides
    fun provideChartUseCases(
        repo: ChartRepository
    ) = ChartUseCases(
        getChart = GetChartUseCase(repo),
        addDayUseCase = AddDayUseCase(repo),
        deleteDayUseCase = DeleteDayUseCase(repo)
    )
}