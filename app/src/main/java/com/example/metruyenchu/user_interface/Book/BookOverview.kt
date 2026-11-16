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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.user_interface.Book.components.BookHeader



// ==========================================================
// BOOK OVERVIEW
// ==========================================================

@Composable
fun BookOverview(

    // ======================================================
    // BOOK THẬT TỪ API
    // ======================================================

    book: BookResponse,

    // ======================================================
    // BACK
    // ======================================================

    onBack: () -> Unit = {},

    // ======================================================
    // BOTTOM BAR
    // ======================================================

    onReadClick: () -> Unit = {},

    isInLibrary: Boolean = false,

    onAddToLibraryClick: () -> Unit = {},
    // ======================================================
    // TAB
    // ======================================================

    onReviewClick: () -> Unit = {},

    onCommentClick: () -> Unit = {},

    onChapterListClick: () -> Unit = {},

    // ======================================================
    // THỂ LOẠI + NHÃN
    // ======================================================

    onGenreClick: (String) -> Unit = {},

    onTagClick: (String) -> Unit = {},

    onReadChapter: (Int) -> Unit = {}

) {

    // ======================================================
    // LẤY DANH SÁCH THỂ LOẠI THẬT
    // ======================================================

    val genres =
        book.bookGenres
            .orEmpty()
            .mapNotNull {
                it.genre?.genreName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()


    // ======================================================
    // LẤY DANH SÁCH NHÃN THẬT
    // ======================================================

    val tags =
        book.bookTags
            .orEmpty()
            .mapNotNull {
                it.tag?.tagName
            }
            .filter {
                it.isNotBlank()
            }
            .distinct()


    // ======================================================
    // THỂ LOẠI CHÍNH
    // ======================================================

    val mainGenre =
        genres.firstOrNull()
            ?: "Chưa phân loại"


    // ======================================================
    // TRẠNG THÁI TỦ TRUYỆN
    // ======================================================

    var currentUserId by remember { mutableStateOf<Long?>(null) }
    var isBookmarked by remember { mutableStateOf(isInLibrary) }
    var isTogglingBookmark by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(book.bookId) {
        try {
            val userResponse = RetrofitClient.api.getCurrentUser()
            if (userResponse.isSuccessful) {
                currentUserId = userResponse.body()?.userId
            }

            // Kiểm tra truyện đã nằm trong danh sách Đánh dấu chưa
            val followsResponse = RetrofitClient.api.getMyFollows()
            if (followsResponse.isSuccessful) {
                val followList = followsResponse.body().orEmpty()
                isBookmarked = followList.any { it.bookId == book.bookId }
            }
        } catch (_: Exception) {}
    }

    // ======================================================
    // SCAFFOLD
    // ======================================================

    Scaffold(

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
                            text =
                                book.title,

                            color =
                                Color.White,

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.Bold,

                            maxLines = 1
                        )

                        Text(
                            text =
                                mainGenre,

                            color =
                                Color(0xFF9E9E9E),

                            fontSize =
                                10.sp,

                            maxLines = 1
                        )
                    }



                    // ==========================================
                    // NÚT ĐỌC -> LƯU LỊCH SỬ ĐỌC TRUYỆN
                    // ==========================================

                    Button(
                        onClick = {
                            val uid = currentUserId
                            if (uid != null) {
                                scope.launch {
                                    try {
                                        val chaptersRes = RetrofitClient.api.getChaptersByBookId(book.bookId)
                                        val firstChapterId = chaptersRes.body()?.firstOrNull()?.chapterId ?: 1L

                                        RetrofitClient.api.saveReadingHistory(
                                            userId = uid,
                                            bookId = book.bookId,
                                            chapterId = firstChapterId,
                                            progress = 0.0
                                        )
                                    } catch (_: Exception) {}
                                }
                            }
                            onReadClick()
                        },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Đọc",
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // ==========================================
                    // NÚT "+" / "-" -> THÊM / XÓA MỤC ĐÁNH DẤU
                    // ==========================================

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1976D2))
                            .clickable(enabled = !isTogglingBookmark && currentUserId != null) {
                                val uid = currentUserId ?: return@clickable
                                scope.launch {
                                    isTogglingBookmark = true
                                    try {
                                        val response = if (isBookmarked) {
                                            RetrofitClient.api.unfollowBook(
                                                userId = uid,
                                                bookId = book.bookId
                                            )
                                        } else {
                                            RetrofitClient.api.followBook(
                                                userId = uid,
                                                bookId = book.bookId
                                            )
                                        }

                                        if (response.isSuccessful) {
                                            isBookmarked = !isBookmarked
                                            onAddToLibraryClick()
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("BookOverview", "Lỗi cập nhật đánh dấu", e)
                                    } finally {
                                        isTogglingBookmark = false
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Remove else Icons.Default.Add,
                            contentDescription = if (isBookmarked) "Bỏ đánh dấu" else "Đánh dấu",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

    ) { paddingValues ->


        // ======================================================
        // NỘI DUNG CHÍNH
        // ======================================================

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF303030))
                // Chỉ lấy bottom padding của Scaffold, bỏ khoảng thừa ở trên cùng
                .padding(bottom = paddingValues.calculateBottomPadding()),

            contentPadding =
                PaddingValues(
                    bottom = 20.dp
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
                    onReadChapterClick = { chapterNumber ->
                        onReadChapter(chapterNumber)
                    },
                    onAddToLibraryClick = onAddToLibraryClick
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

                        // ==========================================
                        // GIỚI THIỆU - ĐANG CHỌN
                        // ==========================================

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    "Giới Thiệu",

                                color =
                                    Color.White,

                                fontSize =
                                    13.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(7.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(2.dp)
                                    .background(
                                        Color.White
                                    )
                            )
                        }


                        // ==========================================
                        // ĐÁNH GIÁ
                        // ==========================================

                        Text(
                            text =
                                "Đánh Giá",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onReviewClick()
                                }
                        )


                        // ==========================================
                        // BÌNH LUẬN
                        // ==========================================

                        Text(
                            text =
                                "Bình Luận",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onCommentClick()
                                }
                        )


                        // ==========================================
                        // D.S CHƯƠNG
                        // ==========================================

                        Text(
                            text =
                                "D.S Chương",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                14.sp,

                            modifier =
                                Modifier.clickable {
                                    onChapterListClick()
                                }
                        )
                    }


                    HorizontalDivider(
                        color =
                            Color(0xFF454545),

                        thickness =
                            1.dp
                    )
                }
            }


            // ==================================================
            // 3. THỐNG KÊ
            // ==================================================

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF303030)
                        )
                        .padding(
                            vertical = 14.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceEvenly
                    ) {

                        // ------------------------------------------
                        // CHƯƠNG / TUẦN
                        // ------------------------------------------

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    book.weeklyViewCount
                                        ?.toString()
                                        ?: "0",

                                color =
                                    Color.White,

                                fontSize =
                                    22.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Lượt đọc/tuần",

                                color =
                                    Color(0xFF9E9E9E),

                                fontSize =
                                    13.sp
                            )
                        }


                        // ------------------------------------------
                        // TỔNG CHƯƠNG
                        // ------------------------------------------

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    (
                                            book.chapterCount
                                                ?: 0
                                            ).toString(),

                                color =
                                    Color.White,

                                fontSize =
                                    22.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    if (
                                        book.status.equals(
                                            "COMPLETED",
                                            ignoreCase = true
                                        )
                                    ) {
                                        "Chương - Hoàn thành"
                                    } else {
                                        "Chương - Còn tiếp"
                                    },

                                color =
                                    Color(0xFF9E9E9E),

                                fontSize =
                                    13.sp
                            )
                        }


                        // ------------------------------------------
                        // LƯỢT ĐỌC
                        // ------------------------------------------

                        Column(
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    book.viewCount
                                        ?.toString()
                                        ?: "0",

                                color =
                                    Color.White,

                                fontSize =
                                    22.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Lượt đọc",

                                color =
                                    Color(0xFF9E9E9E),

                                fontSize =
                                    13.sp
                            )
                        }
                    }
                }


                HorizontalDivider(
                    color =
                        Color(0xFF454545)
                )
            }


            // ==================================================
            // 4. MÔ TẢ
            // ==================================================

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF282828)
                        )
                ) {

                    Text(
                        text =
                            book.description
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Chưa có mô tả.",

                        color =
                            Color(0xFFD0D0D0),

                        fontSize =
                            14.sp,

                        lineHeight =
                            20.sp,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                    )
                }
            }


            // ==================================================
            // 5. THỂ LOẠI + NHÃN
            // ==================================================

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF303030)
                        )
                        .padding(16.dp)
                ) {

                    // ------------------------------------------
                    // THỂ LOẠI
                    // ------------------------------------------

                    Text(
                        text =
                            "Thể loại",

                        color =
                            Color.White,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    if (genres.isNotEmpty()) {

                        LazyRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            items(
                                items = genres
                            ) { genre ->

                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            onGenreClick(genre)
                                        }
                                        .background(
                                            Color.White,
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                ) {

                                    Text(
                                        text =
                                            genre,

                                        color =
                                            Color.Black,

                                        fontSize =
                                            12.sp
                                    )
                                }
                            }
                        }

                    } else {

                        Text(
                            text =
                                "Chưa có thể loại",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                13.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // ------------------------------------------
                    // NHÃN
                    // ------------------------------------------

                    Text(
                        text =
                            "Nhãn",

                        color =
                            Color.White,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    if (tags.isNotEmpty()) {

                        LazyRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            items(
                                items = tags
                            ) { tag ->

                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            onTagClick(tag)
                                        }
                                        .background(
                                            Color.White,
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                ) {

                                    Text(
                                        text =
                                            tag,

                                        color =
                                            Color.Black,

                                        fontSize =
                                            12.sp
                                    )
                                }
                            }
                        }

                    } else {

                        Text(
                            text =
                                "Chưa có nhãn",

                            color =
                                Color(0xFF888888),

                            fontSize =
                                13.sp
                        )
                    }
                }
            }


        }
    }
}