package com.vyy.sekerimremake.features.chart.presenter.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.AddChartResponse
import com.vyy.sekerimremake.features.chart.domain.repository.DeleteChartResponse
import com.vyy.sekerimremake.features.chart.domain.use_case.ChartUseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartDetailViewModel @Inject constructor(
    private val chartUseCases: ChartUseCases
) : ViewModel() {
    private val _addDayResponse = MutableSharedFlow<AddChartResponse>()
    val addDayResponse = _addDayResponse.asSharedFlow()

    private var _deleteDayResponse = MutableSharedFlow<DeleteChartResponse>()
    val deleteDayResponse = _deleteDayResponse.asSharedFlow()

    //TODO: Inject Dispatchers
    fun addDay(userId: String, chartDayModel: ChartDayModel) = viewModelScope.launch(Dispatchers.IO) {
        _addDayResponse.emit (Response.Loading)
        _addDayResponse.emit ( chartUseCases.addDayUseCase(userId, chartDayModel) )
    }

     fun deleteDay(userId: String, rowId: String) = viewModelScope.launch(Dispatchers.IO) {
        _deleteDayResponse.emit (Response.Loading)
        _deleteDayResponse.emit ( chartUseCases.deleteDayUseCase(userId, rowId) )
    }
}