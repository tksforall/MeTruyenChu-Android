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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metruyenchu.home.LibraryViewModel
import com.example.metruyenchu.user_interface.library.components.LibraryStoryItem
import com.example.metruyenchu.user_interface.library.components.LibraryTopBar

@Composable
fun BookmarkScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSort: () -> Unit = {},
    onBookClick: (Long) -> Unit = {},
    viewModel: LibraryViewModel = viewModel()
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var searchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { viewModel.loadLibrary() }
    val filtered = books.filter { searchText.isBlank() || it.title.contains(searchText, true) }

    Column(Modifier.fillMaxSize().background(Color(0xFF303030))) {
        LibraryTopBar(searchMode, searchText, { searchText = it }, { searchMode = true }, { searchText = ""; searchMode = false }, onNavigateToSort)
        Row(Modifier.fillMaxWidth().background(Color(0xFF282828)).padding(horizontal = 20.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("Lịch sử", color = Color(0xFF777777), modifier = Modifier.padding(end = 40.dp).clickable { onNavigateToHistory() })
            Text("Đánh dấu", color = Color.White)
        }
        Box(Modifier.fillMaxSize()) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(error!!, color = Color.White, modifier = Modifier.align(Alignment.Center))
                filtered.isEmpty() -> Text("Chưa có truyện trong tủ", color = Color(0xFFAAAAAA), modifier = Modifier.align(Alignment.Center))
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(filtered, key = { it.bookId }) { book ->
                        LibraryStoryItem(
                            title = book.title,
                            imageUrl = book.coverUrl,
                            progressText = "${book.chapterCount ?: 0} chương • ${book.status ?: "Chưa rõ"}",
                            showNotification = false,
                            onItemClick = { onBookClick(book.bookId) }
                        )
                    }
                }
            }
        }
    }
}
