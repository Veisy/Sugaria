package com.vyy.sekerimremake.features.chart.domain.model

data class ChartRowModel(
    var rowId: String? = null,
    var dayOfMonth: String? = null,
    var month: String? = null,
    var year: String? = null,
    var morningEmpty: String? = null,
    var morningFull: String? = null,
    var afternoonEmpty: String? = null,
    var afternoonFull: String? = null,
    var eveningEmpty: String? = null,
    var eveningFull: String? = null,
    var night: String? = null
)