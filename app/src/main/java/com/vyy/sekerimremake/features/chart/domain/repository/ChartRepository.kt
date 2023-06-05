package com.vyy.sekerimremake.features.chart.domain.repository

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias AddChartResponse = Response<Boolean>
typealias DeleteChartResponse = Response<Boolean>
typealias GetChartResponse = Response<List<ChartDayModel>>

interface ChartRepository {
    fun getChart(uid: String): Flow<GetChartResponse>

    suspend fun addChartRow(chartDayModel: ChartDayModel): AddChartResponse

    suspend fun deleteChartRow(dayId: String): DeleteChartResponse
}