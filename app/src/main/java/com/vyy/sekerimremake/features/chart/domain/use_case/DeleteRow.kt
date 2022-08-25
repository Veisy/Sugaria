package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository

class DeleteRow (
    private val repo: ChartRepository
) {
    suspend operator fun invoke(rowId: String) = repo.deleteChartRow(rowId)
}