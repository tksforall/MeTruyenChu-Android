package com.example.metruyenchu.api

data class NotificationResponse(
    val notificationId: Long,
    val userId: Long,
    val bookId: Long? = null,
    val type: String,
    val title: String,
    val content: String,
    val isRead: Int,
    val createdAt: String? = null
)
