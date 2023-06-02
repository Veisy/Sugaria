package com.vyy.sekerimremake.features.settings.domain.repository

import com.vyy.sekerimremake.features.settings.domain.model.ContactsModel
import com.vyy.sekerimremake.features.settings.domain.model.UserInfoModel
import com.vyy.sekerimremake.utils.Response
import kotlinx.coroutines.flow.Flow

typealias GetContactsResponse = Response<ContactsModel?>
typealias UserInfoResponse = Response<UserInfoModel?>

interface SettingsRepository {
    fun getUserInfo(): Flow<UserInfoResponse>
    fun getContacts(): Flow<GetContactsResponse>
}