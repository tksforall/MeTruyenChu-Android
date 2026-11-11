package com.example.metruyenchu.api

data class BookFollowResponse(
    val userId: Long,
    val bookId: Long,
    val createdAt: String
)