package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch

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
// MÀN HÌNH DANH SÁCH CHƯƠNG (THEO ẢNH MẪU)
// ==========================================================

@Composable
fun ChapterList(
    book: BookResponse,
    chapters: List<ChapterResponse>,
    currentChapterNumber: Int? = null,
    onBack: () -> Unit = {},
    onReadClick: () -> Unit = {},
    onAddToLibraryClick: () -> Unit = {},
    onChapterClick: (ChapterData) -> Unit = { onReadClick() },
    onOverviewClick: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onChapterListClick: () -> Unit = {}
) {
    var isReversed by remember { mutableStateOf(false) }
    var chapterListState by remember(chapters) { mutableStateOf(chapters) }
    var activeChapterNumber by remember(currentChapterNumber) { mutableStateOf(currentChapterNumber) }
    val scope = rememberCoroutineScope()

    // Tự động lấy chương đang đọc từ lịch sử nếu chưa truyền vào
    LaunchedEffect(book.bookId) {
        if (activeChapterNumber == null) {
            try {
                val historyRes = RetrofitClient.api.getMyReadingHistory()
                if (historyRes.isSuccessful) {
                    val bookHistory = historyRes.body().orEmpty().find { it.bookId == book.bookId }
                    if (bookHistory != null) {
                        val matchedChapter = chapterListState.find { it.chapterId == bookHistory.chapterId }
                        if (matchedChapter != null) {
                            activeChapterNumber = matchedChapter.chapterNumber
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    val realChapters = chapterListState
        .sortedBy { it.chapterNumber }
        .map { chapter ->
            ChapterData(
                id = chapter.chapterId,
                index = chapter.chapterNumber,
                title = chapter.title?.takeIf { it.isNotBlank() } ?: "Không có tiêu đề",
                timestamp = formatChapterTimestamp(chapter.createdAt)
            )
        }

    val displayedChapters = if (isReversed) realChapters.reversed() else realChapters

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030))
    ) {
        // ==================================================
        // 1. TOP BAR ("<-  D.S Chương          ↻")
        // ==================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF282828))
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "D.S Chương",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            val res = RetrofitClient.api.getChaptersByBookId(book.bookId)
                            if (res.isSuccessful && res.body() != null) {
                                chapterListState = res.body()!!
                            }
                        } catch (_: Exception) {}
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Làm mới",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ==================================================
        // 2. HÀNG "Số chương (xxx)" + ICON SẮP XẾP
        // ==================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Số chương",
                    color = Color(0xFFE0E0E0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${realChapters.size})",
                    color = Color(0xFF888888),
                    fontSize = 15.sp
                )
            }

            IconButton(
                onClick = { isReversed = !isReversed }
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = if (isReversed) "Sắp xếp tăng dần" else "Sắp xếp giảm dần",
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // ==================================================
        // 3. DANH SÁCH CÁC CHƯƠNG
        // ==================================================
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(
                items = displayedChapters,
                key = { it.id }
            ) { chapter ->
                ChapterItemView(
                    chapter = chapter,
                    isCurrentChapter = chapter.index == activeChapterNumber,
                    onClick = {
                        activeChapterNumber = chapter.index
                        onChapterClick(chapter)
                    }
                )
            }
        }
    }
}

// ==========================================================
// ITEM TỪNG CHƯƠNG (SỐ THỨ TỰ + CHƯƠNG X: TIÊU ĐỀ (THỜI GIAN))
// ==========================================================

@Composable
fun ChapterItemView(
    chapter: ChapterData,
    isCurrentChapter: Boolean = false,
    onClick: () -> Unit = {}
) {
    val textColor = if (isCurrentChapter) Color.White else Color(0xFFB5B5B5)
    val indexColor = if (isCurrentChapter) Color.White else Color(0xFF8E8E8E)
    val textWeight = if (isCurrentChapter) FontWeight.Bold else FontWeight.Normal

    val displayLine = buildString {
        append("Chương ${chapter.index}: ${chapter.title}")
        if (chapter.timestamp.isNotBlank()) {
            append(" (${chapter.timestamp})")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = chapter.index.toString(),
            color = indexColor,
            fontSize = 15.sp,
            fontWeight = textWeight,
            modifier = Modifier.width(34.dp)
        )

        Text(
            text = displayLine,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = textWeight,
            lineHeight = 21.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ==========================================================
// FORMAT THỜI GIAN CHƯƠNG -> (YYYY-MM-DD HH:mm)
// ==========================================================

private fun formatChapterTimestamp(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return try {
        val clean = raw.trim().replace("T", " ")
        if (clean.length >= 16) clean.substring(0, 16) else clean
    } catch (_: Exception) {
        raw
    }
}