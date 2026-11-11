package com.example.metruyenchu.user_interface.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ACCOUNT SCREEN - CHƯA ĐĂNG NHẬP
// ==========================================================

@Composable
fun AccountGuestScreen(
    onAppSettingsClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {

    // ======================================================
    // MÀU SẮC
    // ======================================================

    val backgroundColor = Color(0xFF292929)
    val textColor = Color.White
    val arrowColor = Color(0xFF999999)


    // ======================================================
    // ROOT
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // ==================================================
        // TIÊU ĐỀ
        // ==================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF292929))
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Text(
                text = "Tài Khoản",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1F1F)) // màu header
                    .padding(
                        start = 18.dp,
                        top = 22.dp,
                        bottom = 20.dp
                    )
            )


            // ==================================================
            // PHẦN BÊN DƯỚI
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF292929))
            ) {

                // Cấu hình ứng dụng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAppSettingsClick()
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Cấu hình ứng dụng",
                        color = Color.White,
                        fontSize = 17.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Cấu hình ứng dụng",
                        tint = Color(0xFF999999)
                    )
                }

                // Đường kẻ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF555555))
                )


                // Đăng nhập
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onLoginClick()
                        }
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Đăng nhập",
                        color = Color.White,
                        fontSize = 17.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Đăng nhập",
                        tint = Color(0xFF999999)
                    )
                }

                // Đường kẻ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF555555))
                )
            }
        }
    }
}
