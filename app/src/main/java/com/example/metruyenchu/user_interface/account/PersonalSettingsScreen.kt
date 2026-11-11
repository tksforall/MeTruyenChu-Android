package com.example.metruyenchu.user_interface.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun PersonalSettingsScreen(
    onBack: () -> Unit = {},
    onSortLibraryClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {},
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)  }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {

        // =========================
        // HEADER
        // =========================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {

            // Nút quay lại
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Tiêu đề chính giữa
            Text(
                text = "Cài đặt cá nhân",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // =========================
        // DANH SÁCH CÀI ĐẶT
        // =========================

        PersonalSettingItem(
            title = "Sắp xếp tủ truyện",
            onClick = onSortLibraryClick
        )

        PersonalSettingItem(
            title = "Đổi mật khẩu",
            onClick = onChangePasswordClick
        )

        PersonalSettingItem(
            title = "Đổi email",
            onClick = onChangeEmailClick
        )

        PersonalSettingItem(
            title = "Xóa tài khoản",
            onClick = {
                showDeleteDialog = true
            }
        )
        if (showDeleteDialog) {

            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                },

                title = {
                    Text(
                        text = "Cảnh báo"
                    )
                },

                text = {
                    Text(
                        text = "Bạn có chắc chắn muốn xóa tài khoản không?"
                    )
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false

                            // Sau này xử lý xóa tài khoản thật ở đây
                        }
                    ) {
                        Text(
                            text = "Đồng ý"
                        )
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
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
}

// ==========================================================
// MỘT DÒNG CÀI ĐẶT
// ==========================================================

@Composable
private fun PersonalSettingItem(
    title: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Tên chức năng
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )

        // Mũi tên bên phải
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
    }

    // Đường phân cách
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF3D3D3D))
    )
}