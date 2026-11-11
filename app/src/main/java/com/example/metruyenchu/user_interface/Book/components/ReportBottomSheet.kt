package com.example.metruyenchu.user_interface.Book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================================
// REPORT BOTTOM SHEET
// ==========================================================

@Composable
fun ReportBottomSheet(
    username: String = "Username",
    bookName: String = "Book_name",
    contentType: String = "Bình luận",
    onDismiss: () -> Unit = {},
    onSubmit: (reason: String) -> Unit = {}
) {

    // ======================================================
    // MÀU
    // ======================================================

    val sheetColor = Color(0xFF303030)

    val textColor = Color(0xFFF5F5F5)

    val secondaryColor = Color(0xFF8A8A8A)

    val dividerColor = Color(0xFF696969)

    val selectedColor = Color(0xFF9E9E9E)

    val inputColor = Color(0xFF404040)

    val buttonColor = Color.Black


    // ======================================================
    // DANH SÁCH LÝ DO
    // ======================================================

    val reportOptions = listOf(
        "Spam",
        "Công kích cá nhân",
        "Ngôn từ không phù hợp",
        "Quảng cáo trá hình",
        "Vấn đề khác"
    )


    // ======================================================
    // TRẠNG THÁI
    // ======================================================

    var selectedReason by remember {
        mutableStateOf("Spam")
    }

    var customReason by remember {
        mutableStateOf("")
    }


    // ======================================================
    // OVERLAY
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // ==================================================
        // NỀN TỐI
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.45f)
                )
                .clickable {
                    onDismiss()
                }
        )


        // ==================================================
        // BOTTOM SHEET
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .background(
                    color = sheetColor,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
        ) {

            // ==================================================
            // THANH KÉO
            // ==================================================

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(Color(0xFFAAAAAA))
            )


            // ==================================================
            // NỘI DUNG
            // KHÔNG SCROLL
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 34.dp,
                        end = 34.dp,
                        top = 20.dp,
                        bottom = 16.dp
                    )
            ) {

                // ==================================================
                // TIÊU ĐỀ
                // ==================================================

                Text(
                    text = "Bạn cần hỗ trợ hay báo cáo vấn đề gì?",

                    color = textColor,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )


                // ==================================================
                // DANH SÁCH OPTION
                // ==================================================

                reportOptions.forEach { option ->

                    ReportOption(
                        text = option,

                        selected = option == selectedReason,

                        textColor = textColor,

                        secondaryColor = secondaryColor,

                        dividerColor = dividerColor,

                        selectedColor = selectedColor,

                        onClick = {

                            selectedReason = option

                            if (option != "Vấn đề khác") {
                                customReason = ""
                            }
                        }
                    )
                }


                // ==================================================
                // KHOẢNG CÁCH
                // ==================================================

                Spacer(
                    modifier = Modifier.height(18.dp)
                )


                // ==================================================
                // Ô TRÊN
                // ==================================================

                TextField(
                    value =
                        if (selectedReason == "Vấn đề khác") {
                            customReason
                        } else {
                            selectedReason
                        },

                    onValueChange = { value ->

                        if (selectedReason == "Vấn đề khác") {
                            customReason = value
                        }
                    },

                    readOnly =
                        selectedReason != "Vấn đề khác",

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        ),

                    placeholder = {

                        if (selectedReason == "Vấn đề khác") {

                            Text(
                                text = "Nhập vấn đề...",

                                color = secondaryColor,

                                fontSize = 16.sp
                            )
                        }
                    },

                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = textColor,
                        fontSize = 16.sp
                    ),

                    colors = TextFieldDefaults.colors(

                        focusedTextColor = textColor,

                        unfocusedTextColor = textColor,

                        disabledTextColor = textColor,

                        focusedContainerColor = inputColor,

                        unfocusedContainerColor = inputColor,

                        disabledContainerColor = inputColor,

                        focusedIndicatorColor = Color.Transparent,

                        unfocusedIndicatorColor = Color.Transparent,

                        disabledIndicatorColor = Color.Transparent,

                        cursorColor = textColor,

                        focusedPlaceholderColor = secondaryColor,

                        unfocusedPlaceholderColor = secondaryColor
                    )
                )


                // ==================================================
                // KHOẢNG CÁCH
                // ==================================================

                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // ==================================================
                // Ô DƯỚI - KHÔNG CHỈNH SỬA
                // ==================================================

                val previewReason =
                    if (selectedReason == "Vấn đề khác") {
                        customReason
                    } else {
                        selectedReason
                    }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(inputColor)
                        .padding(
                            horizontal = 18.dp,
                            vertical = 14.dp
                        )
                ) {

                    Text(
                        text =
                            if (contentType == "Đánh giá") {

                                "Đánh giá của $username tại $bookName là '$previewReason'"

                            } else {

                                "Bình luận của $username là '$previewReason'"
                            },

                        color = textColor,

                        fontSize = 16.sp,

                        lineHeight = 23.sp
                    )
                }


                // ==================================================
                // KHOẢNG CÁCH TRƯỚC NÚT GỬI
                // ==================================================

                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                // ==================================================
                // NÚT GỬI
                // ==================================================

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(
                            RoundedCornerShape(28.dp)
                        )
                        .background(buttonColor)
                        .clickable {

                            val reason =
                                if (selectedReason == "Vấn đề khác") {
                                    customReason
                                } else {
                                    selectedReason
                                }

                            if (reason.isBlank()) {
                                return@clickable
                            }

                            onSubmit(reason)

                            onDismiss()
                        },

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Gửi",

                        color = Color.White,

                        fontSize = 19.sp,

                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}


// ==========================================================
// REPORT OPTION
// ==========================================================

@Composable
private fun ReportOption(
    text: String,

    selected: Boolean,

    textColor: Color,

    secondaryColor: Color,

    dividerColor: Color,

    selectedColor: Color,

    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            // ==================================================
            // TEXT
            // ==================================================

            Text(
                text = text,

                color = textColor,

                fontSize = 19.sp,

                modifier = Modifier.weight(1f)
            )


            Spacer(
                modifier = Modifier.width(12.dp)
            )


            // ==================================================
            // CHECK CIRCLE
            // ==================================================

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) {
                            selectedColor
                        } else {
                            Color.Transparent
                        }
                    )
                    .border(
                        width =
                            if (selected) {
                                0.dp
                            } else {
                                2.dp
                            },

                        color =
                            if (selected) {
                                selectedColor
                            } else {
                                secondaryColor
                            },

                        shape = CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                if (selected) {

                    Text(
                        text = "✓",

                        color = Color.White,

                        fontSize = 25.sp,

                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }


        // ==================================================
        // DIVIDER
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(dividerColor)
        )
    }
}