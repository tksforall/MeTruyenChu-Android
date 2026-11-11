package com.example.metruyenchu.user_interface.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.metruyenchu.user_interface.notification.components.NotificationDivider
import com.example.metruyenchu.user_interface.notification.components.NotificationItem


// ==========================================================
// NOTIFICATION SCREEN
// ==========================================================

@Composable
fun NotificationScreen(
    onBack: () -> Unit = {}
) {

    // ======================================================
    // TRẠNG THÁI MENU SẮP XẾP
    // ======================================================

    // false = menu đang đóng
    // true  = menu đang mở
    var sortMenuExpanded by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // THỨ TỰ HIỆN TẠI
    // ======================================================

    // Mặc định là "Mới nhất".
    var selectedSort by remember {
        mutableStateOf("Mới nhất")
    }


    // ======================================================
    // DỮ LIỆU SKELETON
    // ======================================================

    // Tạo 3 thông báo giả để xem UI.
    val notifications = listOf(

        // --------------------------------------------------
        // THÔNG BÁO CHƯƠNG MỚI
        // --------------------------------------------------

        Notification(
            id = "1",

            type = NotificationType.NEW_CHAPTER,

            title = "Tên Truyện",

            content = "Chương 125 đã được cập nhật.",

            time = "10 phút trước",

            bookId = "book_001",

            chapterId = "chapter_125",

            isRead = false
        ),


        // --------------------------------------------------
        // THÔNG BÁO BÌNH LUẬN
        // --------------------------------------------------

        Notification(
            id = "2",

            type = NotificationType.COMMENT_REPLY,

            title = "Tên Truyện",

            content = "Bình luận: aaa bbb ccc\nCó người đã trả lời bình luận của bạn.",

            time = "1 giờ trước",

            bookId = "book_001",

            commentId = "comment_001",

            isRead = false
        ),


        // --------------------------------------------------
        // CHƯƠNG MỚI
        // --------------------------------------------------

        Notification(
            id = "3",

            type = NotificationType.NEW_CHAPTER,

            title = "Tên Truyện",

            content = "Chương 89 đã được cập nhật.",

            time = "Hôm qua",

            bookId = "book_002",

            chapterId = "chapter_089",

            isRead = true
        )
    )


    // ======================================================
    // TOÀN BỘ SCREEN
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
            .statusBarsPadding()
    ) {


        // ==================================================
        // HEADER
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {

            // ----------------------------------------------
            // NÚT QUAY LẠI
            // ----------------------------------------------

            IconButton(
                onClick = onBack,
                modifier = Modifier.align(
                    Alignment.CenterStart
                )
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,

                    contentDescription = "Quay lại",

                    tint = Color.White,

                    modifier = Modifier.size(30.dp)
                )
            }


            // ----------------------------------------------
            // TIÊU ĐỀ
            // ----------------------------------------------

            Text(
                text = "Thông Báo",

                color = Color.White,

                fontSize = 23.sp,

                fontWeight = FontWeight.Medium,

                modifier = Modifier.align(
                    Alignment.Center
                )
            )


            // ----------------------------------------------
            // NÚT SẮP XẾP
            // ----------------------------------------------

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            Color(0xFF3A3A3A)
                        )
                        .clickable {
                            sortMenuExpanded = true
                        }
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Tên kiểu sắp xếp hiện tại
                    Text(
                        text = selectedSort,

                        color = Color.White,

                        fontSize = 12.sp
                    )


                    // Mũi tên xuống
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,

                        contentDescription = "Sắp xếp",

                        tint = Color.White,

                        modifier = Modifier.size(20.dp)
                    )
                }


                // ==========================================
                // MENU SẮP XẾP
                // ==========================================

                DropdownMenu(
                    expanded = sortMenuExpanded,

                    onDismissRequest = {
                        sortMenuExpanded = false
                    }
                ) {

                    // Mới nhất
                    DropdownMenuItem(
                        text = {
                            Text("Mới nhất")
                        },

                        onClick = {
                            selectedSort = "Mới nhất"
                            sortMenuExpanded = false
                        }
                    )


                    // Cũ nhất
                    DropdownMenuItem(
                        text = {
                            Text("Cũ nhất")
                        },

                        onClick = {
                            selectedSort = "Cũ nhất"
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }


        // ==================================================
        // ĐƯỜNG PHÂN CÁCH HEADER
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color(0xFF3A3A3A)
                )
        )


        // ==================================================
        // DANH SÁCH THÔNG BÁO
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // Duyệt qua từng notification
            notifications.forEachIndexed { index, notification ->

                NotificationItem(
                    notification = notification,

                    onClick = {

                        // ==============================
                        // TODO: NAVIGATION
                        // ==============================

                        // Nếu là NEW_CHAPTER:
                        // → mở đúng chapter.

                        // Nếu là COMMENT_REPLY:
                        // → mở đúng comment.

                        // Phần này làm sau.
                    }
                )


                // Không tạo đường kẻ sau item cuối.
                if (index < notifications.lastIndex) {

                    NotificationDivider()
                }
            }
        }
    }
}