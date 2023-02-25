package com.vyy.sekerimremake.features.chart.utils

object ChartConstants {

    //Firestore
    const val CHARTS = "charts"

    val STATE_KEYS = arrayOf(
        "MORNING_EMPTY_STATE", "MORNING_FULL_STATE",
        "AFTERNOON_EMPTY_STATE", "AFTERNOON_FULL_STATE", "EVENING_EMPTY_STATE",
        "EVENING_FULL_STATE", "NIGHT_STATE"
    )

    const val INITIAL_BUTTON_FLAG = "INITIAL_BUTTON_FLAG"
    const val DAY_ID = "DAY_ID_SAVED"
    const val BUTTON_FLAG = "BUTTON_FLAG_SAVED"
    const val DAY_OF_MONTH = "DAY_OF_MONTH_SAVED"
    const val MONTH = "MONTH_SAVED"
    const val YEAR = "YEAR_SAVED"
}