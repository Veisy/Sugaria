package com.vyy.sekerimremake.features.chart.domain.use_case

import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel
import com.vyy.sekerimremake.features.chart.domain.repository.ChartRepository
import com.vyy.sekerimremake.features.chart.utils.ChartUtils.generateRowId
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

class AddRow(
    private val repo: ChartRepository
) {
    // TODO: Is 'suspend' really redundant or this is a IDE bug again.
    suspend operator fun invoke(chartRowModel: ChartRowModel): Flow<Response<Void?>> {
        with(chartRowModel) {
            rowId = generateRowId(
                day = dayOfMonth,
                month = month,
                year = year
            )
        }
        return repo.addChartRow(chartRowModel)
    }
}