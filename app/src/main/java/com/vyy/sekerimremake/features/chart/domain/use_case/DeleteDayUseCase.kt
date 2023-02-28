package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class DeleteDayUseCase (
    private val repo: ChartRepository
) {
    suspend operator fun invoke(dayId: String) = repo.deleteChartRow(dayId)
}
