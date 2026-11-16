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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.RetrofitClient
import androidx.compose.foundation.text.BasicTextField


@Composable
fun AccountInfoScreen(
    userName: String,
    email: String,
    phone: String?,
    gender: String?,
    onSave: (phone: String, gender: String) -> Unit,
    onBack: () -> Unit = {}
) {

    // ==================================================
    // DỮ LIỆU USER
    // ==================================================

    var phoneValue by remember {
        mutableStateOf(phone ?: "")
    }

    var selectedGender by remember {
        mutableStateOf(
            when (gender) {
                "MALE" -> "Nam"
                "FEMALE" -> "Nữ"
                else -> "Bí mật"
            }
        )
    }

    var currentUserName by remember {
        mutableStateOf(userName)
    }

    var currentEmail by remember {
        mutableStateOf(email)
    }

    // ==================================================
    // LẤY USER TỪ BACKEND
    // ==================================================

    LaunchedEffect(Unit) {

        try {

            println("========== GET CURRENT USER ==========")

            val response =
                RetrofitClient.api.getCurrentUser()

            println("HTTP CODE = ${response.code()}")

            if (response.isSuccessful) {

                val user = response.body()

                if (user != null) {

                    currentUserName = user.userName
                    currentEmail = user.email


                    phoneValue = user.phone ?: ""

                    selectedGender = when (user.gender) {
                        "MALE" -> "Nam"
                        "FEMALE" -> "Nữ"
                        else -> "Bí mật"
                    }

                    println("USER NAME = ${user.userName}")
                    println("EMAIL = ${user.email}")
                    println("PHONE = ${user.phone}")
                    println("GENDER = ${user.gender}")
                }

            } else {

                println("GET USER FAILED")
                println("ERROR = ${response.errorBody()?.string()}")
            }

        } catch (e: Exception) {

            println("GET USER ERROR")
            println("ERROR = ${e.message}")

            e.printStackTrace()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {

        // ==================================================
        // HEADER
        // ==================================================

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
                text = "Thông tin tài khoản",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        // ==================================================
        // AVATAR
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(145.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5E5)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(135.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.BottomCenter)
                    .padding(start = 95.dp, bottom = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFD0D0D0)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Đổi ảnh đại diện",
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        Spacer(
            modifier = Modifier.height(22.dp)
        )


        // ==================================================
        // TÊN TÀI KHOẢN
        // ==================================================

        AccountInfoRow(
            icon = {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(32.dp)
                )
            },

            text = currentUserName
        )


        AccountDivider()


        // ==================================================
        // SỐ ĐIỆN THOẠI
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 44.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = Color(0xFFAAAAAA),
                modifier = Modifier.size(30.dp)
            )

            Spacer(
                modifier = Modifier.width(28.dp)
            )

            BasicTextField(
                value = phoneValue,

                onValueChange = { newValue ->

                    // Chỉ cho nhập số
                    if (newValue.all { it.isDigit() }) {
                        phoneValue = newValue
                    }
                },

                singleLine = true,

                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 20.sp
                ),

                modifier = Modifier.weight(1f),

                decorationBox = { innerTextField ->

                    if (phoneValue.isEmpty()) {

                        Text(
                            text = "Nhập số điện thoại",
                            color = Color(0xFF777777),
                            fontSize = 20.sp
                        )
                    }

                    innerTextField()
                }
            )
        }


        AccountDivider()


        // ==================================================
        // EMAIL
        // ==================================================

        AccountInfoRow(
            icon = {

                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(30.dp)
                )
            },

            text = currentEmail
        )


        AccountDivider()


        // ==================================================
        // GIỚI TÍNH
        // ==================================================

        // ==================================================
        // GIỚI TÍNH
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 44.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Giới tính",
                color = Color.White,
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.width(20.dp)
            )

            GenderSelector(
                selectedGender = selectedGender,
                onGenderSelected = {
                    selectedGender = it
                },
                modifier = Modifier.weight(1f)
            )
        }


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        // ==================================================
        // NÚT LƯU
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp)
                .height(55.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFF2474A6))
                .clickable {

                    val genderValue = when (selectedGender) {
                        "Nam" -> "MALE"
                        "Nữ" -> "FEMALE"
                        else -> "SECRET"
                    }

                    onSave(
                        phoneValue,
                        genderValue
                    )
                },

            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Lưu",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// ==========================================================
// DÒNG THÔNG TIN
// ==========================================================

@Composable
private fun AccountInfoRow(
    icon: @Composable () -> Unit,
    text: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 44.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        icon()

        Spacer(
            modifier = Modifier.width(28.dp)
        )

        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}


// ==========================================================
// ĐƯỜNG KẺ
// ==========================================================

@Composable
private fun AccountDivider() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
            .height(1.dp)
            .background(Color(0xFF5A5A5A))
    )
}


// ==========================================================
// GIỚI TÍNH
// ==========================================================

@Composable
private fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF444444)),
        verticalAlignment = Alignment.CenterVertically
    ) {

        GenderItem(
            text = "Bí mật",
            selected = selectedGender == "Bí mật",
            onClick = { onGenderSelected("Bí mật") },
            modifier = Modifier.weight(1f)
        )

        GenderItem(
            text = "Nam",
            selected = selectedGender == "Nam",
            onClick = { onGenderSelected("Nam") },
            modifier = Modifier.weight(1f)
        )

        GenderItem(
            text = "Nữ",
            selected = selectedGender == "Nữ",
            onClick = { onGenderSelected("Nữ") },
            modifier = Modifier.weight(1f)
        )
    }
}


// ==========================================================
// GENDER ITEM
// ==========================================================

@Composable
private fun GenderItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .height(50.dp)
            .clickable {
                onClick()
            }
            .background(
                if (selected) {
                    Color(0xFFE0E0E0)
                } else {
                    Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = if (selected) {
                Color(0xFF555555)
            } else {
                Color(0xFFBBBBBB)
            },
            fontSize = 15.sp,
            maxLines = 1
        )
    }
}