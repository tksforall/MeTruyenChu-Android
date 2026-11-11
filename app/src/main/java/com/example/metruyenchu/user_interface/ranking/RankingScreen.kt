package com.example.metruyenchu.user_interface.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.example.metruyenchu.R
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import java.time.LocalDate
import com.example.metruyenchu.user_interface.explore.components.BookListItemData
import com.example.metruyenchu.user_interface.ranking.components.RankingBookItem

@Composable
fun RankingScreen(
    onBookClick: (Long) -> Unit
) {

    val currentDate = LocalDate.now()

    val currentMonth = currentDate.monthValue
    val currentYear = currentDate.year
    // ==========================================
    // TAB ĐANG ĐƯỢC CHỌN
    // ==========================================

    var selectedTab by remember {
        mutableStateOf("Lượt đọc")
    }


    // ==========================================
    // DANH SÁCH TRUYỆN TỪ API
    // ==========================================

    var books by remember {
        mutableStateOf<List<BookListItemData>>(emptyList())
    }


    // ==========================================
    // LẤY DANH SÁCH TRUYỆN
    // ==========================================

    LaunchedEffect(Unit) {

        try {

            val response = RetrofitClient.api.getAllBooks()

            if (response.isSuccessful) {

                books = response.body()
                    ?.map {
                        it.toBookListItemData()
                    }
                    ?: emptyList()

            } else {

                println(
                    "GET BOOKS FAILED: ${response.code()}"
                )
            }

        } catch (e: Exception) {

            println("GET BOOKS ERROR")
            e.printStackTrace()
        }
    }


    // ==========================================
    // SẮP XẾP THEO TAB
    // ==========================================

    val rankingBooks = when (selectedTab) {

        "Lượt đọc" -> {

            books.sortedByDescending {
                it.viewCount
            }
        }

        "Đề cử" -> {

            books.sortedByDescending {
                it.recommendationCount
            }
        }

        "Bình luận" -> {

            books.sortedByDescending {
                it.commentCount
            }
        }

        else -> {

            books
        }
    }


    // ==========================================
    // GIAO DIỆN
    // ==========================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
            .statusBarsPadding()
    ) {

        // ==========================================
        // HEADER
        // ==========================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.ic_launcher_foreground
                ),

                contentDescription = "Logo Mê Truyện Chữ",

                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .size(36.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
            )


            Spacer(
                modifier = Modifier.weight(1f)
            )


            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        Color(0xFF333333)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
            ) {

                Text(
                    text = "$currentMonth/$currentYear",

                    color = Color(0xFFAAAAAA),

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Medium
                )
            }
        }


        // ==========================================
        // 3 TAB
        // ==========================================

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            RankingTab(
                title = "Lượt đọc",

                selected = selectedTab == "Lượt đọc",

                onClick = {
                    selectedTab = "Lượt đọc"
                },

                modifier = Modifier.weight(1f)
            )


            RankingTab(
                title = "Đề cử",

                selected = selectedTab == "Đề cử",

                onClick = {
                    selectedTab = "Đề cử"
                },

                modifier = Modifier.weight(1f)
            )


            RankingTab(
                title = "Bình luận",

                selected = selectedTab == "Bình luận",

                onClick = {
                    selectedTab = "Bình luận"
                },

                modifier = Modifier.weight(1f)
            )
        }


        // ==========================================
        // DANH SÁCH TRUYỆN
        // ==========================================

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 8.dp,
                bottom = 12.dp
            ),

            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            itemsIndexed(rankingBooks) { index, book ->

                RankingBookItem(
                    rank = index + 1,
                    book = book,
                    onClick = {
                        onBookClick(book.bookId)
                    }
                )
            }
        }
    }
}


// ==========================================================
// RANKING TAB
// ==========================================================

@Composable
private fun RankingTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .height(47.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 4.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,

            color = if (selected) {
                Color.White
            } else {
                Color(0xFF777777)
            },

            fontSize = 16.sp,

            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )


        Spacer(
            modifier = Modifier.height(7.dp)
        )


        if (selected) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White)
            )
        }
    }
}