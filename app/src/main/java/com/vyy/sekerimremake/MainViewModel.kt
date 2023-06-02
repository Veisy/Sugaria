package com.vyy.sekerimremake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.auth.domain.repository.UserInfoResponse
import com.vyy.sekerimremake.features.auth.domain.usecase.AuthUseCases
import com.vyy.sekerimremake.features.chart.domain.repository.GetChartResponse
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
    private val _userInfoResponse = MutableStateFlow<UserInfoResponse>(Response.Loading)
    val userInfoResponse = _userInfoResponse.asStateFlow()
    private var getUserInfoJob: Job? = null

    private val _chartResponse = MutableStateFlow<GetChartResponse>(Response.Loading)
    val chartResponse = _chartResponse.asStateFlow()
    private var getChartJob: Job? = null

    //TODO: Inject Dispatchers
    fun getUserInfo() {
        getUserInfoJob?.cancel()
        getUserInfoJob = viewModelScope.launch(Dispatchers.IO) {
            authUseCases.getUserInfoUseCase().collectLatest { response ->
                _userInfoResponse.update { response }
                if (response is Response.Success && response.data?.userId != null) {
                    getChart(response.data.userId!!)
                } else {
                    _chartResponse.update { Response.Error("User not found") }
                }
            }
        }
    }

    private fun getChart(userId: String) {
        getChartJob?.cancel()
        getChartJob = viewModelScope.launch(Dispatchers.IO) {
            chartUseCases.getChartUserCase(userId).collectLatest { response ->
                _chartResponse.update { response }
            }
        }
    }

    fun getAuthState() = authUseCases.getAuthState().shareIn(viewModelScope, SharingStarted.Eagerly)
}