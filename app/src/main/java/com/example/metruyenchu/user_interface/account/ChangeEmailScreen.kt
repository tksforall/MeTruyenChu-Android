package com.example.metruyenchu.user_interface.account

import android.util.Patterns
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
import androidx.compose.material.icons.filled.Email
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
import com.example.metruyenchu.api.ChangeEmailRequest
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ChangeEmailScreen(
    onBack: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
            // ==========================================
            // HEADER
            // ==========================================
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
                    text = "Đổi email",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // MÔ TẢ NGẮN
            // ==========================================
            Text(
                text = "Nhập mật khẩu hiện tại để xác minh và cập nhật địa chỉ email mới cho tài khoản.",
                color = Color(0xFFAAAAAA),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 36.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ==========================================
            // MẬT KHẨU HIỆN TẠI
            // ==========================================
            ChangeEmailPasswordField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = null
                },
                visible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // EMAIL MỚI
            // ==========================================
            ChangeEmailTextField(
                value = newEmail,
                onValueChange = {
                    newEmail = it
                    errorMessage = null
                }
            )

            // ==========================================
            // THÔNG BÁO LỖI
            // ==========================================
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 36.dp, top = 12.dp, end = 36.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ==========================================
            // NÚT LƯU
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(
                        if (isLoading) Color(0xFF555555) else Color(0xFF2474A6)
                    )
                    .clickable(enabled = !isLoading) {
                        val trimmedEmail = newEmail.trim()

                        when {
                            currentPassword.isBlank() -> {
                                errorMessage = "Vui lòng nhập mật khẩu hiện tại"
                                return@clickable
                            }
                            trimmedEmail.isBlank() -> {
                                errorMessage = "Vui lòng nhập email mới"
                                return@clickable
                            }
                            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                                errorMessage = "Email mới không đúng định dạng"
                                return@clickable
                            }
                        }

                        focusManager.clearFocus()

                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            try {
                                val response = RetrofitClient.api.changeEmail(
                                    ChangeEmailRequest(
                                        currentPassword = currentPassword,
                                        newEmail = trimmedEmail
                                    )
                                )

                                if (response.isSuccessful) {
                                    currentPassword = ""
                                    newEmail = ""
                                    snackbarHostState.showSnackbar("Đổi email thành công")
                                } else {
                                    when (response.code()) {
                                        401, 400, 403 -> {
                                            errorMessage = "Mật khẩu hiện tại hoặc email không hợp lệ"
                                        }
                                        409 -> {
                                            errorMessage = "Email này đã được sử dụng"
                                        }
                                        else -> {
                                            errorMessage = "Đổi email thất bại (${response.code()})"
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                errorMessage = "Không thể kết nối đến máy chủ"
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ==================================================
        // SNACKBAR
        // ==================================================
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 35.dp, start = 30.dp, end = 30.dp)
        )
    }
}

// ==========================================================
// PASSWORD FIELD (ĐỒNG BỘ CHIỀU CAO 52.DP & ICON XÁM)
// ==========================================================
@Composable
private fun ChangeEmailPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .height(52.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
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
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Mật khẩu hiện tại",
                                color = Color(0xFFAAAAAA),
                                fontSize = 18.sp
                            )
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

// ==========================================================
// EMAIL FIELD (ĐỒNG BỘ CHIỀU CAO 52.DP & ICON XÁM)
// ==========================================================
@Composable
private fun ChangeEmailTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .height(52.dp),
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                        contentDescription = null,
                        tint = Color(0xFFAAAAAA),
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Email mới",
                                color = Color(0xFFAAAAAA),
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
                        .background(Color(0xFF5A5A5A))
                )
            }
        }
    )
}