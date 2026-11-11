package com.example.metruyenchu.user_interface.Book

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.metruyenchu.user_interface.Book.components.BookHeader


// ==========================================================
// DỮ LIỆU CHƯƠNG
// ==========================================================

data class ChapterData(
    val id: Long,
    val index: Int,
    val title: String,
    val timestamp: String
)


// ==========================================================
// MÀN HÌNH DANH SÁCH CHƯƠNG
// ==========================================================

@Composable
fun ChapterList(

    // ======================================================
    // DỮ LIỆU THẬT
    // ======================================================

    book: com.example.metruyenchu.api.BookResponse,

    chapters: List<com.example.metruyenchu.api.ChapterResponse>,

    currentChapterNumber: Int? = null,

    // ======================================================
    // HEADER
    // ======================================================

    onBack: () -> Unit = {},

    onReadClick: () -> Unit = {},

    onAddToLibraryClick: () -> Unit = {},

    // ======================================================
    // BẤM VÀO CHƯƠNG
    // ======================================================

    onChapterClick: (ChapterData) -> Unit = {
        onReadClick()
    },

    // ======================================================
    // CÁC TAB
    // ======================================================

    onOverviewClick: () -> Unit = {},

    onReviewClick: () -> Unit = {},

    onCommentClick: () -> Unit = {},

    onChapterListClick: () -> Unit = {}
) {


    // ======================================================
    // TRẠNG THÁI SẮP XẾP
    // ======================================================

    var isReversed by remember {
        mutableStateOf(false)
    }

    var isAddedToLibrary by remember {
        mutableStateOf(false)
    }

    // ======================================================
    // CHUYỂN DỮ LIỆU API -> DỮ LIỆU HIỂN THỊ
    // ======================================================

    val realChapters = chapters
        .sortedBy { it.chapterNumber }
        .map { chapter ->

            ChapterData(
                id = chapter.chapterId,

                index = chapter.chapterNumber,

                title = chapter.title ?: "Không có tiêu đề",

                timestamp = chapter.createdAt
                    ?.let {
                        "($it)"
                    }
                    ?: ""
            )
        }


   // ======================================================
   // DANH SÁCH HIỂN THỊ
   // ======================================================

    val displayedChapters =
        if (isReversed) {
            realChapters.reversed()
        } else {
            realChapters
        }


    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        // ==================================================
        // BOTTOM BAR
        // ==================================================

        bottomBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 10.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .background(
                            Color(0xFF2A2A2A)
                        )
                        .padding(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ==========================================
                    // THÔNG TIN TRUYỆN
                    // ==========================================

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Tên truyện",

                            color =
                                Color.White,

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            maxLines = 1
                        )

                        Text(
                            text = "Thể loại chính",

                            color =
                                Color(0xFF9E9E9E),

                            fontSize =
                                10.sp,

                            maxLines = 1
                        )
                    }


                    // ==========================================
                    // NÚT ĐỌC
                    // ==========================================

                    Button(
                        onClick = onReadClick,

                        modifier =
                            Modifier.height(36.dp),

                        contentPadding =
                            PaddingValues(
                                horizontal = 18.dp
                            ),

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    Color.Black,

                                contentColor =
                                    Color.White
                            )
                    ) {

                        Text(
                            text = "Đọc",

                            fontSize = 12.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    // ==========================================
                    // NÚT THÊM VÀO TỦ
                    // ==========================================

                    IconButton(
                        onClick = {

                            isAddedToLibrary = !isAddedToLibrary

                            if (isAddedToLibrary) {
                                onAddToLibraryClick()
                            }
                        },

                        modifier = Modifier.size(40.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Color(0xFF1976D2)
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    if (isAddedToLibrary) {
                                        Icons.Default.Remove
                                    } else {
                                        Icons.Default.Add
                                    },

                                contentDescription =
                                    if (isAddedToLibrary) {
                                        "Bỏ khỏi tủ"
                                    } else {
                                        "Thêm vào tủ"
                                    },

                                tint = Color.White,

                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

    ) { paddingValues ->


        // ======================================================
        // DANH SÁCH CHÍNH
        // ======================================================

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF303030)
                )
                .padding(
                    paddingValues
                ),

            contentPadding =
                PaddingValues(
                    bottom = 15.dp
                )
        ) {


            // ==================================================
            // 1. HEADER
            // ==================================================

            item {

                BookHeader(
                    book = book,

                    onBack = onBack,

                    onReadClick = onReadClick,

                    onAddToLibraryClick =
                        onAddToLibraryClick
                )
            }


            // ==================================================
            // 2. THANH TAB
            // ==================================================

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF282828)
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),

                        horizontalArrangement =
                            Arrangement.SpaceEvenly,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        // --------------------------------------
                        // GIỚI THIỆU
                        // --------------------------------------

                        Text(
                            text = "Giới Thiệu",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onOverviewClick()
                                }
                        )


                        // --------------------------------------
                        // ĐÁNH GIÁ
                        // --------------------------------------

                        Text(
                            text = "Đánh Giá",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onReviewClick()
                                }
                        )


                        // --------------------------------------
                        // BÌNH LUẬN
                        // --------------------------------------

                        Text(
                            text = "Bình Luận",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onCommentClick()
                                }
                        )


                        // --------------------------------------
                        // D.S CHƯƠNG
                        // --------------------------------------

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            modifier =
                                Modifier.clickable {
                                    onChapterListClick()
                                }
                        ) {

                            Text(
                                text =
                                    "D.S Chương",

                                color =
                                    Color.White,

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(
                                        Color.White
                                    )
                            )
                        }
                    }


                    HorizontalDivider(
                        color =
                            Color(0xFF454545),

                        thickness = 1.dp
                    )
                }
            }


            // ==================================================
            // 3. SỐ CHƯƠNG + SẮP XẾP
            // ==================================================

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF303030)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 6.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ------------------------------------------
                    // SỐ CHƯƠNG
                    // ------------------------------------------

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Số chương",

                            color =
                                Color.White,

                            fontSize =
                                16.sp,

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text = "(${realChapters.size})",

                            color = Color(0xFF888888),

                            fontSize = 16.sp
                        )
                    }


                    // ------------------------------------------
                    // NÚT SẮP XẾP
                    // ------------------------------------------

                    IconButton(

                        onClick = {

                            // Đảo thứ tự danh sách
                            isReversed = !isReversed
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Sort,

                            contentDescription =
                                if (isReversed) {
                                    "Sắp xếp tăng dần"
                                } else {
                                    "Sắp xếp giảm dần"
                                },

                            tint =
                                Color.White,

                            modifier =
                                Modifier.size(24.dp)
                        )
                    }
                }
            }


            // ==================================================
            // 4. DANH SÁCH CHƯƠNG
            // ==================================================

            items(

                items = displayedChapters,

                key = {
                    it.id
                }

            ) { chapter ->

                ChapterItemView(

                    chapter = chapter,

                    isCurrentChapter =
                        chapter.index == currentChapterNumber,

                    onClick = {

                        onChapterClick(chapter)
                    }
                )
            }


            // ==================================================
            // KHOẢNG TRỐNG CUỐI
            // ==================================================

            item {

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )
            }
        }
    }
}


// ==========================================================
// ITEM TỪNG CHƯƠNG
// ==========================================================

@Composable
fun ChapterItemView(

    chapter: ChapterData,

    isCurrentChapter: Boolean = false,

    onClick: () -> Unit = {}
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()

            .clickable {
                onClick()
            }

            .background(
                if (isCurrentChapter) {
                    Color(0xFF383838)
                } else {
                    Color(0xFF303030)
                }
            )

            .padding(
                horizontal = 16.dp,
                vertical = 6.dp
            ),

        verticalAlignment =
            Alignment.Top
    ) {

        // ==================================================
        // SỐ CHƯƠNG
        // ==================================================

        Text(
            text = chapter.index.toString(),

            color =
                if (isCurrentChapter) {
                    Color.White
                } else {
                    Color(0xFF888888)
                },

            fontSize = 15.sp,

            fontWeight =
                if (isCurrentChapter) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },

            modifier =
                Modifier.width(36.dp)
        )


        // ==================================================
        // THÔNG TIN CHƯƠNG
        // ==================================================

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = chapter.title,

                color =
                    if (isCurrentChapter) {
                        Color.White
                    } else {
                        Color(0xFFD0D0D0)
                    },

                fontSize = 15.sp,

                fontWeight =
                    if (isCurrentChapter) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },

                maxLines = 2
            )

            Spacer(
                modifier =
                    Modifier.height(1.dp)
            )

            Text(
                text = chapter.timestamp,

                color =
                    Color(0xFF888888),

                fontSize = 12.sp
            )
        }
    }
}