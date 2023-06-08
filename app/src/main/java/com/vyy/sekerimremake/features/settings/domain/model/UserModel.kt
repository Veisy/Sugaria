package com.vyy.sekerimremake.features.settings.domain.model

data class UserModel(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var userName: String? = null,
    var blockList: List<HashMap<String, String>>? = null,
    var monitoreds: List<HashMap<String, String>>? = null,
    var monitors: List<HashMap<String, String>>? = null,
    var waiting_monitors: List<HashMap<String, String>>? = null
)
