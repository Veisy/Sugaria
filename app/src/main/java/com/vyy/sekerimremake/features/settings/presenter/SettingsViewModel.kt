package com.vyy.sekerimremake.features.settings.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.settings.domain.model.UserModel
import com.vyy.sekerimremake.features.settings.domain.repository.RequestMonitorResponse
import com.vyy.sekerimremake.features.settings.domain.use_cases.SettingsUseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel  @Inject constructor(
    private val settingUserCases: SettingsUseCases
) : ViewModel() {
    private val _requestMonitorResponse = MutableSharedFlow<RequestMonitorResponse>()
    val requestMonitorResponse = _requestMonitorResponse.asSharedFlow()

    fun requestMonitor(user: UserModel) = viewModelScope.launch(Dispatchers.IO) {
        _requestMonitorResponse.emit (Response.Loading)
        _requestMonitorResponse.emit ( settingUserCases.requestMonitorUseCase(user) )
    }
}