package com.example.metruyenchu.api

data class ReviewCreateRequest(
    val bookId: Long,
    val parentReviewId: Long? = null,
    val chapterId: Long? = null,
    val rating: Double? = null,
    val content: String
)