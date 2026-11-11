package com.example.metruyenchu.user_interface.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.metruyenchu.viewmodel.LatestBooksViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.metruyenchu.user_interface.explore.components.BookFilter
import com.example.metruyenchu.user_interface.explore.components.BookListItem
import com.example.metruyenchu.user_interface.explore.components.FilterBottomSheet

@Composable
fun LastestBooksScreen(
    onBack: () -> Unit = {},
    onApplyFilter: (BookFilter) -> Unit = {},
    onNavigateToListOfStory: () -> Unit = {},
    onBookClick: (Long) -> Unit = {}
) {

    // =========================================================
    // VIEWMODEL
    // =========================================================

    val viewModel: LatestBooksViewModel = viewModel()

    val books by viewModel.books.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBooks()
    }


    // =========================================================
    // TRẠNG THÁI FILTER BOTTOM SHEET
    // =========================================================

    var showFilterSheet by remember {
        mutableStateOf(false)
    }


    // =========================================================
    // GIAO DIỆN
    // =========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {

        // =========================
        // HEADER
        // =========================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {

            // Nút quay lại
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White,
                    modifier = Modifier.size(29.dp)
                )
            }


            // Tiêu đề
            Text(
                text = "Mới Nhất",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )


            // Nút bộ lọc
            IconButton(
                onClick = {
                    showFilterSheet = true
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Bộ lọc",
                    tint = Color.White,
                    modifier = Modifier.size(29.dp)
                )
            }
        }


        // =========================
        // ĐƯỜNG PHÂN CÁCH
        // =========================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF383838))
        )


        // =========================
        // DANH SÁCH TRUYỆN
        // =========================

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),

            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 15.dp
            ),

            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            items(books) { book ->                BookListItem(
                    book = book,
                    onClick = {
                        onBookClick(book.bookId)
                    }
                )
            }
        }
    }


    // =========================================================
    // FILTER BOTTOM SHEET
    // =========================================================

    if (showFilterSheet) {

        FilterBottomSheet(

            onDismiss = {
                showFilterSheet = false
            },

            onApply = { filter ->
                onApplyFilter(filter)
            },

            onNavigateToListOfStory = {
                showFilterSheet = false
                onNavigateToListOfStory()
            }
        )
    }
}