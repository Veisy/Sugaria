package com.vyy.sekerimremake.features.settings.domain.model

data class ContactsModel(
    var ignored: List<String>? = null,
    var monitoreds: List<String>? = null,
    var monitors: List<String>? = null
)