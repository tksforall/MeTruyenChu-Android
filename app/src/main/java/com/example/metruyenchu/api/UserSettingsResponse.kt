package com.example.metruyenchu.api

import java.math.BigDecimal

data class UserSettingsResponse(
    val userId: Long?,
    val backgroundColor: String?,
    val nightBackgroundColor: String?,
    val nightMode: String?,
    val fontFamily: String?,
    val fontSize: BigDecimal?,
    val audioSource: String?,
    val voice: String?,
    val speechSpeed: BigDecimal?,
    val pitch: BigDecimal?
)