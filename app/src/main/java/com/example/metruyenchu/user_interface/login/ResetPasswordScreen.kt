package com.example.metruyenchu.user_interface.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text


// ==========================================================
// MÀU SẮC
// ==========================================================

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val ButtonBlue = Color(0xFF1976B2)


// ==========================================================
// RESET PASSWORD SCREEN
// ==========================================================

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit = {},

    onConfirm: (String, String, String) -> Unit = { _, _, _ -> },

    // Sau khi đổi mật khẩu thành công
    onResetSuccess: () -> Unit = {},

    // Email được truyền từ ForgotPasswordScreen
    email: String = ""
) {

    // ======================================================
    // TRẠNG THÁI
    // ======================================================

    var code by remember {
        mutableStateOf("")
    }

    var newPassword by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var confirmPasswordVisible by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
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
        // NÚT BACK
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
                imageVector = Icons.Default.ArrowBack,

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
                .padding(horizontal = 40.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ==================================================
            // LOGO
            // ==================================================

            Spacer(
                modifier = Modifier.height(100.dp)
            )

            Box(
                modifier = Modifier.size(180.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "MTC",

                    color = Color(0xFFFFD740),

                    fontSize = 64.sp,

                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(45.dp)
            )


            // ==================================================
            // NHẬP MÃ XÁC NHẬN
            // ==================================================

            ResetPasswordInput(
                value = code,

                onValueChange = {
                    code = it
                    errorMessage = ""
                },

                placeholder = "Mã xác nhận",

                keyboardType = KeyboardType.Number
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ==================================================
            // MẬT KHẨU MỚI
            // ==================================================

            PasswordInput(
                value = newPassword,

                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },

                placeholder = "Mật khẩu mới",

                visible = passwordVisible,

                onVisibleChange = {
                    passwordVisible = !passwordVisible
                }
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // ==================================================
            // NHẬP LẠI MẬT KHẨU
            // ==================================================

            PasswordInput(
                value = confirmPassword,

                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },

                placeholder = "Nhập lại mật khẩu",

                visible = confirmPasswordVisible,

                onVisibleChange = {
                    confirmPasswordVisible =
                        !confirmPasswordVisible
                }
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ==================================================
            // THÔNG BÁO LỖI
            // ==================================================

            if (errorMessage.isNotEmpty()) {

                Text(
                    text = errorMessage,

                    color = Color(0xFFFF7777),

                    fontSize = 14.sp,

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 10.dp
                        )
                )
            }


            // ==================================================
            // XÁC NHẬN
            // ==================================================

            Button(
                onClick = {

                    // ------------------------------
                    // KIỂM TRA
                    // ------------------------------

                    when {

                        code.isBlank() -> {
                            errorMessage =
                                "Vui lòng nhập mã xác nhận"
                        }

                        newPassword.isBlank() -> {
                            errorMessage =
                                "Vui lòng nhập mật khẩu mới"
                        }

                        confirmPassword.isBlank() -> {
                            errorMessage =
                                "Vui lòng nhập lại mật khẩu"
                        }

                        newPassword != confirmPassword -> {
                            errorMessage =
                                "Mật khẩu nhập lại không khớp"
                        }

                        else -> {

                            /*
                             * SAU NÀY BACKEND SẼ XỬ LÝ:
                             *
                             * 1. Kiểm tra email
                             * 2. Kiểm tra mã xác nhận
                             * 3. Kiểm tra mã còn hạn hay không
                             * 4. Hash mật khẩu mới
                             * 5. UPDATE USERS
                             * 6. Đánh dấu mã reset đã sử dụng
                             */

                            onResetSuccess()
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(30.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    contentColor = TextWhite
                )
            ) {

                Text(
                    text = "Xác nhận",

                    fontSize = 20.sp
                )
            }
        }
    }
}


// ==========================================================
// Ô NHẬP MÃ
// ==========================================================

@Composable
private fun ResetPasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {

    BasicTextField(
        value = value,

        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),

        singleLine = true,

        textStyle = TextStyle(
            color = Color.White,
            fontSize = 18.sp
        ),

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),

        decorationBox = { innerTextField ->

            Row(
                modifier = Modifier.fillMaxSize(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Lock,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier.size(28.dp)
                )

                Spacer(
                    modifier = Modifier.width(40.dp)
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    if (value.isEmpty()) {

                        Text(
                            text = placeholder,

                            color = TextGray,

                            fontSize = 18.sp
                        )
                    }

                    innerTextField()
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF777777))
    )
}


// ==========================================================
// Ô NHẬP MẬT KHẨU
// ==========================================================

@Composable
private fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onVisibleChange: () -> Unit
) {

    BasicTextField(
        value = value,

        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),

        singleLine = true,

        textStyle = TextStyle(
            color = Color.White,
            fontSize = 18.sp
        ),

        visualTransformation =
            if (visible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),

        decorationBox = { innerTextField ->

            Row(
                modifier = Modifier.fillMaxSize(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Lock,

                    contentDescription = null,

                    tint = Color.White,

                    modifier = Modifier.size(28.dp)
                )

                Spacer(
                    modifier = Modifier.width(40.dp)
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    if (value.isEmpty()) {

                        Text(
                            text = placeholder,

                            color = TextGray,

                            fontSize = 18.sp
                        )
                    }

                    innerTextField()
                }


                IconButton(
                    onClick = onVisibleChange
                ) {

                    Icon(
                        imageVector =
                            if (visible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,

                        contentDescription =
                            if (visible)
                                "Ẩn mật khẩu"
                            else
                                "Hiện mật khẩu",

                        tint = TextGray
                    )
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF777777))
    )
}