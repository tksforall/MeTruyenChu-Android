package com.example.metruyenchu.user_interface.explore.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

data class BookListItemData(
    val bookId: Long = 0L,

    // =========================
    // HIỂN THỊ
    // =========================

    val imageUrl: String? = null,

    val genre: String = "",

    val subGenre: String = "",

    val title: String = "",

    val author: String = "",

    val rating: String = "0.0",

    val bookmarks: String = "0",

    val chapterCount: Int = 0,

    val description: String = "",

    // =========================
    // DỮ LIỆU RANKING
    // =========================

    val viewCount: Long = 0L,

    val recommendationCount: Long = 0L,

    val commentCount: Long = 0L
)

@Composable
private fun HighlightText(
    text: String,
    keyword: String,
    normalColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight? = null
) {
    val annotatedText = buildAnnotatedString {

        if (keyword.isBlank()) {
            append(text)
            return@buildAnnotatedString
        }

        val regex = Regex(
            Regex.escape(keyword),
            RegexOption.IGNORE_CASE
        )

        var lastIndex = 0

        regex.findAll(text).forEach { match ->

            append(
                text.substring(
                    lastIndex,
                    match.range.first
                )
            )

            withStyle(
                SpanStyle(
                    color = Color.Red
                )
            ) {
                append(match.value)
            }

            lastIndex = match.range.last + 1
        }

        append(
            text.substring(lastIndex)
        )
    }

    Text(
        text = annotatedText,
        color = normalColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}


@Composable
fun BookListItem(
    book: BookListItemData,
    modifier: Modifier = Modifier,
    highlightKeyword: String = "",
    onClick: () -> Unit = {}
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(135.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.Top
    ) {

        // =========================
        // ẢNH TRUYỆN
        // =========================

        AsyncImage(
            model = book.imageUrl,
            contentDescription = "Ảnh bìa",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(95.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(11.dp))
        )

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        // =========================
        // THÔNG TIN TRUYỆN
        // =========================

        Column(
            modifier = Modifier
                .weight(1f)
                .height(135.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // =========================
            // THỂ LOẠI
            // =========================

            Text(
                text = "#${book.genre}  #${book.subGenre}",
                color = Color(0xFF1976A8),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            // =========================
            // TÊN TRUYỆN
            // =========================

            HighlightText(
                text = book.title,
                keyword = highlightKeyword,
                normalColor = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            // =========================
            // TÁC GIẢ
            // =========================

            HighlightText(
                text = book.author,
                keyword = highlightKeyword,
                normalColor = Color(0xFFD0D0D0),
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            // =========================
            // ĐÁNH GIÁ + SỐ CHƯƠNG
            // =========================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Đánh giá",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(
                    modifier = Modifier.width(3.dp)
                )

                Text(
                    text = book.rating,
                    color = Color(0xFFAAAAAA),
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.width(18.dp)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Số chương",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "${book.chapterCount} chương",
                    color = Color(0xFFAAAAAA),
                    fontSize = 13.sp
                )
            }
        }
    }
}