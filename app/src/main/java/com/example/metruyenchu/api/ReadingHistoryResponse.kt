package com.example.metruyenchu.api

data class ReadingHistoryResponse(
    val userId: Long,
    val bookId: Long,
    val chapterId: Long,
    val lastReadAt: String,
    val progress: Double
)