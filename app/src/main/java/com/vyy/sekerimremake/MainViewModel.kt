package com.vyy.sekerimremake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.auth.domain.usecase.AuthUseCases
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val chartUseCases: ChartUseCases
) : ViewModel() {
    private val _chartResponse = MutableStateFlow<Response<List<ChartDayModel>>>(Response.Loading)
    val chartResponse = _chartResponse.asStateFlow()
    private var getChartJob: Job? = null

    //TODO: Inject Dispatchers
    fun getChart() {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch(Dispatchers.IO) {
            chartUseCases.getChart().collectLatest { response ->
                _chartResponse.update { response }
            }
        }
    }

    fun getAuthState() = authUseCases.getAuthState().shareIn(viewModelScope, SharingStarted.Eagerly)
}