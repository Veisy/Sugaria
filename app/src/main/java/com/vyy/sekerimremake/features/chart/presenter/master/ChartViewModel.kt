package com.vyy.sekerimremake.features.chart.presenter.master

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.use_case.UseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel  @Inject constructor(
    private val useCases: UseCases
): ViewModel() {
    var chartResponse = MutableStateFlow<Response<List<ChartDayModel>>>(Response.Loading)
    private set

    init {
        getChart()
    }

    private fun getChart() = viewModelScope.launch {
        useCases.getChart().collect { response ->
            chartResponse.update { response }
        }
    }
}