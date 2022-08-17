package com.vyy.sekerimremake.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vyy.sekerimremake.features.chart.data.repository.ChartRepositoryImp
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.domain.use_case.GetChart
import com.vyy.sekerimremake.features.chart.domain.use_case.UseCases
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
    fun provideBooksRef(
        db: FirebaseFirestore
    ) = db.collection(CHARTS)

    @Provides
    fun provideBooksRepository(
        chartRef: CollectionReference
    ): ChartRepository = ChartRepositoryImp(chartRef)

    @Provides
    fun provideUseCases(
        repo: ChartRepository
    ) = UseCases(
        getChart = GetChart(repo)
    )
}