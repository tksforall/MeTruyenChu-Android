package com.example.metruyenchu.api

data class ChangeEmailRequest(
    val currentPassword: String,
    val newEmail: String
)