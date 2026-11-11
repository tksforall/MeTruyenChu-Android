package com.example.metruyenchu.user_interface.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.AiSearchRequest
import com.example.metruyenchu.api.AiSearchResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.ui.theme.MeTruyenChuTheme
import kotlinx.coroutines.launch


@Composable
fun AISearchScreen(
    onBack: () -> Unit = {}
) {

    // ==================================================
    // TEXT ĐANG NHẬP TRONG Ô INPUT
    // ==================================================

    var inputText by remember {
        mutableStateOf("")
    }


    // ==================================================
    // TEXT ĐÃ BẤM GỬI
    // ==================================================

    var sentDescription by remember {
        mutableStateOf("")
    }


    // ==================================================
    // AI RESULT
    // ==================================================

    var aiResult by remember {
        mutableStateOf<AiSearchResponse?>(null)
    }


    // ==================================================
    // LOADING
    // ==================================================

    var isLoading by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // ERROR
    // ==================================================

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ==================================================
    // COROUTINE
    // ==================================================

    val scope = rememberCoroutineScope()


    // ==================================================
    // CALL AI API
    // ==================================================

    fun searchAI() {

        // Không gửi nếu ô nhập rỗng
        if (inputText.isBlank() || isLoading) {
            return
        }

        // Lưu nội dung hiện tại trước khi xóa ô nhập
        val textToSend = inputText.trim()

        // Hiển thị nội dung user vừa gửi
        sentDescription = textToSend

        // Xóa ô nhập
        inputText = ""

        // Reset kết quả cũ
        aiResult = null
        errorMessage = null

        scope.launch {

            isLoading = true

            try {

                val response =
                    RetrofitClient.api.searchByAiDescription(
                        AiSearchRequest(
                            description = textToSend
                        )
                    )

                if (response.isSuccessful) {

                    aiResult = response.body()

                    if (aiResult == null) {

                        errorMessage =
                            "Backend không trả về dữ liệu AI."
                    }

                } else {

                    val errorBody =
                        response.errorBody()?.string()

                    errorMessage =
                        "Lỗi ${response.code()}: ${
                            errorBody ?: "Không xác định"
                        }"
                }

            } catch (e: Exception) {

                errorMessage =
                    e.message
                        ?: "Không thể kết nối đến Backend."

            } finally {

                isLoading = false
            }
        }
    }


    // ==================================================
    // MAIN UI
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

            // BACK

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


            // TITLE

            Text(
                text = "AI Mô tả",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }


        // ==================================================
        // DIVIDER
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF3A3A3A))
        )


        // ==================================================
        // CHAT / RESULT
        // ==================================================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 16.dp,
                bottom = 16.dp
            ),

            verticalArrangement = Arrangement.spacedBy(
                14.dp
            )
        ) {


            // ==================================================
            // USER MESSAGE
            // ==================================================

            if (sentDescription.isNotBlank()) {

                item {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "Bạn",
                            color = Color(0xFFAAAAAA),
                            fontSize = 12.sp
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF3A3A3A),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(14.dp)
                        ) {

                            Text(
                                text = sentDescription,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }


            // ==================================================
            // LOADING
            // ==================================================

            if (isLoading) {

                item {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "AI đang phân tích...",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }


            // ==================================================
            // ERROR
            // ==================================================

            if (errorMessage != null) {

                item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFF452929),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(14.dp)
                    ) {

                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFFFAAAA),
                            fontSize = 14.sp
                        )
                    }
                }
            }


            // ==================================================
            // AI RESULT
            // ==================================================

            aiResult?.let { result ->

                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFF333333),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {


                        // ==========================================
                        // TITLE
                        // ==========================================

                        Text(
                            text = "AI phân tích",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        // ==========================================
                        // SUMMARY
                        // ==========================================

                        Text(
                            text = "Tóm tắt",
                            color = Color(0xFFBBBBBB),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Text(
                            text = result.summary,
                            color = Color.White,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )


                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )


                        // ==========================================
                        // KEYWORDS
                        // ==========================================

                        Text(
                            text = "Từ khóa",
                            color = Color(0xFFBBBBBB),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        result.keywords.forEach { keyword ->

                            Text(
                                text = "• $keyword",
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(
                                    bottom = 4.dp
                                )
                            )
                        }


                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )


                        // ==========================================
                        // GENRES
                        // ==========================================

                        Text(
                            text = "Thể loại",
                            color = Color(0xFFBBBBBB),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )

                        result.genres.forEach { genre ->

                            Text(
                                text = "• $genre",
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(
                                    bottom = 4.dp
                                )
                            )
                        }
                    }
                }
            }
        }


        // ==================================================
        // INPUT AREA
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()

                // Quan trọng:
                // Đẩy ô nhập lên ngay phía trên bàn phím
                .imePadding()

                .navigationBarsPadding()

                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    bottom = 12.dp,
                    top = 6.dp
                ),

            verticalAlignment = Alignment.Bottom
        ) {


            // ==================================================
            // TEXT FIELD
            // ==================================================

            OutlinedTextField(

                value = inputText,

                onValueChange = {
                    inputText = it
                },

                modifier = Modifier.fillMaxWidth(),

                placeholder = {

                    Text(
                        text = "Nhập nội dung truyện muốn tìm kiếm",
                        color = Color(0xFF777777),
                        fontSize = 14.sp
                    )
                },


                // ==================================================
                // SEND BUTTON
                // ==================================================

                trailingIcon = {

                    IconButton(
                        onClick = {
                            searchAI()
                        },

                        enabled =
                            inputText.isNotBlank()
                                    && !isLoading
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )

                        } else {

                            Icon(
                                imageVector =
                                    Icons.Default.ArrowUpward,

                                contentDescription =
                                    "Gửi",

                                tint =
                                    if (inputText.isBlank()) {
                                        Color(0xFF666666)
                                    } else {
                                        Color.White
                                    },

                                modifier =
                                    Modifier.size(25.dp)
                            )
                        }
                    }
                },


                // ==================================================
                // TEXT FIELD STYLE
                // ==================================================

                shape = RoundedCornerShape(22.dp),

                colors = TextFieldDefaults.colors(

                    focusedContainerColor =
                        Color(0xFF3A3A3A),

                    unfocusedContainerColor =
                        Color(0xFF3A3A3A),

                    disabledContainerColor =
                        Color(0xFF3A3A3A),

                    focusedIndicatorColor =
                        Color.Transparent,

                    unfocusedIndicatorColor =
                        Color.Transparent,

                    disabledIndicatorColor =
                        Color.Transparent,

                    // ==============================================
                    // CON TRỎ CHỮ
                    // ==============================================

                    // Dấu | khi đang gõ
                    // sẽ màu trắng và nhấp nháy
                    cursorColor = Color.White,

                    focusedTextColor =
                        Color.White,

                    unfocusedTextColor =
                        Color.White,

                    disabledTextColor =
                        Color.White
                ),


                // ==================================================
                // SỐ DÒNG
                // ==================================================

                minLines = 1,

                maxLines = 4
            )
        }
    }
}


// ==========================================================
// PREVIEW
// ==========================================================

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AISearchScreenPreview() {

    MeTruyenChuTheme {

        AISearchScreen()
    }
}