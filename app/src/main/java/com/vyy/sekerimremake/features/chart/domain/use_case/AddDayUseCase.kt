package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class AddDayUseCase(
    private val repo: ChartRepository
) {
    suspend operator fun invoke(userId: String, chartDayModel: ChartDayModel) =
        repo.addChartRow(userId, chartDayModel)
}