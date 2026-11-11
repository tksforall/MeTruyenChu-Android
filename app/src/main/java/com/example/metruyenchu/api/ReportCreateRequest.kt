package com.example.metruyenchu.api

data class ReportCreateRequest(
    val commentId: Long? = null,
    val reviewId: Long? = null,
    val chapterId: Long? = null,
    val reason: String,
    val status: String = "PENDING"
)