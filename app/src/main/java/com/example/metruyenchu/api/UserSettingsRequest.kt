package com.example.metruyenchu.api

data class UserSettingsRequest(
    val backgroundColor: String? = null,
    val nightBackgroundColor: String? = null,
    val nightMode: String? = null,
    val fontFamily: String? = null,
    val fontSize: Double? = null,
    val audioSource: String? = null,
    val voice: String? = null,
    val speechSpeed: Double? = null,
    val pitch: Double? = null
)