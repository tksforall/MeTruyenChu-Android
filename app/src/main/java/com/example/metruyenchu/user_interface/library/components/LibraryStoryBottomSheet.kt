package com.example.metruyenchu.user_interface.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryStoryBottomSheet(
    title: String,
    coverUrl: String? = null,
    showNotificationOption: Boolean = false,
    isNotificationEnabled: Boolean = false,
    onToggleNotification: (Boolean) -> Unit = {},
    onDismiss: () -> Unit = {},
    onDownloadClick: (fromChapter: String, toChapter: String) -> Unit = { _, _ -> },
    onDeleteClick: () -> Unit = {}
) {
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF292929)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 26.dp)
        ) {
            // ==================================================
            // 1. HEADER: ẢNH BÌA + TÊN TRUYỆN (CHỮ BÌNH THƯỜNG)
            // ==================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (!coverUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = Color(0xFF444444))

            // ==================================================
            // 2. TẢI TRUYỆN
            // ==================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDownloadDialog = true }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Tải truyện",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Tải truyện",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                )
            }

            HorizontalDivider(color = Color(0xFF444444))

            // ==================================================
            // 3. XÓA KHỎI TỦ TRUYỆN (BẤM VÀO HIỆN DIALOG XÁC NHẬN)
            // ==================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDeleteConfirmDialog = true }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa khỏi Tủ Truyện",
                    tint = Color(0xFFBDBDBD),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Xóa khỏi Tủ Truyện",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light
                )
            }

            // ==================================================
            // 4. NHẬN THÔNG BÁO (CHỈ HIỆN Ở LỊCH SỬ)
            // ==================================================
            if (showNotificationOption) {
                HorizontalDivider(color = Color(0xFF444444))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleNotification(!isNotificationEnabled) }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isNotificationEnabled) {
                            Icons.Default.Notifications
                        } else {
                            Icons.Default.NotificationsOff
                        },
                        contentDescription = "Nhận thông báo",
                        tint = if (isNotificationEnabled) Color.White else Color(0xFFBDBDBD),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Nhận thông báo",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { onToggleNotification(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF1976D2),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF555555),
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }

    // ======================================================
    // DIALOG XÁC NHẬN XÓA KHỎI TỦ TRUYỆN
    // ======================================================
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = Color(0xFF4A4A4A),
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn xóa khỏi tủ truyện?",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text(
                        text = "Hủy",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text(
                        text = "Đồng ý",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        )
    }

    // ======================================================
    // DIALOG TẢI TRUYỆN (NHỎ GỌN, KHOẢNG CÁCH HẸP, NỀN XÁM)
    // ======================================================
    if (showDownloadDialog) {
        DownloadChapterDialog(
            onDismiss = { showDownloadDialog = false },
            onConfirm = { fromChapter, toChapter ->
                onDownloadClick(fromChapter, toChapter)
                showDownloadDialog = false
                onDismiss()
            }
        )
    }
}

@Composable
private fun DownloadChapterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var fromChapter by remember { mutableStateOf("") }
    var toChapter by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF4A4A4A),
        title = {
            Text(
                text = "Bạn muốn tải",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Từ chương",
                    color = Color(0xFFD0D0D0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )

                OutlinedTextField(
                    value = fromChapter,
                    onValueChange = { fromChapter = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF777777))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Đến chương",
                    color = Color(0xFFD0D0D0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )

                OutlinedTextField(
                    value = toChapter,
                    onValueChange = { toChapter = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF777777))
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Hủy",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fromChapter.isNotBlank() && toChapter.isNotBlank()) {
                        onConfirm(fromChapter, toChapter)
                    }
                }
            ) {
                Text(
                    text = "Đồng ý",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    )
}