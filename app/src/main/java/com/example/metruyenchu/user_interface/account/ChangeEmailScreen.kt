package com.example.metruyenchu.user_interface.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.metruyenchu.api.ChangeEmailRequest
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ChangeEmailScreen(
    onBack: () -> Unit = {}
) {

    // ==================================================
    // DỮ LIỆU
    // ==================================================

    var currentPassword by remember {
        mutableStateOf("")
    }

    var newEmail by remember {
        mutableStateOf("")
    }


    // ==================================================
    // HIỆN / ẨN MẬT KHẨU
    // ==================================================

    var passwordVisible by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // TRẠNG THÁI
    // ==================================================

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // SNACKBAR
    // ==================================================

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()


    // ==================================================
    // SCREEN
    // ==================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
            .statusBarsPadding()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
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
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
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
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }


            Spacer(
                modifier = Modifier.height(50.dp)
            )


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

                onVisibilityChange = {
                    passwordVisible =
                        !passwordVisible
                }
            )


            Spacer(
                modifier = Modifier.height(30.dp)
            )


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
            // ERROR
            // ==========================================

            if (errorMessage != null) {

                Text(
                    text = errorMessage!!,
                    color = Color(0xFFFF6B6B),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(
                        start = 44.dp,
                        top = 8.dp,
                        end = 44.dp
                    )
                )
            }


            Spacer(
                modifier = Modifier.height(45.dp)
            )


            // ==========================================
            // NÚT LƯU
            // ==========================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp)
                    .height(55.dp)
                    .clip(
                        RoundedCornerShape(28.dp)
                    )
                    .background(
                        if (isLoading) {
                            Color(0xFF555555)
                        } else {
                            Color(0xFF2474A6)
                        }
                    )
                    .clickable(
                        enabled = !isLoading
                    ) {

                        // ==========================================
                        // 1. KIỂM TRA Ô TRỐNG
                        // ==========================================

                        if (currentPassword.isBlank()) {

                            errorMessage =
                                "Vui lòng nhập mật khẩu hiện tại"

                            return@clickable
                        }

                        if (newEmail.isBlank()) {

                            errorMessage =
                                "Vui lòng nhập email mới"

                            return@clickable
                        }


                        // ==========================================
                        // 2. GỌI BACKEND
                        // ==========================================

                        scope.launch {

                            isLoading = true
                            errorMessage = null

                            try {

                                val email = newEmail.trim()

                                val response =
                                    RetrofitClient.api.changeEmail(
                                        ChangeEmailRequest(
                                            currentPassword = currentPassword,
                                            newEmail = email
                                        )
                                    )

                                // ==================================
                                // 3. THÀNH CÔNG
                                // ==================================

                                if (response.isSuccessful) {

                                    currentPassword = ""
                                    newEmail = ""

                                    snackbarHostState.showSnackbar(
                                        "Đổi email thành công"
                                    )

                                }

                                // ==================================
                                // 4. LỖI TỪ BACKEND
                                // ==================================

                                else {

                                    when (response.code()) {

                                        401, 400 -> {

                                            errorMessage =
                                                "Mật khẩu hoặc email không chính xác"
                                        }

                                        409 -> {

                                            errorMessage =
                                                "Email đã được sử dụng"
                                        }

                                        403 -> {

                                            // Trường hợp Spring Security vẫn trả 403
                                            // thay vì 401 cho password sai
                                            errorMessage =
                                                "Mật khẩu hoặc email không chính xác"
                                        }

                                        else -> {

                                            errorMessage =
                                                "Đổi email thất bại"
                                        }
                                    }
                                }

                            } catch (e: Exception) {

                                e.printStackTrace()

                                errorMessage =
                                    "Không thể kết nối đến máy chủ"

                            } finally {

                                isLoading = false
                            }
                        }
                    },

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = if (isLoading) {
                        "Đang lưu..."
                    } else {
                        "Lưu"
                    },

                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }


        // ==================================================
        // SNACKBAR
        // ==================================================

        SnackbarHost(
            hostState = snackbarHostState,

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 35.dp,
                    start = 30.dp,
                    end = 30.dp
                )
        )
    }
}


// ==========================================================
// PASSWORD FIELD
// ==========================================================

@Composable
private fun ChangeEmailPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFAAAAAA),
                modifier = Modifier.size(35.dp)
            )


            Spacer(
                modifier = Modifier.width(30.dp)
            )


            androidx.compose.foundation.text.BasicTextField(
                value = value,

                onValueChange = onValueChange,

                modifier = Modifier.weight(1f),

                singleLine = true,

                textStyle =
                    androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    ),

                visualTransformation =
                    if (visible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },

                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (value.isEmpty()) {

                            Text(
                                text = "Mật khẩu hiện tại",
                                color = Color(0xFFAAAAAA),
                                fontSize = 20.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )


            IconButton(
                onClick = onVisibilityChange
            ) {

                Icon(
                    imageVector =
                        if (visible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },

                    contentDescription =
                        if (visible) {
                            "Ẩn mật khẩu"
                        } else {
                            "Hiện mật khẩu"
                        },

                    tint = Color(0xFFAAAAAA)
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color(0xFF5A5A5A)
                )
        )
    }
}


// ==========================================================
// EMAIL FIELD
// ==========================================================

@Composable
private fun ChangeEmailTextField(
    value: String,
    onValueChange: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )


            Spacer(
                modifier = Modifier.width(30.dp)
            )


            androidx.compose.foundation.text.BasicTextField(
                value = value,

                onValueChange = onValueChange,

                modifier = Modifier.weight(1f),

                singleLine = true,

                textStyle =
                    androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    ),

                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (value.isEmpty()) {

                            Text(
                                text = "Email mới",
                                color = Color(0xFFAAAAAA),
                                fontSize = 20.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color(0xFF5A5A5A)
                )
        )
    }
}