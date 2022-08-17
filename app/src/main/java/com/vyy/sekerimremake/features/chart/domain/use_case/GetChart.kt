package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class GetChart(
    private val repo: ChartRepository
) {
    operator fun invoke() = repo.getChartFromFirestore()
}