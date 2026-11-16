package com.example.metruyenchu.api

data class LoginResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val userId: Long,
    val userName: String,
    val email: String,
    val avatarUrl: String?,
    val recommendationCount: Long,
    val status: String,
    val authProvider: String,
    val emailVerified: Int,
    val phone: String?,
    val gender: String?
)

data class ForgotPasswordResponse(
    val message: String,
    val otp: String
)

data class ResetPasswordResponse(
    val message: String
)