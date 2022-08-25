package com.vyy.sekerimremake.features.chart.presenter.master

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel
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
    var chartResponse = MutableStateFlow<Response<List<ChartRowModel>>>(Response.Loading)
    private set
    var addRowResponse = MutableStateFlow<Response<Void?>>(Response.Success(null))
        private set
    var deleteRowResponse = MutableStateFlow<Response<Void?>>(Response.Success(null))
        private set

    init {
        getChart()
    }

    private fun getChart() = viewModelScope.launch {
        useCases.getChart().collect { response ->
            chartResponse.update { response }
        }
    }

    fun addRow(chartRowModel: ChartRowModel) = viewModelScope.launch {
        useCases.addRow(chartRowModel).collect { response ->
            addRowResponse.update { response }
        }
    }

    fun deleteRow(rowId: String) = viewModelScope.launch {
        useCases.deleteRow(rowId).collect { response ->
            deleteRowResponse.update { response }
        }
    }
}