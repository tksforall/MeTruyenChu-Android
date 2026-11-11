package com.example.metruyenchu.api

data class RegisterRequest(
    val userName: String,
    val email: String,
    val password: String,
    val avatarUrl: String? = null
)
