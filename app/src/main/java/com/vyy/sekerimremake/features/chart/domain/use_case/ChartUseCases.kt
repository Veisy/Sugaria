package com.vyy.sekerimremake.features.chart.domain.use_case

data class ChartUseCases (
    val getChart: GetChartUseCase,
    val addDayUseCase: AddDayUseCase,
    val deleteDayUseCase: DeleteDayUseCase
)