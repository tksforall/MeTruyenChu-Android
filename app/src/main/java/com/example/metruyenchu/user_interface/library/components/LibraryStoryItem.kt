package com.example.metruyenchu.user_interface.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import coil3.compose.AsyncImage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ITEM DÙNG CHUNG CHO LỊCH SỬ + ĐÁNH DẤU
// ==========================================================

@Composable
fun LibraryStoryItem(
    title: String = "Tên truyện",
    imageUrl: String? = null,
    progressText: String? = null,

    // Có hiển thị chuông hay không
    showNotification: Boolean = false,

    // Trạng thái chuông
    notificationEnabled: Boolean = false,

    onItemClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {

    // ======================================================
    // KÍCH THƯỚC
    // ======================================================

    // ----------------------------------------------
    // KHUNG ẢNH
    // ----------------------------------------------

    val imageWidth = 64.dp
    val imageHeight = 88.dp

    // Độ bo góc
    val imageCornerRadius = 7.dp


    // ----------------------------------------------
    // CHỮ
    // ----------------------------------------------

    // Tên truyện
    val titleFontSize = 14.sp

    // "Đã đọc"
    val progressFontSize = 12.sp


    // ----------------------------------------------
    // KHOẢNG CÁCH
    // ----------------------------------------------

    // Ảnh -> chữ
    val imageToTextSpace = 10.dp

    // Tên truyện -> Đã đọc
    val titleToProgressSpace = 4.dp


    // ======================================================
    // ITEM
    // ======================================================

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick()
            }
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),

        verticalAlignment = Alignment.Top
    ) {


        // ==================================================
        // KHUNG ẢNH TRUYỆN
        // ==================================================

        Column(
            modifier = Modifier
                .size(width = imageWidth, height = imageHeight)
                .background(Color(0xFF555555), RoundedCornerShape(imageCornerRadius)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl?.let { url -> if (url.startsWith("[") && "](" in url) url.substringAfter("[").substringBefore("](") else url },
                    contentDescription = title,
                    modifier = Modifier.size(imageWidth, imageHeight)
                )
            } else {
                Text("ẢNH\nTRUYỆN", color = Color(0xFFAAAAAA), fontSize = 10.sp)
            }
        }


        // ==================================================
        // KHOẢNG CÁCH ẢNH -> CHỮ
        // ==================================================

        Spacer(
            modifier = Modifier.width(
                imageToTextSpace
            )
        )


        // ==================================================
        // THÔNG TIN TRUYỆN
        // ==================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 2.dp)
        ) {

            // ----------------------------------------------
            // TÊN TRUYỆN
            // ----------------------------------------------

            Text(
                text = title,

                color = Color.White,

                fontSize = titleFontSize,

                fontWeight = FontWeight.Medium,

                maxLines = 2,

                lineHeight = 20.sp
            )


            // ----------------------------------------------
            // ĐÃ ĐỌC
            // ----------------------------------------------

            if (progressText != null) {

                Spacer(
                    modifier = Modifier.height(
                        titleToProgressSpace
                    )
                )

                Text(
                    text = progressText,

                    color = Color(0xFF999999),

                    fontSize = progressFontSize
                )
            }
        }


        // ==================================================
        // CHUÔNG + 3 CHẤM
        // ==================================================

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            // ==================================================
            // CHUÔNG
            // ==================================================

            if (showNotification) {

                IconButton(
                    onClick = onNotificationClick,

                    modifier = Modifier.size(32.dp)
                ) {

                    Icon(
                        imageVector =
                            if (notificationEnabled) {

                                Icons.Default.Notifications

                            } else {

                                Icons.Default.NotificationsOff
                            },

                        contentDescription =
                            if (notificationEnabled) {

                                "Tắt thông báo"

                            } else {

                                "Bật thông báo"
                            },

                        tint =
                            if (notificationEnabled) {

                                Color.White

                            } else {

                                Color(0xFF777777)
                            },

                        modifier = Modifier.size(20.dp)
                    )
                }
            }


            // ==================================================
            // 3 CHẤM
            // ==================================================

            IconButton(
                onClick = onMenuClick,

                modifier = Modifier.size(32.dp)
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MoreVert,

                    contentDescription = "Menu",

                    tint = Color(0xFF999999),

                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}