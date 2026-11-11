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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// MÀU SẮC
// ==========================================================

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val LineColor = Color(0xFF777777)
private val ButtonBlue = Color(0xFF1976B2)


// ==========================================================
// MÀN HÌNH ĐĂNG KÝ
// ==========================================================

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onRegisterClick: (String, String) -> Unit = { _, _ -> }
) {

    // ======================================================
    // BIẾN DỮ LIỆU
    // ======================================================

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
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


    // ======================================================
    // GIAO DIỆN CHÍNH
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
                .align(Alignment.TopStart)
                .padding(
                    start = 12.dp,
                    top = 35.dp
                )
        ) {

            Icon(
                imageVector = Icons.Default.ArrowBack,

                contentDescription = "Quay lại",

                tint = Color.White,

                modifier = Modifier.size(30.dp)
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
                modifier = Modifier.height(70.dp)
            )

            Box(
                modifier = Modifier.size(180.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "MTC",

                    color = Color(0xFFFFD740),

                    fontSize = 64.sp
                )
            }


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
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                singleLine = true,

                textStyle = TextStyle(
                    color = TextWhite,
                    fontSize = 18.sp
                ),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),

                decorationBox = { innerTextField ->

                    Row(
                        modifier = Modifier.fillMaxSize(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Email,

                            contentDescription =
                                "Email",

                            tint = TextWhite,

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(40.dp)
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


            // Gạch chân Email

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineColor)
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ==================================================
            // MẬT KHẨU
            // ==================================================

            BasicTextField(
                value = password,

                onValueChange = {
                    password = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                singleLine = true,

                textStyle = TextStyle(
                    color = TextWhite,
                    fontSize = 18.sp
                ),

                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                keyboardOptions = KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password
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
                                Icons.Default.Lock,

                            contentDescription =
                                "Mật khẩu",

                            tint = TextWhite,

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(40.dp)
                        )

                        Box(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            if (password.isEmpty()) {

                                Text(
                                    text = "Mật khẩu",

                                    color = TextGray,

                                    fontSize = 18.sp
                                )
                            }

                            innerTextField()
                        }


                        IconButton(
                            onClick = {

                                passwordVisible =
                                    !passwordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,

                                contentDescription =
                                    "Hiện hoặc ẩn mật khẩu",

                                tint = TextGray
                            )
                        }
                    }
                }
            )


            // Gạch chân mật khẩu

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineColor)
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // ==================================================
            // XÁC NHẬN MẬT KHẨU
            // ==================================================

            BasicTextField(
                value = confirmPassword,

                onValueChange = {
                    confirmPassword = it
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                singleLine = true,

                textStyle = TextStyle(
                    color = TextWhite,
                    fontSize = 18.sp
                ),

                visualTransformation =
                    if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                keyboardOptions = KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password
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
                                Icons.Default.Lock,

                            contentDescription =
                                "Xác nhận mật khẩu",

                            tint = TextWhite,

                            modifier =
                                Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(40.dp)
                        )

                        Box(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            if (confirmPassword.isEmpty()) {

                                Text(
                                    text =
                                        "Xác nhận mật khẩu mới",

                                    color = TextGray,

                                    fontSize = 18.sp
                                )
                            }

                            innerTextField()
                        }


                        IconButton(
                            onClick = {

                                confirmPasswordVisible =
                                    !confirmPasswordVisible
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (confirmPasswordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,

                                contentDescription =
                                    "Hiện hoặc ẩn mật khẩu",

                                tint = TextGray
                            )
                        }
                    }
                }
            )


            // Gạch chân xác nhận mật khẩu

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineColor)
            )


            // ==================================================
            // KHOẢNG CÁCH TRƯỚC NÚT ĐĂNG KÝ
            // ==================================================

            Spacer(
                modifier = Modifier.height(40.dp)
            )


            // ==================================================
            // NÚT ĐĂNG KÝ
            // ==================================================

            Button(
                onClick = {

                    if (
                        email.isNotBlank() &&
                        password.isNotBlank() &&
                        password == confirmPassword
                    ) {

                        onRegisterClick(
                            email,
                            password
                        )
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

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
                    text = "Đăng ký",

                    color = Color.White,

                    fontSize = 20.sp
                )
            }
        }
    }
}