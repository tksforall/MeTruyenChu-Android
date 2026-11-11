package com.example.metruyenchu.user_interface.notification.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.user_interface.notification.Notification
import com.example.metruyenchu.user_interface.notification.NotificationType



// ==========================================================
// MỘT ITEM THÔNG BÁO
// ==========================================================

@Composable
fun NotificationItem(
    notification: Notification,

    // Callback khi người dùng bấm vào thông báo.
    // Hiện tại Skeleton chưa xử lý Navigation.
    onClick: () -> Unit = {}
) {

    // ======================================================
    // XÁC ĐỊNH ICON THEO LOẠI THÔNG BÁO
    // ======================================================

    val notificationIcon = when (notification.type) {

        // Thông báo chương mới.
        NotificationType.NEW_CHAPTER -> {
            Icons.Default.Book
        }

        // Thông báo trả lời bình luận.
        NotificationType.COMMENT_REPLY -> {
            Icons.Default.Chat
        }

        // Thông báo hệ thống.
        NotificationType.SYSTEM -> {
            Icons.Default.Notifications
        }
    }


    // ======================================================
    // MÀU NỀN THÔNG BÁO
    // ======================================================

    // Thông báo chưa đọc sẽ sáng hơn một chút.
    val backgroundColor = if (notification.isRead) {
        Color.Transparent
    } else {
        Color(0xFF333333)
    }


    // ======================================================
    // ITEM
    // ======================================================

    Row(
        modifier = Modifier
            .fillMaxWidth()

            // Nền của notification.
            .background(backgroundColor)

            // Cho phép bấm vào notification.
            .clickable {
                onClick()
            }

            // Khoảng cách bên trong item.
            .padding(
                horizontal = 12.dp,
                vertical = 12.dp
            ),

        verticalAlignment = Alignment.Top
    ) {

        // ==================================================
        // ICON
        // ==================================================

        Icon(
            imageVector = notificationIcon,
            contentDescription = null,

            tint = if (notification.isRead) {
                Color(0xFF777777)
            } else {
                Color(0xFF1682C4)
            },

            modifier = Modifier.size(25.dp)
        )


        Spacer(
            modifier = Modifier.width(12.dp)
        )


        // ==================================================
        // NỘI DUNG
        // ==================================================

        Column(
            modifier = Modifier.weight(1f)
        ) {

            // ----------------------------------------------
            // TIÊU ĐỀ
            // ----------------------------------------------

            Text(
                text = notification.title,

                color = if (notification.isRead) {
                    Color(0xFFBBBBBB)
                } else {
                    Color.White
                },

                fontSize = 16.sp,

                fontWeight = FontWeight.Medium,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // ----------------------------------------------
            // NỘI DUNG
            // ----------------------------------------------

            Text(
                text = notification.content,

                color = Color(0xFFCCCCCC),

                fontSize = 14.sp,

                lineHeight = 19.sp,

                maxLines = 2,

                overflow = TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // ----------------------------------------------
            // THỜI GIAN
            // ----------------------------------------------

            Text(
                text = notification.time,

                color = Color(0xFF777777),

                fontSize = 13.sp
            )
        }
    }
}


// ==========================================================
// ĐƯỜNG PHÂN CÁCH
// ==========================================================

@Composable
fun NotificationDivider() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Color(0xFF3A3A3A)
            )
    )
}