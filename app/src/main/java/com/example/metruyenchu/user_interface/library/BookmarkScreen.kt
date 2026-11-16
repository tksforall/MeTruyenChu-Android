package com.example.metruyenchu.user_interface.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.metruyenchu.home.LibraryViewModel
import com.example.metruyenchu.user_interface.library.components.LibraryStoryBottomSheet
import com.example.metruyenchu.user_interface.library.components.LibraryStoryItem
import com.example.metruyenchu.user_interface.library.components.LibraryTopBar
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon

@Composable
fun BookmarkScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSort: () -> Unit = {},
    onBookClick: (Long) -> Unit = {},
    viewModel: LibraryViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = remember { SessionManager(context).getUserId() }

    val books by viewModel.books.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var searchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Truyện đang mở BottomSheet khi bấm dấu 3 chấm
    var selectedBook by remember { mutableStateOf<BookResponse?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadLibrary()
    }

    val bookmarkSort = remember { LibrarySortPrefs.getBookmarkSort(context) }

    val filtered = remember(books, searchText, bookmarkSort) {
        val baseList = books.filter {
            searchText.isBlank() || it.title.contains(searchText, true)
        }
        when (bookmarkSort) {
            "Tên truyện" -> baseList.sortedBy { it.title.lowercase() }
            "Chương mới" -> baseList.sortedByDescending { it.chapterCount ?: 0 }
            else -> baseList // "Mới lưu" giữ nguyên thứ tự lưu mới nhất từ API
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

        // Thanh Tab: Lịch sử (xám) | Đánh dấu (trắng + gạch chân trắng)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF282828))
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clickable { onNavigateToHistory() }
            ) {
                Text(
                    text = "Lịch sử",
                    color = Color(0xFF777777),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Spacer(modifier = Modifier.width(36.dp))

            Column(
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text(
                    text = "Đánh dấu",
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

    // BottomSheet khi bấm dấu 3 chấm (ẩn tùy chọn Nhận thông báo)
    selectedBook?.let { book ->
        LibraryStoryBottomSheet(
            title = book.title,
            coverUrl = book.coverUrl,
            showNotificationOption = false,
            onDismiss = { selectedBook = null },
            onDownloadClick = { fromChapter, toChapter ->
                println("Tải truyện ${book.title} từ chương $fromChapter đến $toChapter")
            },
            onDeleteClick = {
                val bookIdToRemove = book.bookId
                selectedBook = null
                scope.launch {
                    try {
                        val res = RetrofitClient.api.removeBookFromLibrary(currentUserId, bookIdToRemove)
                        if (res.isSuccessful) {
                            viewModel.loadLibrary()
                        }
                    } catch (_: Exception) {}
                }
            }
        )
    }
}