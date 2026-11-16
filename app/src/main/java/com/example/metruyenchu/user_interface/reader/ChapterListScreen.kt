package com.example.metruyenchu.user_interface.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.repository.ChapterRepository
import kotlinx.coroutines.launch

@Composable
fun ChapterListScreen(
    bookId: Long,
    currentChapterNumber: Int = 1,
    onBack: () -> Unit = {},
    onChapterClick: (Int) -> Unit = {}
) {
    val repository = remember { ChapterRepository() }
    var chapters by remember { mutableStateOf<List<ChapterResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var descending by remember { mutableStateOf(false) }
    var activeChapterNumber by remember(currentChapterNumber) { mutableStateOf(currentChapterNumber) }
    val scope = rememberCoroutineScope()

    fun load() = scope.launch {
        loading = true
        error = null
        repository.getChaptersByBookId(bookId)
            .onSuccess { list ->
                chapters = list.sortedBy { c -> c.chapterNumber }

                // Lấy chương đang đọc từ lịch sử đọc của user nếu có
                try {
                    val historyRes = RetrofitClient.api.getMyReadingHistory()
                    if (historyRes.isSuccessful) {
                        val bookHistory = historyRes.body().orEmpty().find { it.bookId == bookId }
                        if (bookHistory != null) {
                            val matched = chapters.find { it.chapterId == bookHistory.chapterId }
                            if (matched != null) {
                                activeChapterNumber = matched.chapterNumber
                            }
                        }
                    }
                } catch (_: Exception) {}
            }
            .onFailure { error = it.message ?: "Không thể tải danh sách chương" }
        loading = false
    }

    LaunchedEffect(bookId) { load() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030))
    ) {
        // ==================================================
        // 1. TOP BAR GỌN GÀNG ("<-  D.S Chương          ↻")
        // ==================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF282828))
                .height(48.dp)
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

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "D.S Chương",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { load() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Làm mới",
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }
        }

        // ==================================================
        // 2. NỘI DUNG
        // ==================================================
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error!!, color = Color.White)
                }
            }

            else -> {
                // HÀNG "Số chương (xxx)" + ICON SẮP XẾP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 12.dp, top = 6.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Số chương",
                        color = Color(0xFFE0E0E0),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${chapters.size})",
                        color = Color(0xFF888888),
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { descending = !descending }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = if (descending) "Sắp xếp tăng dần" else "Sắp xếp giảm dần",
                            tint = Color(0xFFE0E0E0),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // DANH SÁCH CHƯƠNG
                val displayedList = if (descending) chapters.asReversed() else chapters

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(
                        items = displayedList,
                        key = { it.chapterId }
                    ) { chapter ->
                        val isCurrent = chapter.chapterNumber == activeChapterNumber
                        val textColor = if (isCurrent) Color.White else Color(0xFFB5B5B5)
                        val indexColor = if (isCurrent) Color.White else Color(0xFF8E8E8E)
                        val fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

                        val titleText = chapter.title?.takeIf { it.isNotBlank() } ?: "Không có tiêu đề"
                        val timeText = formatChapterTime(chapter.createdAt ?: chapter.updatedAt)
                        val displayLine = "Chương ${chapter.chapterNumber}: $titleText\n($timeText)"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeChapterNumber = chapter.chapterNumber
                                    onChapterClick(chapter.chapterNumber)
                                }
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = chapter.chapterNumber.toString(),
                                color = indexColor,
                                fontSize = 15.sp,
                                fontWeight = fontWeight,
                                modifier = Modifier.width(30.dp)
                            )

                            Text(
                                text = displayLine,
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = fontWeight,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatChapterTime(raw: String?): String {
    if (raw.isNullOrBlank()) return "2026-05-01 12:00"
    return try {
        val clean = raw.trim().replace("T", " ")
        if (clean.length >= 16) clean.substring(0, 16) else clean
    } catch (_: Exception) {
        raw
    }
}