package com.example.metruyenchu.user_interface.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.SessionManager
import com.example.metruyenchu.home.HistoryViewModel
import com.example.metruyenchu.user_interface.library.components.LibraryStoryBottomSheet
import com.example.metruyenchu.user_interface.library.components.LibraryStoryItem
import com.example.metruyenchu.user_interface.library.components.LibraryTopBar
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    onNavigateToBookmark: () -> Unit = {},
    onNavigateToSort: () -> Unit = {},
    onBookClick: (Long) -> Unit = {},
    viewModel: HistoryViewModel = viewModel()
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val currentUserId = remember { SessionManager(context).getUserId() }

    var searchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var removedBookIds by remember { mutableStateOf(emptySet<Long>()) }

    // Truyện đang được mở BottomSheet khi bấm dấu 3 chấm
    var selectedBook by remember { mutableStateOf<BookResponse?>(null) }
    val scope = rememberCoroutineScope()

    // Lưu trạng thái bật/tắt thông báo cho từng bookId
    var notifiedBookIds by remember {
        mutableStateOf(emptySet<Long>())
    }

    // Map<bookId, Pair<số chương đang đọc, tổng số chương>>
    var chapterProgressMap by remember {
        mutableStateOf(emptyMap<Long, Pair<Int, Int>>())
    }

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    // Ánh xạ chapterId trong history sang số chương thật và tổng số chương
    LaunchedEffect(history, books) {
        if (history.isNotEmpty() && books.isNotEmpty()) {
            val resultMap = mutableMapOf<Long, Pair<Int, Int>>()
            for (book in books) {
                val h = history.firstOrNull { it.bookId == book.bookId } ?: continue
                try {
                    val res = RetrofitClient.api.getChaptersByBookId(book.bookId)
                    if (res.isSuccessful) {
                        val chapters = res.body().orEmpty()
                        val readNum = chapters.firstOrNull { it.chapterId == h.chapterId }?.chapterNumber ?: 1
                        val maxChap = chapters.maxOfOrNull { it.chapterNumber }
                            ?: book.chapterCount
                            ?: chapters.size.coerceAtLeast(1)
                        resultMap[book.bookId] = Pair(readNum, maxChap)
                    }
                } catch (_: Exception) {
                    val fallbackTotal = (book.chapterCount ?: 1).coerceAtLeast(1)
                    resultMap[book.bookId] = Pair(1, fallbackTotal)
                }
            }
            chapterProgressMap = resultMap
        }
    }

    // Lọc danh sách truyện hiển thị
    val historySort = remember { LibrarySortPrefs.getHistorySort(context) }

    val filtered = remember(books, history, searchText, removedBookIds, historySort) {
        val baseList = books.filter {
            it.bookId !in removedBookIds &&
                    (searchText.isBlank() || it.title.contains(searchText, true))
        }
        when (historySort) {
            "Tên truyện" -> baseList.sortedBy { it.title.lowercase() }
            "Chương mới" -> baseList.sortedByDescending { it.chapterCount ?: 0 }
            else -> baseList // "Mới đọc" giữ nguyên thứ tự lịch sử mới nhất từ API
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030))
    ) {
        LibraryTopBar(
            searchMode,
            searchText,
            { searchText = it },
            { searchMode = true },
            { searchText = ""; searchMode = false },
            onNavigateToSort
        )

        // Thanh Tab Lịch sử (có gạch chân trắng) | Đánh dấu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF282828))
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text(
                    text = "Lịch sử",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.width(36.dp))

            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clickable { onNavigateToBookmark() }
            ) {
                Text(
                    text = "Đánh dấu",
                    color = Color(0xFF777777),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Box(Modifier.fillMaxSize()) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(error!!, color = Color.White, modifier = Modifier.align(Alignment.Center))
                filtered.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Chưa có tủ truyện",
                            tint = Color(0xFF777777),
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chưa có tủ truyện",
                            color = Color(0xFFAAAAAA),
                            fontSize = 18.sp
                        )
                    }
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(filtered, key = { it.bookId }) { book ->
                        LibraryStoryItem(
                            title = book.title,
                            imageUrl = book.coverUrl,
                            progressText = null,
                            showNotification = false,
                            onItemClick = { onBookClick(book.bookId) },
                            onMenuClick = { selectedBook = book }
                        )
                    }
                }
            }
        }
    }

    // Hiển thị LibraryStoryBottomSheet khi bấm dấu 3 chấm
    selectedBook?.let { book ->
        val isNotified = book.bookId in notifiedBookIds

        LibraryStoryBottomSheet(
            title = book.title,
            coverUrl = book.coverUrl,
            showNotificationOption = true,
            isNotificationEnabled = isNotified,
            onToggleNotification = { enabled ->
                notifiedBookIds = if (enabled) {
                    notifiedBookIds + book.bookId
                } else {
                    notifiedBookIds - book.bookId
                }
            },
            onDismiss = { selectedBook = null },
            onDownloadClick = { fromChapter, toChapter ->
                println("Tải truyện ${book.title} từ chương $fromChapter đến $toChapter")
            },
            onDeleteClick = {
                val bookIdToRemove = book.bookId
                selectedBook = null
                removedBookIds = removedBookIds + bookIdToRemove
                scope.launch {
                    try {
                        RetrofitClient.api.removeBookFromLibrary(currentUserId, bookIdToRemove)
                        viewModel.loadHistory()
                    } catch (_: Exception) {}
                }
            }
        )
    }
}