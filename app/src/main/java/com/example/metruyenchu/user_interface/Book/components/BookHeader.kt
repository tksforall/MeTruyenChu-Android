package com.example.metruyenchu.user_interface.Book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import coil3.compose.AsyncImage
import com.example.metruyenchu.api.BookResponse

@Composable
fun BookHeader(
    book: BookResponse,

    onBack: () -> Unit = {},

    onReadClick: () -> Unit = {},

    onAddToLibraryClick: () -> Unit = {},

    // Trạng thái thật lấy từ Library / API
    isAddedToLibrary: Boolean = false
) {

    // ==================================================
    // THỂ LOẠI THẬT
    // ==================================================

    val mainGenre =
        book.bookGenres
            .orEmpty()
            .mapNotNull {
                it.genre?.genreName
            }
            .distinct()
            .firstOrNull()
            ?: "Chưa phân loại"


    // ==================================================
    // HEADER
    // ==================================================

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3A3A3A))
            .padding(
                top = 4.dp,
                bottom = 14.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {

        // ==================================================
        // BACK
        // ==================================================

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,

                contentDescription = "Quay lại",

                tint = Color.White,

                modifier = Modifier.size(30.dp)
            )
        }


        // ==================================================
        // ẢNH + THÔNG TIN
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),

            verticalAlignment = Alignment.Top
        ) {

            // ==================================================
            // ẢNH BÌA THẬT
            // ==================================================

            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(182.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(Color.DarkGray),

                contentAlignment = Alignment.Center
            ) {

                if (!book.coverUrl.isNullOrBlank()) {

                    AsyncImage(
                        model = book.coverUrl,

                        contentDescription =
                            "Ảnh bìa ${book.title}",

                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),

                        contentScale = ContentScale.Crop
                    )

                } else {

                    Text(
                        text = "ẢNH",

                        color = Color(0xFFAAAAAA),

                        fontSize = 11.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.width(12.dp)
            )


            // ==================================================
            // THÔNG TIN
            // ==================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // ==================================================
                // THỂ LOẠI THẬT
                // ==================================================

                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF1976D2),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(
                            horizontal = 6.dp,
                            vertical = 2.dp
                        )
                ) {

                    Text(
                        text = mainGenre,

                        color = Color.White,

                        fontSize = 10.sp
                    )
                }


                Spacer(
                    modifier = Modifier.height(4.dp)
                )


                // ==================================================
                // TÊN TRUYỆN THẬT
                // ==================================================

                Text(
                    text = book.title,

                    color = Color.White,

                    fontSize = 17.sp,

                    fontWeight = FontWeight.Bold,

                    maxLines = 2
                )


                Spacer(
                    modifier = Modifier.height(2.dp)
                )


                // ==================================================
                // TÁC GIẢ THẬT
                // ==================================================

                Text(
                    text = book.author,

                    color = Color.LightGray,

                    fontSize = 13.sp,

                    maxLines = 1
                )


                Spacer(
                    modifier = Modifier.height(3.dp)
                )


                // ==================================================
                // ĐÁNH GIÁ THẬT
                // ==================================================

                Text(
                    text = "⭐⭐⭐⭐⭐ ${
                        String.format(
                            "%.1f",
                            book.ratingScore ?: 0.0
                        )
                    } (${book.ratingCount ?: 0} đánh giá)",

                    color = Color.Yellow,

                    fontSize = 11.sp,

                    maxLines = 1
                )


                Spacer(
                    modifier = Modifier.height(10.dp)
                )


                // ==================================================
                // BUTTON
                // ==================================================

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ==================================================
                    // ĐỌC
                    // ==================================================

                    Button(
                        onClick = onReadClick,

                        modifier =
                            Modifier.height(36.dp),

                        contentPadding =
                            PaddingValues(
                                horizontal = 12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color(0xFF1976D2),

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            text = "Đọc truyện",

                            fontSize = 11.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )


                    // ==================================================
                    // TỦ TRUYỆN
                    // ==================================================

                    Button(
                        onClick = onAddToLibraryClick,

                        modifier =
                            Modifier.height(36.dp),

                        contentPadding =
                            PaddingValues(
                                horizontal = 10.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    if (isAddedToLibrary) {
                                        Color(0xFF555555)
                                    } else {
                                        Color.White
                                    },

                                contentColor =
                                    if (isAddedToLibrary) {
                                        Color.White
                                    } else {
                                        Color.Black
                                    }
                            )
                    ) {

                        Text(
                            text =
                                if (isAddedToLibrary) {
                                    "− Trừ khỏi tủ truyện"
                                } else {
                                    "+ Thêm vào tủ truyện"
                                },

                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}