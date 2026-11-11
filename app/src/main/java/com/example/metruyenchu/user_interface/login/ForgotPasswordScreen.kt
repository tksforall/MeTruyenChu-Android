package com.example.metruyenchu.user_interface.login

import androidx.compose.foundation.background

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// MÀU SẮC
// ==========================================================

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val ButtonBlue = Color(0xFF1976B2)


// ==========================================================
// FORGOT PASSWORD SCREEN
// ==========================================================

@Composable
fun ForgotPasswordScreen(
    // Bấm X
    onBack: () -> Unit = {},

    // Gửi mã xác nhận
    onSendCodeClick: (String) -> Unit = {},

) {

    var email by remember {
        mutableStateOf("")
    }

    var emailError by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // ROOT
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        // ==================================================
        // NÚT X
        // ==================================================

        IconButton(
            onClick = onBack,

            modifier = Modifier
                .padding(
                    start = 8.dp,
                    top = 8.dp
                )
                .size(48.dp)
                .align(Alignment.TopStart)
        ) {

            Icon(
                imageVector = Icons.Default.Close,

                contentDescription = "Quay lại",

                tint = Color.White,

                modifier = Modifier.size(28.dp)
            )
        }


        // ==================================================
        // NỘI DUNG
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 30.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ==================================================
            // LOGO
            // ==================================================

            Spacer(
                modifier = Modifier.height(90.dp)
            )


            Box(
                modifier = Modifier.size(180.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "MTC",

                    color = Color(0xFFFFD740),

                    fontSize = 64.sp,

                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // ==================================================
            // TIÊU ĐỀ
            // ==================================================

            Text(
                text = "Quên mật khẩu",

                color = TextWhite,

                fontSize = 25.sp,

                fontWeight = FontWeight.Medium
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // ==================================================
            // HƯỚNG DẪN
            // ==================================================

            Text(
                text = "Nhập email bạn đã đăng ký để nhận mã xác nhận.",

                color = TextGray,

                fontSize = 15.sp,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            )


            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // ==================================================
            // EMAIL
            // ==================================================

            BasicTextField(
                value = email,

                onValueChange = {
                    email = it
                    emailError = false
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                singleLine = true,

                textStyle =
                    androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ),

                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Email
                    ),

                decorationBox = { innerTextField ->

                    Row(
                        modifier =
                            Modifier.fillMaxSize(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Email,

                            contentDescription =
                                "Email",

                            tint = Color.White,

                            modifier =
                                Modifier.size(28.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(25.dp)
                        )


                        Box(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            if (email.isEmpty()) {

                                Text(
                                    text = "Email",

                                    color = TextGray,

                                    fontSize = 18.sp
                                )
                            }

                            innerTextField()
                        }
                    }
                }
            )


            // ==================================================
            // ĐƯỜNG KẺ
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Color(0xFF777777)
                    )
            )


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // ==================================================
            // GỬI MÃ XÁC NHẬN
            // ==================================================

            Button(
                onClick = {

                    emailError =
                        email.isBlank()

                    if (!emailError) {

                        onSendCodeClick(email)
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape =
                    RoundedCornerShape(30.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ButtonBlue,

                        contentColor =
                            TextWhite
                    )
            ) {

                Text(
                    text = "Gửi mã xác nhận",

                    fontSize = 18.sp
                )
            }



        }
    }
}