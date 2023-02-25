package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.AddChartResponse
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.utils.ChartUtils.generateRowId

class AddRow(
    private val repo: ChartRepository
) {
    suspend operator fun invoke(chartDayModel: ChartDayModel) = repo.addChartRow(chartDayModel)
}