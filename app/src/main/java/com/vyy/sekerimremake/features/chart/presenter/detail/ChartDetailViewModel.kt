package com.vyy.sekerimremake.features.chart.presenter.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.model.ChartDayModel
import com.vyy.sekerimremake.features.chart.domain.repository.AddChartResponse
import com.vyy.sekerimremake.features.chart.domain.repository.DeleteChartResponse
import com.vyy.sekerimremake.features.chart.domain.use_case.UseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartDetailViewModel @Inject constructor(
    private val useCases: UseCases
) : ViewModel() {
    var addRowResponse = MutableSharedFlow<AddChartResponse>()
        private set
    var deleteRowResponse = MutableSharedFlow<DeleteChartResponse>()
        private set

    fun addRow(chartDayModel: ChartDayModel) = viewModelScope.launch {
        addRowResponse.emit (Response.Loading)
        addRowResponse.emit ( useCases.addRow(chartDayModel) )
    }

    fun deleteRow(rowId: String) = viewModelScope.launch {
        deleteRowResponse.emit (Response.Loading)
        deleteRowResponse.emit ( useCases.deleteRow(rowId) )
    }
}