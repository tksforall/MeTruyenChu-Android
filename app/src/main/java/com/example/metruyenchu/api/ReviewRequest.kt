package com.example.metruyenchu.api

data class ReviewRequest(
    val bookId: Long,
    val parentReviewId: Long?,
    val rating: Double?,
    val content: String
)