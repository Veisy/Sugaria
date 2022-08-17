package com.vyy.sekerimremake.features.chart.domain.repository

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

interface ChartRepository {
    fun getChartFromFirestore(): Flow<Response<List<ChartDayModel>>>

}