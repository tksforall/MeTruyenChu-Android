package com.example.metruyenchu.user_interface.account

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// ACCOUNT SCREEN
// ==========================================================

@Composable
fun AccountScreen(
    userName: String,
    onAccountInfoClick: () -> Unit = {},
    onPersonalSettingsClick: () -> Unit = {},
    onAppSettingsClick: () -> Unit = {},
    onClearCacheClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    // ==================================================
    // TRẠNG THÁI DIALOG
    // ==================================================

    // Dialog Xóa cache
    var showClearCacheDialog by remember {
        mutableStateOf(false)
    }

    // Dialog Đăng xuất
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    // ==================================================
    // GIAO DIỆN
    // ==================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {


        // ==================================================
        // THÔNG TIN NGƯỜI DÙNG
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {


            // ==================================================
            // AVATAR
            // ==================================================

            Icon(
                imageVector = Icons.Default.AccountCircle,

                contentDescription = "Avatar",

                tint = Color(0xFFBDBDBD),

                modifier = Modifier.size(43.dp)
            )


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            // ==================================================
            // TÊN NGƯỜI DÙNG
            // ==================================================

            Text(
                text = userName,

                color = Color.White,

                fontSize = 18.sp,

                fontWeight = FontWeight.Medium,

                modifier = Modifier.weight(1f)
            )
        }


        // ==================================================
        // MENU
        // ==================================================

        ProfileMenuItem(
            title = "Thông tin tài khoản",

            onClick = onAccountInfoClick
        )


        ProfileMenuItem(
            title = "Cài đặt cá nhân",

            onClick = onPersonalSettingsClick
        )


        ProfileMenuItem(
            title = "Cấu hình ứng dụng",

            onClick = onAppSettingsClick
        )


        // ==================================================
        // XÓA CACHE
        // ==================================================

        ProfileMenuItem(
            title = "Xóa cache",

            onClick = {
                showClearCacheDialog = true
            }
        )


        // ==================================================
        // KHOẢNG CÁCH
        // ==================================================

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        // ==================================================
        // THOÁT
        // ==================================================

        ProfileMenuItem(
            title = "Thoát",

            onClick = {
                showLogoutDialog = true
            }
        )
    }


    // ==========================================================
    // DIALOG XÓA CACHE
    // ==========================================================

    if (showClearCacheDialog) {

        AlertDialog(

            onDismissRequest = {
                showClearCacheDialog = false
            },

            title = {

                Text(
                    text = "Cảnh báo"
                )
            },

            text = {

                Column {

                    Text(
                        text = "Bạn có chắc chắn xóa cache?"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "Hành động này sẽ xóa tất cả truyện bạn đã tải."
                    )
                }
            },

            // ==================================================
            // ĐỒNG Ý
            // ==================================================

            confirmButton = {
                TextButton(
                    onClick = {

                        // Xóa cache thật
                        onClearCacheClick()

                        // Đóng dialog
                        showClearCacheDialog = false
                    }
                ) {
                    Text(
                        text = "Đồng Ý",
                        color = Color.Black
                    )
                }
            },

            // ==================================================
            // HỦY
            // ==================================================

            dismissButton = {

                TextButton(
                    onClick = {

                        showClearCacheDialog = false
                    }
                ) {

                    Text(
                        text = "Hủy",

                        color = Color.Red
                    )
                }
            },

            containerColor = Color(0xFF4A4A4A),

            titleContentColor = Color.White,

            textContentColor = Color.White
        )
    }


    // ==========================================================
    // DIALOG ĐĂNG XUẤT
    // ==========================================================

    if (showLogoutDialog) {

        AlertDialog(

            onDismissRequest = {

                showLogoutDialog = false
            },

            title = {

                Text(
                    text = "Cảnh báo"
                )
            },

            text = {

                Text(
                    text = "Bạn có chắc chắn thoát?"
                )
            },

            // ==================================================
            // ĐỒNG Ý
            // ==================================================

            confirmButton = {
                TextButton(
                    onClick = {
                        onLogoutClick()
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "Đồng Ý",
                        color = Color.Black
                    )
                }
            },

            // ==================================================
            // HỦY
            // ==================================================

            dismissButton = {

                TextButton(
                    onClick = {

                        showLogoutDialog = false
                    }
                ) {

                    Text(
                        text = "Hủy",

                        color = Color.Red
                    )
                }
            },

            containerColor = Color(0xFF4A4A4A),

            titleContentColor = Color.White,

            textContentColor = Color.White
        )
    }
}


// ==========================================================
// MỘT DÒNG MENU
// ==========================================================

@Composable
private fun ProfileMenuItem(
    title: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 24.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==================================================
        // TÊN
        // ==================================================

        Text(
            text = title,

            color = Color.White,

            fontSize = 16.sp,

            modifier = Modifier.weight(1f)
        )


        // ==================================================
        // MŨI TÊN
        // ==================================================

        Icon(
            imageVector = Icons.Default.ChevronRight,

            contentDescription = null,

            tint = Color.White,

            modifier = Modifier.size(21.dp)
        )
    }


    // ==================================================
    // ĐƯỜNG PHÂN CÁCH
    // ==================================================

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF3A3A3A))
    )
}

