package com.vyy.sekerimremake.features.chart.domain.repository

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias AddChartResponse = Response<Boolean>
typealias DeleteChartResponse = Response<Boolean>
typealias GetChartResponse = Response<List<ChartDayModel>>

interface ChartRepository {
    fun getChart(userId: String): Flow<GetChartResponse>

    suspend fun addChartRow(userId: String, chartDayModel: ChartDayModel): AddChartResponse

    suspend fun deleteChartRow(userId: String, id: String): DeleteChartResponse
}