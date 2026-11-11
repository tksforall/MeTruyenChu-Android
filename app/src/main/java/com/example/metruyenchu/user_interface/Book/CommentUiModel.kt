package com.example.metruyenchu.user_interface.Book

data class CommentUiModel(
    val commentId: Long,
    val userId: Long,

    // User
    val userName: String,
    val avatarUrl: String?,

    // Comment
    val content: String,

    // Chapter
    val chapterId: Long?,
    val chapterNumber: Int?,
    val chapterTitle: String?,

    // Statistics
    val likeCount: Long,
    val replyCount: Long,

    // Current user
    val isLiked: Boolean,

    // Time
    val createdAt: String
)