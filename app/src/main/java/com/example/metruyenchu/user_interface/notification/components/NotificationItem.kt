package com.example.metruyenchu.user_interface.notification.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.user_interface.notification.Notification
import com.example.metruyenchu.user_interface.notification.NotificationType
import kotlin.math.roundToInt

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val notificationIcon = when (notification.type) {
        NotificationType.NEW_CHAPTER,
        NotificationType.BOOK_UPDATE -> Icons.Default.Book

        NotificationType.COMMENT_REPLY -> Icons.Default.Chat

        NotificationType.SYSTEM -> Icons.Default.Notifications
    }

    // Đã đọc -> nền màu xám; Chưa đọc -> sáng hơn một chút
    val backgroundColor = if (notification.isRead) {
        Color(0xFF424242)
    } else {
        Color(0xFF333333)
    }

    val displayContent = if (notification.type == NotificationType.COMMENT_REPLY) {
        "Có người đã trả lời bình luận của bạn."
    } else {
        notification.content
    }

    // Chiều rộng vùng nút thùng rác bên phải (72.dp)
    val density = LocalDensity.current
    val actionWidthPx = with(density) { 72.dp.toPx() }
    var rawOffsetX by remember(notification.id) { mutableFloatStateOf(0f) }

    // Reset về 0 nếu chưa đọc
    LaunchedEffect(notification.isRead) {
        if (!notification.isRead) rawOffsetX = 0f
    }

    val animatedOffsetX by animateFloatAsState(
        targetValue = rawOffsetX,
        label = "swipe_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // ==================================================
        // LỚP DƯỚI: NÚT THÙNG RÁC BÊN PHẢI (CHỈ KHI ĐÃ ĐỌC)
        // ==================================================
        if (notification.isRead) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(72.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFD32F2F)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        rawOffsetX = 0f
                        onDelete()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa thông báo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // ==================================================
        // LỚP TRÊN: NỘI DUNG THÔNG BÁO (CÓ THỂ KÉO SANG TRÁI)
        // ==================================================
        Row(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .fillMaxWidth()
                .background(backgroundColor)
                .then(
                    if (notification.isRead) {
                        Modifier.pointerInput(notification.id) {
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    rawOffsetX = (rawOffsetX + dragAmount).coerceIn(-actionWidthPx, 0f)
                                },
                                onDragEnd = {
                                    // Kéo quá 40% chiều rộng nút xóa thì neo đứng yên, ngược lại đóng về 0
                                    rawOffsetX = if (rawOffsetX < -actionWidthPx * 0.4f) {
                                        -actionWidthPx
                                    } else {
                                        0f
                                    }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }
                )
                .clickable {
                    if (rawOffsetX < 0f) {
                        // Đang mở nút xóa thì bấm vào item sẽ đóng lại
                        rawOffsetX = 0f
                    } else {
                        onClick()
                    }
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = notificationIcon,
                contentDescription = null,
                tint = if (notification.isRead) Color(0xFF777777) else Color(0xFF1682C4),
                modifier = Modifier.size(25.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    color = if (notification.isRead) Color(0xFFBBBBBB) else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayContent,
                    color = Color(0xFFCCCCCC),
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.time,
                    color = Color(0xFF777777),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun NotificationDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF3A3A3A))
    )
}