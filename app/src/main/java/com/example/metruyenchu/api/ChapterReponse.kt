package com.example.metruyenchu.api

data class ChapterResponse(
    val chapterId: Long,
    val bookId: Long,
    val chapterNumber: Int,
    val title: String?,
    val content: String?,
    val viewCount: Long?,
    val createdAt: String?,
    val updatedAt: String?
)
