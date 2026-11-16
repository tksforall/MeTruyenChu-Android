package com.example.metruyenchu.user_interface.login

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.R


// ==========================================================
// MÀU SẮC
// ==========================================================

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val LineColor = Color(0xFF777777)
private val ButtonBlue = Color(0xFF1976B2)
private val ButtonGray = Color(0xFF666666)


// ==========================================================
// LOGIN SCREEN
// ==========================================================

@Composable
fun LoginScreen(
    onBack: () -> Unit = {},
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // ======================================================
    // ROOT
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {

        // ==================================================
        // NỘI DUNG LOGIN
        // ==================================================

        val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
        val adjustedImePadding = (imeBottom - 20.dp).coerceAtLeast(0.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = adjustedImePadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ==================================================
            // LOGO TO
            // ==================================================

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier.size(330.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_mtc),
                    contentDescription = "Logo MTC",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))


            // ==================================================
            // EMAIL (GẠCH CHÂN NẰM TRONG DECORATIONBOX)
            // ==================================================

            BasicTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = TextWhite,
                    fontSize = 18.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                decorationBox = { innerTextField ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(32.dp))

                            Box(modifier = Modifier.weight(1f)) {
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

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(LineColor)
                        )
                    }
                }
            )


            // ==================================================
            // MẬT KHẨU (GẠCH CHÂN NẰM TRONG DECORATIONBOX)
            // ==================================================

            Spacer(modifier = Modifier.height(14.dp))

            BasicTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = TextWhite,
                    fontSize = 18.sp
                ),
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                decorationBox = { innerTextField ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Mật khẩu",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(32.dp))

                            Box(modifier = Modifier.weight(1f)) {
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
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible) Icons.Default.Visibility
                                        else Icons.Default.VisibilityOff,
                                    contentDescription =
                                        if (passwordVisible) "Ẩn mật khẩu"
                                        else "Hiện mật khẩu",
                                    tint = TextGray
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(LineColor)
                        )
                    }
                }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))


            // ==================================================
            // ĐĂNG NHẬP
            // ==================================================

            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    when {
                        trimmedEmail.isBlank() || password.isBlank() -> {
                            errorMessage = "Vui lòng nhập đầy đủ email và mật khẩu"
                        }
                        !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                            errorMessage = "Email không đúng định dạng"
                        }
                        else -> {
                            errorMessage = null
                            onLoginClick(trimmedEmail, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    contentColor = TextWhite
                )
            ) {
                Text(
                    text = "Đăng nhập",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))


            // ==================================================
            // ĐĂNG KÝ
            // ==================================================

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonGray,
                    contentColor = TextWhite
                )
            ) {
                Text(
                    text = "Đăng ký",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ==================================================
            // QUÊN MẬT KHẨU
            // ==================================================

            Text(
                text = "Quên mật khẩu",
                color = TextWhite,
                fontSize = 17.sp,
                modifier = Modifier.clickable {
                    onForgotPasswordClick()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }


        // ==================================================
        // NÚT X - GÓC TRÊN TRÁI (ĐẶT SAU COLUMN ĐỂ LUÔN BẤM ĐƯỢC)
        // ==================================================

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
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
    }
}