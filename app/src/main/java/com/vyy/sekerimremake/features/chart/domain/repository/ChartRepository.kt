package com.vyy.sekerimremake.features.chart.domain.repository

import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

interface ChartRepository {
    fun getChart(): Flow<Response<List<ChartRowModel>>>

    fun addChartRow(chartRowModel: ChartRowModel): Flow<Response<Void?>>

    fun deleteChartRow(id: String): Flow<Response<Void?>>
}