package com.example.metruyenchu.api

data class CommentResponse(
    val commentId: Long,
    val userId: Long,
    val userName: String? = null,
    val bookId: Long,
    val parentCommentId: Long?,
    val chapterId: Long?,
    val content: String,
    val likeCount: Long,
    val replyCount: Long,
    val createdAt: String,
    val updatedAt: String
)
