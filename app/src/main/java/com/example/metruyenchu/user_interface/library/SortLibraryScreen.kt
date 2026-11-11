package com.example.metruyenchu.user_interface.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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


// ==========================================================
// MÀN HÌNH SẮP XẾP TỦ TRUYỆN
// ==========================================================

@Composable
fun SortLibraryScreen(
    onBack: () -> Unit = {}
) {

    // ======================================================
    // TRẠNG THÁI LỰA CHỌN
    // ======================================================

    // -------------------------
    // Lịch sử
    // -------------------------

    var historySort by remember {
        mutableStateOf("Mới đọc")
    }


    // -------------------------
    // Đánh dấu
    // -------------------------

    var bookmarkSort by remember {
        mutableStateOf("Mới lưu")
    }


    // ======================================================
    // TOÀN BỘ SCREEN
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
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
                    tint = Color.White
                )
            }


            // ----------------------------------------------
            // TIÊU ĐỀ
            // ----------------------------------------------

            Text(
                text = "Sắp xếp tủ truyện",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }


        // ==================================================
        // ĐƯỜNG PHÂN CÁCH
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
        // DÒNG LỊCH SỬ
        // ==================================================

        SortLibraryRow(
            title = "Lịch sử",

            options = listOf(
                "Chương mới",
                "Mới đọc",
                "Tên truyện"
            ),

            selectedOption = historySort,

            onSelected = {
                historySort = it
            }
        )


        // ==================================================
        // DÒNG ĐÁNH DẤU
        // ==================================================

        SortLibraryRow(
            title = "Đánh dấu",

            options = listOf(
                "Chương mới",
                "Mới lưu",
                "Tên truyện"
            ),

            selectedOption = bookmarkSort,

            onSelected = {
                bookmarkSort = it
            }
        )
    }
}


// ==========================================================
// MỘT DÒNG SẮP XẾP
// ==========================================================

@Composable
private fun SortLibraryRow(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = Color.White,
            fontSize = 17.sp,
            modifier = Modifier.width(80.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF4A4A4A))
        ) {

            options.forEach { option ->

                val isSelected = option == selectedOption

                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) {
                                Color(0xFFE0E0E0)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable {
                            onSelected(option)
                        }
                        .padding(
                            horizontal = 12.dp,
                            vertical = 9.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = option,
                        color = if (isSelected) {
                            Color(0xFF333333)
                        } else {
                            Color.White
                        },
                        fontSize = 12.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}