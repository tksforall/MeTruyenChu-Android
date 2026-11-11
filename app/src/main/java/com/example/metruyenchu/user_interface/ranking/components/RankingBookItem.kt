package com.example.metruyenchu.user_interface.ranking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.metruyenchu.user_interface.explore.components.BookListItemData

@Composable
fun RankingBookItem(
    rank: Int,
    book: BookListItemData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
            .clickable {
                onClick()
            },

        verticalAlignment = Alignment.CenterVertically
    ) {

        // =========================
        // HẠNG
        // =========================

        RankNumber(
            rank = rank
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )


        // =========================
        // ẢNH TRUYỆN
        // TẠM THỜI CHƯA XỬ LÝ
        // =========================

        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(
                    width = 82.dp,
                    height = 115.dp
                )
                .clip(
                    RoundedCornerShape(7.dp)
                )
        )


        Spacer(
            modifier = Modifier.width(9.dp)
        )


        // =========================
        // THÔNG TIN
        // =========================

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {

            // =========================
            // THỂ LOẠI
            // TẠM THỜI
            // =========================

            Text(
                text = "#${book.genre}  #${book.subGenre}",

                color = Color(0xFF1976A8),

                fontSize = 10.sp,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // =========================
            // TÊN TRUYỆN
            // =========================

            Text(
                text = book.title,

                color = Color.White,

                fontSize = 15.sp,

                fontWeight = FontWeight.Medium,

                maxLines = 2,

                overflow = TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(3.dp)
            )


            // =========================
            // TÁC GIẢ
            // =========================

            Text(
                text = book.author,

                color = Color(0xFFD0D0D0),

                fontSize = 12.sp,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // =========================
            // RATING + BOOKMARK
            // =========================

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(15.dp)
                )

                Spacer(
                    modifier = Modifier.width(2.dp)
                )

                Text(
                    text = book.rating,
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier.size(15.dp)
                )

                Spacer(
                    modifier = Modifier.width(2.dp)
                )

                Text(
                    text = book.bookmarks,
                    color = Color(0xFFAAAAAA),
                    fontSize = 12.sp
                )
            }
        }
    }
}


// ==========================================================
// SỐ THỨ HẠNG
// ==========================================================

@Composable
private fun RankNumber(
    rank: Int
) {

    val rankColor = when (rank) {

        1 -> Color(0xFFFFC107)

        2 -> Color(0xFFFF9800)

        3 -> Color(0xFFE5734F)

        4 -> Color(0xFF1976A8)

        else -> Color(0xFF777777)
    }


    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(
                RoundedCornerShape(50)
            )
            .background(rankColor),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = rank.toString(),

            color = Color.White,

            fontSize = 11.sp,

            fontWeight = FontWeight.Bold
        )
    }
}