package com.example.metruyenchu.user_interface.reader.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding

// ==========================================================
// READER REPORT BOTTOM SHEET
// ==========================================================

@Composable
fun ReaderReportBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit = {},
    onSubmit: (reportType: String, detail: String) -> Unit = { _, _ -> }
) {

    // ======================================================
    // KHÔNG HIỂN THỊ
    // ======================================================

    if (!visible) return

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
    // DANH SÁCH LOẠI BÁO CÁO
    // ======================================================

    val reportOptions = listOf(
        "Báo lỗi nội dung",
        "Báo cáo Chương",
        "Báo lỗi chức năng"
    )

    // ======================================================
    // TRẠNG THÁI
    // ======================================================

    var selectedReport by remember {
        mutableStateOf("Báo lỗi nội dung")
    }

    var detail by remember {
        mutableStateOf("")
    }

    // ======================================================
    // PLACEHOLDER CHO Ô TO
    // ======================================================

    val detailPlaceholder = when (selectedReport) {

        "Báo lỗi nội dung" ->
            "Vui lòng cung cấp thông tin chi tiết về lỗi chương này (sai, thiếu, nhầm,...). Lỗi sẽ khắc phục trong thời gian sớm nhất."

        "Báo cáo Chương" ->
            "Vui lòng cung cấp thông tin chi tiết về VI PHẠM của chương này (vi phạm nội quy, vi phạm thuần phong mỹ tục,...)."

        "Báo lỗi chức năng" ->
            "Vui lòng cung cấp thông tin chi tiết về vấn đề bạn gặp phải (Không đọc được, không tải được,...)."

        else -> ""
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
                .fillMaxHeight(0.70f)
                .align(Alignment.BottomCenter)
                .background(
                    color = sheetColor,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
                .imePadding()
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
                    .background(
                        Color(0xFFAAAAAA)
                    )
            )

            // ==================================================
            // NỘI DUNG
            // ==================================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                    text = "Bạn cần báo cáo vấn đề gì?",

                    color = textColor,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold,

                    modifier = Modifier.padding(
                        bottom = 12.dp
                    )
                )

                // ==================================================
                // 3 LOẠI BÁO CÁO
                // ==================================================

                reportOptions.forEach { option ->

                    ReaderReportOption(
                        text = option,

                        selected =
                            option == selectedReport,

                        textColor = textColor,

                        secondaryColor = secondaryColor,

                        dividerColor = dividerColor,

                        selectedColor = selectedColor,

                        onClick = {

                            selectedReport = option

                            // Xóa nội dung cũ khi đổi loại báo cáo
                            detail = ""
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
                // Ô NHỎ
                // HIỂN THỊ LOẠI ĐÃ CHỌN
                // ==================================================

                TextField(
                    value = selectedReport,

                    onValueChange = {},

                    readOnly = true,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        ),

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

                        cursorColor = Color.Transparent
                    )
                )

                // ==================================================
                // KHOẢNG CÁCH
                // ==================================================

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // ==================================================
                // Ô NHẬP CHI TIẾT
                // ==================================================


                TextField(
                    value = detail,

                    onValueChange = {
                        detail = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),

                    placeholder = {
                        Text(
                            text = detailPlaceholder,
                            color = Color(0xFF8A8A8A),
                            fontSize = 15.sp
                        )
                    },

                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),

                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,

                        focusedContainerColor = Color(0xFF404040),
                        unfocusedContainerColor = Color(0xFF404040),

                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,

                        cursorColor = Color.White,

                        focusedPlaceholderColor = Color(0xFF8A8A8A),
                        unfocusedPlaceholderColor = Color(0xFF8A8A8A)
                    ),

                    shape = RoundedCornerShape(14.dp)
                )

                // ==================================================
                // KHOẢNG CÁCH TRƯỚC GỬI
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

                            // ======================================
                            // KHÔNG KIỂM TRA detail
                            // → KHÔNG BẮT BUỘC NHẬP
                            // ======================================

                            onSubmit(
                                selectedReport,
                                detail
                            )

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
// READER REPORT OPTION
// ==========================================================

@Composable
private fun ReaderReportOption(
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

            verticalAlignment =
                Alignment.CenterVertically
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

                contentAlignment =
                    Alignment.Center
            ) {

                if (selected) {

                    Text(
                        text = "✓",

                        color = Color.White,

                        fontSize = 25.sp,

                        fontWeight =
                            FontWeight.Normal
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