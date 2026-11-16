package com.example.metruyenchu.user_interface.notification

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val time: String,
    val bookId: String? = null,
    val chapterId: String? = null,
    val commentId: String? = null,
    val isRead: Boolean = false
)

enum class NotificationType {
    NEW_CHAPTER,
    BOOK_UPDATE,
    COMMENT_REPLY,
    SYSTEM
}