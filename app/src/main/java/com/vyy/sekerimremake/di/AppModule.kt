package com.vyy.sekerimremake.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vyy.sekerimremake.features.chart.data.repository.ChartRepositoryImp
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.domain.use_case.AddDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.DeleteDayUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.GetChartUseCase
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.features.chart.utils.ChartConstants.CHARTS
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
    fun provideChartsRef(
        db: FirebaseFirestore
    ) = db.collection(CHARTS)

    @Provides
    fun provideChartRepository(
        chartRef: CollectionReference
    ): ChartRepository = ChartRepositoryImp(chartRef)

    @Provides
    fun provideUseCases(
        repo: ChartRepository
    ) = ChartUseCases(
        getChart = GetChartUseCase(repo),
        addDayUseCase = AddDayUseCase(repo),
        deleteDayUseCase = DeleteDayUseCase(repo)
    )
}