package com.vyy.sekerimremake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.use_case.UseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {
    private val _chartResponse = MutableStateFlow<Response<List<ChartDayModel>>>(Response.Loading)
    val chartResponse = _chartResponse.asStateFlow()

    private var getChartJob: Job? = null

    init {
        getChart()
    }

    //TODO: Inject Dispatchers
    private fun getChart() {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch(Dispatchers.IO) {
            useCases.getChart().collect { response ->
                _chartResponse.update { response }
            }
        }
    }
}