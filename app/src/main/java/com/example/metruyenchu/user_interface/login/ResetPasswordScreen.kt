package com.example.metruyenchu.user_interface.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
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

private val BackgroundColor = Color(0xFF303030)
private val TextWhite = Color.White
private val TextGray = Color(0xFFAAAAAA)
private val LineColor = Color(0xFF777777)
private val ButtonBlue = Color(0xFF1976B2)

@Composable
fun ResetPasswordScreen(
    email: String = "",
    demoOtp: String = "",
    onBack: () -> Unit = {},
    onConfirm: (String, String) -> Unit = { _, _ -> }
) {
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
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
            Spacer(modifier = Modifier.height(35.dp))

            // LOGO ĐỒNG BỘ 330.DP
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

            // HIỂN THỊ MÃ OTP GIẢ LẬP ĐỂ DỄ TEST
            if (demoOtp.isNotEmpty()) {
                Text(
                    text = "Mã xác nhận (Demo) cho $email: $demoOtp",
                    color = Color(0xFFFFD740),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // NHẬP MÃ XÁC NHẬN
            ResetPasswordInput(
                value = code,
                onValueChange = {
                    code = it
                    errorMessage = ""
                },
                placeholder = "Mã xác nhận",
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(14.dp))

            // MẬT KHẨU MỚI
            PasswordInput(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                placeholder = "Mật khẩu mới",
                visible = passwordVisible,
                onVisibleChange = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // NHẬP LẠI MẬT KHẨU
            PasswordInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                placeholder = "Nhập lại mật khẩu",
                visible = confirmPasswordVisible,
                onVisibleChange = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // XÁC NHẬN
            Button(
                onClick = {
                    when {
                        code.isBlank() -> {
                            errorMessage = "Vui lòng nhập mã xác nhận"
                        }
                        newPassword.length < 6 -> {
                            errorMessage = "Mật khẩu mới phải có ít nhất 6 ký tự"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Mật khẩu nhập lại không khớp"
                        }
                        else -> {
                            errorMessage = ""
                            onConfirm(code.trim(), newPassword)
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
                    text = "Xác nhận",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // NÚT BACK ĐẶT SAU COLUMN
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
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
    }
}

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
            .height(50.dp),
        singleLine = true,
        textStyle = TextStyle(color = TextWhite, fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(text = placeholder, color = TextGray, fontSize = 18.sp)
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
}

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
            .height(50.dp),
        singleLine = true,
        textStyle = TextStyle(color = TextWhite, fontSize = 18.sp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(text = placeholder, color = TextGray, fontSize = 18.sp)
                        }
                        innerTextField()
                    }
                    IconButton(onClick = onVisibleChange) {
                        Icon(
                            imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
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
}