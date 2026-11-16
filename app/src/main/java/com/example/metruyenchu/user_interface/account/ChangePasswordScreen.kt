package com.example.metruyenchu.user_interface.account

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ChangePasswordRequest
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val adjustedImePadding = (imeBottom - 20.dp).coerceAtLeast(0.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = adjustedImePadding)
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Đổi mật khẩu",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // MẬT KHẨU HIỆN TẠI
            PasswordField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = null
                },
                hint = "Mật khẩu hiện tại",
                visible = currentPasswordVisible,
                onVisibilityChange = { currentPasswordVisible = !currentPasswordVisible }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // MẬT KHẨU MỚI
            PasswordField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = null
                },
                hint = "Mật khẩu mới",
                visible = newPasswordVisible,
                onVisibilityChange = { newPasswordVisible = !newPasswordVisible }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // XÁC NHẬN MẬT KHẨU
            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = null
                },
                hint = "Xác nhận mật khẩu mới",
                visible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible }
            )

            // ERROR
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 44.dp, top = 12.dp, end = 44.dp)
                )
            }

            Spacer(modifier = Modifier.height(45.dp))

            // NÚT LƯU
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp)
                    .height(55.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        if (isLoading) Color(0xFF555555) else Color(0xFF2474A6)
                    )
                    .clickable(enabled = !isLoading) {
                        when {
                            currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                                errorMessage = "Vui lòng nhập đầy đủ thông tin"
                                return@clickable
                            }
                            newPassword.length < 6 -> {
                                errorMessage = "Mật khẩu mới phải có ít nhất 6 ký tự"
                                return@clickable
                            }
                            newPassword == currentPassword -> {
                                errorMessage = "Mật khẩu mới phải khác mật khẩu hiện tại"
                                return@clickable
                            }
                            newPassword != confirmPassword -> {
                                errorMessage = "Mật khẩu xác nhận không khớp"
                                return@clickable
                            }
                        }

                        focusManager.clearFocus()

                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            try {
                                val response = RetrofitClient.api.changePassword(
                                    ChangePasswordRequest(
                                        currentPassword = currentPassword,
                                        newPassword = newPassword,
                                        confirmPassword = confirmPassword
                                    )
                                )

                                if (response.isSuccessful) {
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                    snackbarHostState.showSnackbar("Đổi mật khẩu thành công")
                                } else if (response.code() == 401 || response.code() == 403) {
                                    errorMessage = "Mật khẩu hiện tại không chính xác"
                                } else {
                                    errorMessage = "Đổi mật khẩu thất bại (${response.code()})"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Không thể kết nối đến máy chủ"
                                e.printStackTrace()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isLoading) "Đang lưu..." else "Lưu",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // SNACKBAR
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 35.dp, start = 30.dp, end = 30.dp)
        )
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
            .height(55.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 20.sp),
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
                        tint = Color(0xFFAAAAAA),
                        modifier = Modifier.size(35.dp)
                    )

                    Spacer(modifier = Modifier.width(30.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(text = hint, color = Color(0xFFAAAAAA), fontSize = 20.sp)
                        }
                        innerTextField()
                    }

                    IconButton(onClick = onVisibilityChange) {
                        Icon(
                            imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (visible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                            tint = Color(0xFFAAAAAA)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF5A5A5A))
                )
            }
        }
    )
}