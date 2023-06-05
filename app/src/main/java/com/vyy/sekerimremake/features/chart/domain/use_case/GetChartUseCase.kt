package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class GetChartUseCase(
    private val repo: ChartRepository
) {
    operator fun invoke(uid: String) = repo.getChart(uid)
}