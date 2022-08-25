package com.vyy.sekerimremake.features.chart.utils

object ChartUtils {
    //Row id includes year, month and day, in that order. For exp. "20220925"
    //
    fun generateRowId(day: String?, month: String?, year: String?)  =
        year + convertToDoubleDigitWithZero(month) + convertToDoubleDigitWithZero(day)

    // Convert single digit day and month values such as "0" "1" "2" ... "9" to "00" "01" "02" ... "09"
    private fun convertToDoubleDigitWithZero(value: String?) =
        when(value) {
            "0" -> "00"
            "1" -> "01"
            "2" -> "02"
            "3" -> "03"
            "4" -> "04"
            "5" -> "05"
            "6" -> "06"
            "7" -> "07"
            "8" -> "08"
            "9" -> "09"
            else -> value
        }
}