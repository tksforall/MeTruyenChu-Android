package com.example.metruyenchu.api

data class CommentCreateRequest(
    val userId: Long,
    val bookId: Long,
    val parentCommentId: Long? = null,
    val chapterId: Long? = null,
    val content: String
)