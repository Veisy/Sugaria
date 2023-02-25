package com.vyy.sekerimremake.features.chart.domain.model

data class ChartRowModel(
    var rowId: Int = 0,
    var dayOfMonth: Int = 0,
    var month: Int = 0,
    var year: Int = 0,
    var morningEmpty: Int = 0,
    var morningFull: Int = 0,
    var afternoonEmpty: Int = 0,
    var afternoonFull: Int = 0,
    var eveningEmpty: Int = 0,
    var eveningFull: Int = 0,
    var night: Int = 0
)