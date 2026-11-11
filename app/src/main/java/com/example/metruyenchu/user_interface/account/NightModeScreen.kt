package com.example.metruyenchu.user_interface.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.UserSettingsRequest
import kotlinx.coroutines.launch


@Composable
fun NightModeScreen(
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ==================================================
    // DANH SÁCH MÀU
    // ==================================================

    val colors = listOf(
        Color(0xFFFFFFFF), // 0 - Trắng
        Color(0xFFF2D6C9), // 1 - Da
        Color(0xFF8B6F47), // 2 - Nâu
        Color(0xFFB5B5B5), // 3 - Ghi
        Color(0xFF666666), // 4 - Xám
        Color(0xFF1E1E1E)  // 5 - Đen
    )

    val colorHex = listOf(
        "#FFFFFF",
        "#F2D6C9",
        "#8B6F47",
        "#B5B5B5",
        "#666666",
        "#1E1E1E"
    )


    // ==================================================
    // STATE
    // ==================================================

    var selectedMode by remember {
        mutableStateOf("AUTO")
    }

    var selectedDayColor by remember {
        mutableStateOf(5)
    }

    var selectedNightColor by remember {
        mutableStateOf(5)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // GET SETTINGS KHI MỞ MÀN HÌNH
    // ==================================================

    LaunchedEffect(Unit) {

        try {

            val response =
                RetrofitClient.api.getUserSettings()

            if (response.isSuccessful) {

                val settings = response.body()

                if (settings != null) {

                    // ------------------------------
                    // NIGHT MODE
                    // ------------------------------

                    selectedMode =
                        when (settings.nightMode?.uppercase()) {

                            "ON" -> "ON"

                            "OFF" -> "OFF"

                            "AUTO" -> "AUTO"

                            else -> "AUTO"
                        }


                    // ------------------------------
                    // DAY BACKGROUND
                    // ------------------------------

                    val dayColor =
                        settings.backgroundColor
                            ?.uppercase()

                    val dayIndex =
                        colorHex.indexOfFirst {
                            it.equals(
                                dayColor,
                                ignoreCase = true
                            )
                        }

                    if (dayIndex >= 0) {
                        selectedDayColor = dayIndex
                    }


                    // ------------------------------
                    // NIGHT BACKGROUND
                    // ------------------------------

                    val nightColor =
                        settings.nightBackgroundColor
                            ?.uppercase()

                    val nightIndex =
                        colorHex.indexOfFirst {
                            it.equals(
                                nightColor,
                                ignoreCase = true
                            )
                        }

                    if (nightIndex >= 0) {
                        selectedNightColor = nightIndex
                    }
                }

            } else {

                Toast.makeText(
                    context,
                    "Không thể tải cài đặt",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {

            e.printStackTrace()

            Toast.makeText(
                context,
                "Lỗi kết nối máy chủ",
                Toast.LENGTH_SHORT
            ).show()

        } finally {

            isLoading = false
        }
    }


    // ==================================================
    // HÀM LƯU SETTINGS
    // ==================================================

    fun saveSettings(
        newDayColor: Int = selectedDayColor,
        newNightColor: Int = selectedNightColor,
        newMode: String = selectedMode
    ) {

        scope.launch {

            isSaving = true

            try {

                val request =
                    UserSettingsRequest(

                        backgroundColor =
                            colorHex[newDayColor],

                        nightBackgroundColor =
                            colorHex[newNightColor],

                        nightMode =
                            newMode
                    )


                val response =
                    RetrofitClient.api
                        .updateUserSettings(request)


                if (response.isSuccessful) {

                    Toast.makeText(
                        context,
                        "Đã lưu cài đặt",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "Không thể lưu cài đặt",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    context,
                    "Lỗi kết nối máy chủ",
                    Toast.LENGTH_SHORT
                ).show()

            } finally {

                isSaving = false
            }
        }
    }


    // ==================================================
    // GIAO DIỆN
    // ==================================================

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
                text = "Chế độ ban đêm",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }


        // ==================================================
        // LOADING
        // ==================================================

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.White
                )
            }

            return@Column
        }


        // ==================================================
        // CHẾ ĐỘ
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 36.dp,
                    end = 36.dp,
                    top = 22.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Chế độ",
                color = Color.White,
                fontSize = 20.sp
            )


            Spacer(
                modifier = Modifier.width(25.dp)
            )


            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(62.dp)
                    .clip(
                        RoundedCornerShape(32.dp)
                    )
                    .background(
                        Color(0xFF4A4A4A)
                    )
            ) {

                NightModeOption(
                    text = "Tự động",
                    selected = selectedMode == "AUTO",
                    modifier = Modifier.weight(1f),
                    onClick = {

                        selectedMode = "AUTO"

                        saveSettings(
                            newMode = "AUTO"
                        )
                    }
                )


                NightModeOption(
                    text = "Tắt",
                    selected = selectedMode == "OFF",
                    modifier = Modifier.weight(1f),
                    onClick = {

                        selectedMode = "OFF"

                        saveSettings(
                            newMode = "OFF"
                        )
                    }
                )


                NightModeOption(
                    text = "Bật",
                    selected = selectedMode == "ON",
                    modifier = Modifier.weight(1f),
                    onClick = {

                        selectedMode = "ON"

                        saveSettings(
                            newMode = "ON"
                        )
                    }
                )
            }
        }


        // ==================================================
        // MÔ TẢ
        // ==================================================

        Text(
            text = "Tự động là theo chế độ ban đêm của máy",
            color = Color.White,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 35.dp,
                    vertical = 20.dp
                )
        )


        // ==================================================
        // NỀN ĐỌC BAN NGÀY
        // ==================================================

        Text(
            text = "Nền đọc ban ngày",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(
                horizontal = 36.dp
            )
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        ColorSelectorRow(
            colors = colors,
            selectedIndex = selectedDayColor,
            onSelected = {

                selectedDayColor = it

                saveSettings(
                    newDayColor = it
                )
            }
        )


        // ==================================================
        // NỀN ĐỌC BAN ĐÊM
        // ==================================================

        Spacer(
            modifier = Modifier.height(28.dp)
        )


        Text(
            text = "Nền đọc ban đêm",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(
                horizontal = 36.dp
            )
        )


        Spacer(
            modifier = Modifier.height(18.dp)
        )


        ColorSelectorRow(
            colors = colors,
            selectedIndex = selectedNightColor,
            onSelected = {

                selectedNightColor = it

                saveSettings(
                    newNightColor = it
                )
            }
        )
    }
}


// ==========================================================
// MỘT LỰA CHỌN CHẾ ĐỘ
// ==========================================================

@Composable
private fun NightModeOption(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(62.dp)
            .clickable {
                onClick()
            }
            .background(
                if (selected) {
                    Color(0xFFE5E5E5)
                } else {
                    Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color =
                if (selected) {
                    Color(0xFF555555)
                } else {
                    Color.White
                },
            fontSize = 17.sp
        )
    }
}


// ==========================================================
// HÀNG CHỌN MÀU
// ==========================================================

@Composable
private fun ColorSelectorRow(
    colors: List<Color>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 36.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        colors.forEachIndexed { index, color ->

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 2.dp,
                        color =
                            if (index == selectedIndex) {
                                Color(0xFF168BD0)
                            } else {
                                Color(0xFFBDBDBD)
                            },
                        shape = CircleShape
                    )
                    .clickable {
                        onSelected(index)
                    },
                contentAlignment = Alignment.Center
            ) {

                if (index == selectedIndex) {

                    Icon(
                        imageVector =
                            Icons.Default.Check,
                        contentDescription =
                            "Đang chọn",
                        tint =
                            if (index >= 4) {
                                Color.White
                            } else {
                                Color(0xFF555555)
                            },
                        modifier =
                            Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}