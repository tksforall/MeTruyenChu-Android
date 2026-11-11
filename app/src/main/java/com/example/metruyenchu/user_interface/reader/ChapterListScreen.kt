package com.example.metruyenchu.user_interface.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ChapterResponse
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
    val scope = rememberCoroutineScope()

    fun load() = scope.launch {
        loading = true; error = null
        repository.getChaptersByBookId(bookId)
            .onSuccess { chapters = it.sortedBy { c -> c.chapterNumber } }
            .onFailure { error = it.message ?: "Không thể tải danh sách chương" }
        loading = false
    }
    LaunchedEffect(bookId) { load() }

    Column(Modifier.fillMaxSize().background(Color(0xFF303030))) {
        Row(
            Modifier.fillMaxWidth().background(Color(0xFF292929)).height(54.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Quay lại", tint = Color.White) }
            Text("D.S Chương", color = Color.White, fontSize = 21.sp, modifier = Modifier.weight(1f))
            IconButton(onClick = { load() }) { Icon(Icons.Default.Refresh, "Làm mới", tint = Color.White) }
        }
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error!!, color = Color.White) }
            else -> {
                Row(
                    Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Số chương (${chapters.size})", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text(if (descending) "Mới → cũ" else "Cũ → mới", color = Color.LightGray, modifier = Modifier.clickable { descending = !descending })
                }
                LazyColumn(Modifier.fillMaxSize()) {
                    items(if (descending) chapters.asReversed() else chapters, key = { it.chapterId }) { chapter ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onChapterClick(chapter.chapterNumber) }.padding(horizontal = 32.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Chương ${chapter.chapterNumber}", color = if (chapter.chapterNumber == currentChapterNumber) Color.White else Color(0xFFD0D0D0), fontSize = 16.sp, fontWeight = if (chapter.chapterNumber == currentChapterNumber) FontWeight.Bold else FontWeight.Normal)
                            Spacer(Modifier.width(12.dp))
                            Text(chapter.title.orEmpty().ifBlank { "Không có tiêu đề" }, color = Color(0xFF9E9E9E), maxLines = 1, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
