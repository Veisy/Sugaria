package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class DeleteDayUseCase(
    private val repo: ChartRepository
) {
    suspend operator fun invoke(userId: String, dayId: String) = repo.deleteChartRow(userId, dayId)
}
