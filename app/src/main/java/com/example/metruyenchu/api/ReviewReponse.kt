package com.example.metruyenchu.api

data class ReviewResponse(
    val reviewId: Long,
    val userId: Long,
    val userName: String?,
    val bookId: Long,
    val parentReviewId: Long?,
    val chapterId: Long?,
    val rating: Double?,
    val content: String,
    val likeCount: Long,
    val replyCount: Long,
    val createdAt: String,
    val updatedAt: String
)

