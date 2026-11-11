package com.example.metruyenchu.api

data class ReportResponse(
    val reportId: Long?,
    val userId: Long?,
    val commentId: Long?,
    val reviewId: Long?,
    val chapterId: Long?,
    val reason: String?,
    val status: String?,
    val createdAt: String?
)