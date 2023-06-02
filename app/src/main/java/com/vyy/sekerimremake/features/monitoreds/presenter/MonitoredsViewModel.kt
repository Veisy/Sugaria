package com.vyy.sekerimremake.features.monitoreds.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.repository.GetChartResponse
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoredsViewModel @Inject constructor(
    private val chartUseCases: ChartUseCases
) : ViewModel() {

    private val _monitoredChartResponse = MutableStateFlow<GetChartResponse>(Response.Loading)
    val chartResponse = _monitoredChartResponse.asStateFlow()
    private var monitoredChartJob: Job? = null

    fun getChart(userId: String) {
        monitoredChartJob?.cancel()
        monitoredChartJob = viewModelScope.launch(Dispatchers.IO) {
            chartUseCases.getChartUserCase(userId).collectLatest { response ->
                _monitoredChartResponse.update { response }
            }
        }
    }
}