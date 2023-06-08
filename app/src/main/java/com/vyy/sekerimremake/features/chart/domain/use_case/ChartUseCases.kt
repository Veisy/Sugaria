package com.vyy.sekerimremake.features.chart.domain.use_case

data class ChartUseCases(
    val getChartUserCase: GetChartUseCase,
    val addDayUseCase: AddDayUseCase,
    val deleteDayUseCase: DeleteDayUseCase
)