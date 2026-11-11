package com.example.metruyenchu.user_interface.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// BOTTOM SHEET CỦA TRUYỆN
// ==========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryStoryBottomSheet(
    title: String,

    onDismiss: () -> Unit = {},

    onDownloadClick: (fromChapter: String, toChapter: String) -> Unit = { _, _ -> },

    onDeleteClick: () -> Unit = {}
) {

    // ======================================================
    // TRẠNG THÁI DIALOG TẢI TRUYỆN
    // ======================================================

    var showDownloadDialog by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // HIỆN BOTTOM SHEET
    // ======================================================

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF292929)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        ) {

            // ==================================================
            // TÊN TRUYỆN
            // ==================================================

            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 18.dp
                )
            )


            Divider(
                color = Color(0xFF444444)
            )


            // ==================================================
            // TẢI TRUYỆN
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        // Mở Dialog
                        showDownloadDialog = true

                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 18.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = "Tải truyện",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Text(
                    text = "Tải truyện",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }


            Divider(
                color = Color(0xFF444444)
            )


            // ==================================================
            // XÓA TRUYỆN
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {

                        onDeleteClick()

                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 18.dp
                    ),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )

                Spacer(
                    modifier = Modifier.width(16.dp)
                )

                Text(
                    text = "Xóa khỏi Tủ Truyện",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }


    // ======================================================
    // DIALOG TẢI TRUYỆN
    // ======================================================

    if (showDownloadDialog) {

        DownloadChapterDialog(

            onDismiss = {
                showDownloadDialog = false
            },

            onConfirm = { fromChapter, toChapter ->

                // Gửi thông tin ra ngoài
                onDownloadClick(
                    fromChapter,
                    toChapter
                )

                // Đóng dialog
                showDownloadDialog = false

                // Đồng thời đóng BottomSheet
                onDismiss()
            }
        )
    }
}


// ==========================================================
// DIALOG NHẬP KHOẢNG CHƯƠNG
// ==========================================================

@Composable
private fun DownloadChapterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {

    // ======================================================
    // GIÁ TRỊ NHẬP
    // ======================================================

    var fromChapter by remember {
        mutableStateOf("")
    }

    var toChapter by remember {
        mutableStateOf("")
    }


    AlertDialog(

        onDismissRequest = onDismiss,

        // ==================================================
        // TIÊU ĐỀ
        // ==================================================

        title = {

            Text(
                text = "Bạn muốn tải",
                color = Color.White,
                fontSize = 22.sp
            )
        },


        // ==================================================
        // NỘI DUNG
        // ==================================================

        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // ------------------------------------------
                // TỪ CHƯƠNG
                // ------------------------------------------

                Text(
                    text = "Từ chương",
                    color = Color(0xFFBDBDBD),
                    fontSize = 15.sp
                )

                OutlinedTextField(
                    value = fromChapter,

                    onValueChange = {
                        fromChapter = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    colors = TextFieldDefaults.colors(

                        focusedContainerColor =
                            Color.Transparent,

                        unfocusedContainerColor =
                            Color.Transparent,

                        focusedIndicatorColor =
                            Color.Transparent,

                        unfocusedIndicatorColor =
                            Color.Transparent,

                        focusedTextColor =
                            Color.White,

                        unfocusedTextColor =
                            Color.White,

                        cursorColor =
                            Color.White
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF222222))
                )


                Spacer(
                    modifier = Modifier.height(22.dp)
                )


                // ------------------------------------------
                // ĐẾN CHƯƠNG
                // ------------------------------------------

                Text(
                    text = "Đến chương",
                    color = Color(0xFFBDBDBD),
                    fontSize = 15.sp
                )

                OutlinedTextField(
                    value = toChapter,

                    onValueChange = {
                        toChapter = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    singleLine = true,

                    colors = TextFieldDefaults.colors(

                        focusedContainerColor =
                            Color.Transparent,

                        unfocusedContainerColor =
                            Color.Transparent,

                        focusedIndicatorColor =
                            Color.Transparent,

                        unfocusedIndicatorColor =
                            Color.Transparent,

                        focusedTextColor =
                            Color.White,

                        unfocusedTextColor =
                            Color.White,

                        cursorColor =
                            Color.White
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


        // ==================================================
        // NÚT HỦY
        // ==================================================

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Hủy",
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        },


        // ==================================================
        // NÚT ĐỒNG Ý
        // ==================================================

        confirmButton = {

            TextButton(
                onClick = {

                    // Chỉ gửi khi có dữ liệu
                    if (
                        fromChapter.isNotBlank() &&
                        toChapter.isNotBlank()
                    ) {

                        onConfirm(
                            fromChapter,
                            toChapter
                        )
                    }
                }
            ) {

                Text(
                    text = "Đồng ý",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        },

        containerColor = Color(0xFF4A4A4A)
    )
}