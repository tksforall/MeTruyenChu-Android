package com.example.metruyenchu.user_interface.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions


// ==========================================================
// MÀU SẮC
// ==========================================================

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val ButtonBlue = Color(0xFF1976B2)
private val ButtonGray = Color(0xFF666666)


// ==========================================================
// LOGIN SCREEN
// ==========================================================

@Composable
fun LoginScreen(
    // Bấm X
    onBack: () -> Unit = {},

    // Đăng nhập
    onLoginClick: (String, String) -> Unit = { _, _ -> },

    // Đăng ký
    onRegisterClick: () -> Unit = {},

    // Quên mật khẩu
    onForgotPasswordClick: () -> Unit = {},

    // Google
    onGoogleLoginClick: () -> Unit = {}
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var emailError by remember {
        mutableStateOf(false)
    }

    var passwordError by remember {
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
        // NÚT X - GÓC TRÊN TRÁI
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
        // NỘI DUNG LOGIN
        // ==================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 30.dp
                ),

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
                modifier = Modifier.height(70.dp)
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

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
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

                            tint = Color.White,

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


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Color(0xFF777777)
                    )
            )


            // ==================================================
            // MẬT KHẨU
            // ==================================================

            BasicTextField(
                value = password,

                onValueChange = {
                    password = it
                    passwordError = false
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                singleLine = true,

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
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
                        modifier = Modifier.fillMaxSize(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Lock,

                            contentDescription =
                                "Mật khẩu",

                            tint = Color.White,

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
                                    if (passwordVisible)
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
                    .background(
                        Color(0xFF777777)
                    )
            )


            Spacer(
                modifier = Modifier.height(40.dp)
            )


            // ==================================================
            // ĐĂNG NHẬP
            // ==================================================

            Button(

                onClick = {

                    emailError =
                        email.isBlank()

                    passwordError =
                        password.isBlank()

                    // Chỉ cho đăng nhập
                    // khi cả 2 ô đều có dữ liệu
                    if (
                        !emailError &&
                        !passwordError
                    ) {

                        onLoginClick(
                            email,
                            password
                        )
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),

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
                    text = "Đăng nhập",

                    fontSize = 15.sp
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // ==================================================
            // GOOGLE
            // ==================================================

            Button(

                onClick =
                    onGoogleLoginClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),

                shape =
                    RoundedCornerShape(30.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ButtonGray,

                        contentColor =
                            TextWhite
                    )
            ) {

                Text(
                    text = "Google",

                    color =
                        Color(0xFF4285F4),

                    fontSize = 20.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            // ==================================================
            // ĐĂNG KÝ
            // ==================================================

            Button(

                onClick =
                    onRegisterClick,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),

                shape =
                    RoundedCornerShape(30.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            ButtonGray,

                        contentColor =
                            TextWhite
                    )
            ) {

                Text(
                    text = "Đăng ký",

                    fontSize = 15.sp
                )
            }


            Spacer(
                modifier = Modifier.height(21.dp)
            )


            // ==================================================
            // QUÊN MẬT KHẨU
            // ==================================================

            Text(

                text = "Quên mật khẩu",

                color = TextWhite,

                fontSize = 18.sp,

                modifier =
                    Modifier.clickable {

                        onForgotPasswordClick()
                    }
            )
        }
    }
}