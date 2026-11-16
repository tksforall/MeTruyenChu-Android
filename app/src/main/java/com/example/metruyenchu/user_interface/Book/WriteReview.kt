package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


// ==========================================================
// MÀN HÌNH VIẾT ĐÁNH GIÁ
// ==========================================================

@Composable
fun WriteReview(
    bookName: String = "Tên truyện",


    onBack: () -> Unit = {},

    onSubmit: (
        rating: Float,
        character: String,
        plot: String,
        world: String,
        content: String
    ) -> Unit = { _, _, _, _, _ -> },

    isSubmitting: Boolean = false,
    bookId: Long
) {

    // ======================================================
    // MÀU
    // ======================================================

    val backgroundColor = Color(0xFF303030)

    val headerColor = Color(0xFF282828)

    val textColor = Color(0xFFF5F5F5)

    val primaryColor = Color(0xFF1976D2)

    val disabledButtonColor = Color(0xFFA6A6A6)

    val enabledButtonColor = Color.Black


    // ======================================================
    // TRẠNG THÁI
    // ======================================================

    var rating by remember {
        mutableStateOf(5f)
    }

    var onlyRating by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // CÁC PHẦN MÔ TẢ
    // ======================================================

    val character =
        "Nhân vật chính như nào? (nhiệt huyết?, vô..."

    val plot =
        "Cốt truyện như nào? (logic?, sáng vắn?, bố..."

    val world =
        "Bố cục thế giới như nào? (lớn hay nhỏ?, m..."


    // ======================================================
    // NỘI DUNG REVIEW
    // ======================================================

    var content by remember {
        mutableStateOf("")
    }


    // ======================================================
    // CÓ THỂ ĐĂNG KHÔNG
    // ======================================================

    val canSubmit =
        !isSubmitting &&
                (
                        onlyRating ||
                                content.isNotBlank()
                        )


    // ======================================================
    // SCROLL
    // ======================================================

    val scrollState =
        rememberScrollState()

    val scope =
        rememberCoroutineScope()


    // ======================================================
    // ROOT
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {


        // ==================================================
        // HEADER
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(headerColor)
                .padding(horizontal = 18.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clickable {
                        if (!isSubmitting) {
                            onBack()
                        }
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Quay lại",

                    tint =
                        Color.White,

                    modifier =
                        Modifier.size(26.dp)
                )
            }


            Text(
                text = "Viết đánh giá",

                color = Color.White,

                fontSize = 18.sp,

                fontWeight =
                    FontWeight.Normal,

                modifier = Modifier
                    .weight(1f)
                    .padding(end = 30.dp),

                textAlign =
                    androidx.compose.ui.text.style.TextAlign.Center
            )
        }


        // ==================================================
        // NỘI DUNG
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = 34.dp,
                    vertical = 30.dp
                )
        ) {


            // ==================================================
            // ĐIỂM
            // ==================================================

            Text(
                text =
                    "Chấm điểm nội dung truyện: ${
                        String.format(
                            "%.1f",
                            rating
                        )
                    } điểm",

                color =
                    textColor,

                fontSize =
                    22.sp,

                fontWeight =
                    FontWeight.Bold,

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    androidx.compose.ui.text.style.TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // ==================================================
            // SLIDER
            // ==================================================

            Slider(
                value =
                    rating,

                onValueChange = {
                    if (!isSubmitting) {
                        rating = it
                    }
                },

                valueRange =
                    0f..5f,

                steps =
                    9,

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    androidx.compose.material3.SliderDefaults.colors(

                        thumbColor =
                            primaryColor,

                        activeTrackColor =
                            primaryColor,

                        inactiveTrackColor =
                            Color(0xFF555555)
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // ==================================================
            // CHỈ CHẤM ĐIỂM
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        "Tôi chỉ muốn chấm điểm\n(không viết đánh giá)",

                    color =
                        textColor,

                    fontSize =
                        18.sp,

                    lineHeight =
                        25.sp,

                    modifier =
                        Modifier.weight(1f)
                )


                Switch(
                    checked = onlyRating,
                    onCheckedChange = { isChecked ->
                        if (!isSubmitting) {
                            onlyRating = isChecked
                            if (isChecked) {
                                content = "" // Xóa trắng nội dung nếu bật chế độ chỉ chấm điểm
                            }
                        }
                    },

                    colors =
                        SwitchDefaults.colors(

                            checkedThumbColor =
                                Color.White,

                            checkedTrackColor =
                                primaryColor,

                            uncheckedThumbColor =
                                Color.White,

                            uncheckedTrackColor =
                                Color(0xFF555555)
                        )
                )
            }


            // ==================================================
            // REVIEW CONTENT
            // ==================================================

            if (!onlyRating) {

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                ReviewTextField(
                    value =
                        character,

                    onValueChange = {},

                    placeholder = "",

                    enabled = false
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                ReviewTextField(
                    value =
                        plot,

                    onValueChange = {},

                    placeholder = "",

                    enabled = false
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                ReviewTextField(
                    value =
                        world,

                    onValueChange = {},

                    placeholder = "",

                    enabled = false
                )


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                // ==================================================
                // Ô NHẬP REVIEW
                // ==================================================

                val bringIntoViewRequester =
                    remember {
                        BringIntoViewRequester()
                    }


                ReviewTextField(
                    value =
                        content,

                    onValueChange = {
                        if (!isSubmitting) {
                            content = it
                        }
                    },

                    placeholder =
                        "Nội dung bài đánh giá...",

                    minHeight =
                        150.dp,

                    enabled =
                        !isSubmitting,

                    modifier =
                        Modifier.bringIntoViewRequester(
                            bringIntoViewRequester
                        )
                )


                LaunchedEffect(
                    content
                ) {

                    if (
                        content.isNotEmpty() &&
                        !isSubmitting
                    ) {

                        scope.launch {

                            bringIntoViewRequester
                                .bringIntoView()
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(28.dp)
                )
            }


            // ==================================================
            // NÚT ĐĂNG
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(
                        color =
                            if (canSubmit)
                                enabledButtonColor
                            else
                                disabledButtonColor,

                        shape =
                            RoundedCornerShape(32.dp)
                    )
                    .clickable(
                        enabled = canSubmit
                    ) {
                        // Nếu bật "Tôi chỉ muốn chấm điểm" thì ép content thành chuỗi rỗng ""
                        val finalContent = if (onlyRating) "" else content.trim()

                        onSubmit(
                            rating,
                            character,
                            plot,
                            world,
                            finalContent
                        )
                    },

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        if (isSubmitting)
                            "Đang đăng..."
                        else
                            "Đăng",

                    color =
                        Color.White,

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Normal
                )
            }


            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}


// ==========================================================
// Ô NHẬP REVIEW
// ==========================================================

@Composable
private fun ReviewTextField(
    value: String,

    onValueChange: (String) -> Unit,

    placeholder: String,

    minHeight:
    androidx.compose.ui.unit.Dp =
        62.dp,

    enabled: Boolean =
        true,

    modifier: Modifier =
        Modifier
) {

    TextField(
        value =
            value,

        onValueChange =
            onValueChange,

        enabled =
            enabled,

        placeholder = {

            if (
                placeholder.isNotEmpty()
            ) {

                Text(
                    text =
                        placeholder,

                    color =
                        Color(0xFFB0B0B0),

                    fontSize =
                        17.sp,

                    maxLines =
                        1
                )
            }
        },

        modifier =
            modifier
                .fillMaxWidth()
                .height(minHeight)
                .border(
                    width = 2.dp,
                    color = Color(0xFF777777),
                    shape =
                        RoundedCornerShape(15.dp)
                ),

        shape =
            RoundedCornerShape(15.dp),

        textStyle =
            androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 17.sp
            ),

        colors =
            TextFieldDefaults.colors(

                focusedContainerColor =
                    Color.Transparent,

                unfocusedContainerColor =
                    Color.Transparent,

                disabledContainerColor =
                    Color.Transparent,

                focusedIndicatorColor =
                    Color.Transparent,

                unfocusedIndicatorColor =
                    Color.Transparent,

                disabledIndicatorColor =
                    Color.Transparent,

                cursorColor =
                    Color.White,

                focusedTextColor =
                    Color.White,

                unfocusedTextColor =
                    Color.White,

                disabledTextColor =
                    Color(0xFFB0B0B0),

                focusedPlaceholderColor =
                    Color(0xFFB0B0B0),

                unfocusedPlaceholderColor =
                    Color(0xFFB0B0B0)
            ),

        singleLine =
            minHeight < 100.dp
    )
}