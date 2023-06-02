package com.vyy.sekerimremake.features.settings.domain.model

data class UserModel(
    var email: String? = null,
    var name: String? = null,
    var userId: String? = null,
    var ignored: List<String>? = null,
    var monitoreds: List<HashMap<String, String>>? = null,
    var monitors: List<HashMap<String, String>>? = null
)
