package com.example.metruyenchu.user_interface.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.NotificationResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.user_interface.notification.components.NotificationDivider
import com.example.metruyenchu.user_interface.notification.components.NotificationItem
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    onBack: () -> Unit = {},
    onNavigateToBook: (Long) -> Unit = {},
    onNavigateToComment: (Long) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var sortMenuExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Mới nhất") }

    var rawNotifications by remember { mutableStateOf<List<NotificationResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Tải danh sách thông báo từ API
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val res = RetrofitClient.api.getMyNotifications()
            if (res.isSuccessful) {
                rawNotifications = res.body().orEmpty()
                errorMessage = null
            } else {
                errorMessage = "Không tải được thông báo (${res.code()})"
            }
        } catch (e: Exception) {
            errorMessage = "Lỗi kết nối: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Sắp xếp danh sách theo lựa chọn "Mới nhất" / "Cũ nhất"
    val sortedList = remember(rawNotifications, selectedSort) {
        val sorted = if (selectedSort == "Mới nhất") {
            rawNotifications.sortedByDescending { it.createdAt ?: it.notificationId.toString() }
        } else {
            rawNotifications.sortedBy { it.createdAt ?: it.notificationId.toString() }
        }

        sorted.map { dto ->
            val notiType = when (dto.type.uppercase()) {
                "NEW_CHAPTER" -> NotificationType.NEW_CHAPTER
                "BOOK_UPDATE" -> NotificationType.BOOK_UPDATE
                "COMMENT_REPLY" -> NotificationType.COMMENT_REPLY
                else -> NotificationType.SYSTEM
            }

            Notification(
                id = dto.notificationId.toString(),
                type = notiType,
                title = dto.title,
                content = dto.content,
                time = formatNotificationTime(dto.createdAt),
                bookId = dto.bookId?.toString(),
                isRead = dto.isRead == 1
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {
        // ==================================================
        // HEADER (CHIA ROW RÕ RÀNG KHÔNG ĐÈ CHỮ)
        // ==================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Thông Báo",
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            // NÚT SẮP XẾP
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3A3A3A))
                        .clickable { sortMenuExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedSort,
                        color = Color.White,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Sắp xếp",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier.background(Color(0xFF3A3A3A))
                ) {
                    DropdownMenuItem(
                        text = { Text("Mới nhất", color = Color.White, fontSize = 14.sp) },
                        onClick = {
                            selectedSort = "Mới nhất"
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cũ nhất", color = Color.White, fontSize = 14.sp) },
                        onClick = {
                            selectedSort = "Cũ nhất"
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF3A3A3A))
        )

        // ==================================================
        // DANH SÁCH THÔNG BÁO
        // ==================================================
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                sortedList.isEmpty() -> {
                    Text(
                        text = "Không có thông báo nào",
                        color = Color(0xFFAAAAAA),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(
                            items = sortedList,
                            key = { _, item -> item.id }
                        ) { index, notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    val notiId = notification.id.toLongOrNull() ?: return@NotificationItem

                                    // 1. Đánh dấu đã đọc nếu chưa đọc (đổi nền sang xám)
                                    if (!notification.isRead) {
                                        rawNotifications = rawNotifications.map {
                                            if (it.notificationId == notiId) it.copy(isRead = 1) else it
                                        }
                                        scope.launch {
                                            try {
                                                RetrofitClient.api.markNotificationRead(notiId)
                                            } catch (_: Exception) {}
                                        }
                                    }

                                    // 2. Điều hướng theo loại thông báo
                                    val bId = notification.bookId?.toLongOrNull()
                                    when (notification.type) {
                                        NotificationType.NEW_CHAPTER,
                                        NotificationType.BOOK_UPDATE -> {
                                            if (bId != null) onNavigateToBook(bId)
                                        }
                                        NotificationType.COMMENT_REPLY -> {
                                            if (bId != null) onNavigateToComment(bId)
                                        }
                                        NotificationType.SYSTEM -> {
                                            // Thông báo hệ thống: không làm gì thêm
                                        }
                                    }
                                },
                                onDelete = {
                                    val notiId = notification.id.toLongOrNull() ?: return@NotificationItem
                                    rawNotifications = rawNotifications.filterNot { it.notificationId == notiId }
                                    scope.launch {
                                        try {
                                            RetrofitClient.api.deleteNotification(notiId)
                                        } catch (_: Exception) {}
                                    }
                                }
                            )

                            if (index < sortedList.lastIndex) {
                                NotificationDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatNotificationTime(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return ""
    // Cắt chuỗi ISO "2026-09-17T12:19:27..." thành "17/09/2026 12:19"
    return try {
        val datePart = createdAt.substringBefore("T").split("-")
        val timePart = createdAt.substringAfter("T", "").take(5)
        if (datePart.size == 3) {
            "${datePart[2]}/${datePart[1]}/${datePart[0]} $timePart".trim()
        } else {
            createdAt
        }
    } catch (_: Exception) {
        createdAt
    }
}