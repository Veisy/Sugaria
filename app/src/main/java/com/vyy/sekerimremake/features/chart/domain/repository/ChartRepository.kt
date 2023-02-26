package com.vyy.sekerimremake.features.chart.domain.repository

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias GetChartResponse = Response<List<ChartDayModel>>
typealias AddChartResponse = Response<Boolean>
typealias DeleteChartResponse = Response<Boolean>

interface ChartRepository {
    fun getChart(): Flow<Response<List<ChartDayModel>>>

    suspend fun addChartRow(chartDayModel: ChartDayModel): AddChartResponse

    suspend fun deleteChartRow(id: String): DeleteChartResponse
}