package com.vyy.sekerimremake.features.monitoreds.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vyy.sekerimremake.features.settings.domain.repository.GetContactsResponse
import com.vyy.sekerimremake.features.settings.domain.use_cases.SettingsUseCases
import com.vyy.sekerimremake.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitoredsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {
    private val _getContactsResponse = MutableStateFlow<GetContactsResponse>(Response.Loading)
    val getContactsResponse = _getContactsResponse.asSharedFlow()
    private var getMonitoredsJob: Job? = null

    init {
        getMonitoreds()
    }

    //TODO: Inject Dispatchers
    private fun getMonitoreds() {
        getMonitoredsJob?.cancel()
        getMonitoredsJob = viewModelScope.launch(Dispatchers.IO) {
            settingsUseCases.getContactUseCase().collectLatest { response ->
                _getContactsResponse.update { response }
            }
        }
    }
}