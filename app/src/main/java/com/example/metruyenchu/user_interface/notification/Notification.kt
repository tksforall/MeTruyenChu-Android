package com.example.metruyenchu.user_interface.notification

//File này chỉ chịu trách nhiệm định nghĩa dữ liệu của một thông báo.

// ==========================================================
// MODEL: NOTIFICATION
// ==========================================================

// Data class dùng để lưu thông tin của một thông báo.
data class Notification(

    // ID duy nhất của thông báo.
    val id: String,

    // Loại thông báo.
    val type: NotificationType,

    // Tiêu đề của thông báo.
    // Ví dụ: "Tên Truyện"
    val title: String,

    // Nội dung thông báo.
    // Ví dụ: "Chương 125 đã được cập nhật."
    val content: String,

    // Thời gian hiển thị.
    // UI Skeleton đang dùng dữ liệu giả.
    val time: String,

    // ID truyện.
    // Có giá trị khi thông báo liên quan tới truyện.
    // Sau này dùng để Navigation tới đúng truyện.
    val bookId: String? = null,

    // ID chương.
    // Dùng cho thông báo có chương mới.
    val chapterId: String? = null,

    // ID bình luận.
    // Dùng cho thông báo liên quan tới bình luận.
    val commentId: String? = null,

    // true  = đã đọc
    // false = chưa đọc
    val isRead: Boolean = false
)


// ==========================================================
// LOẠI THÔNG BÁO
// ==========================================================

// Enum dùng để xác định thông báo thuộc loại nào.
enum class NotificationType {

    // Có chương mới.
    NEW_CHAPTER,

    // Có người trả lời bình luận.
    COMMENT_REPLY,

    // Thông báo từ hệ thống.
    SYSTEM
}