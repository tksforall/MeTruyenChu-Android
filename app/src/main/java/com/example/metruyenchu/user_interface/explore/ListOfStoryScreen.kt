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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.home.ListOfStoryViewModel
import com.example.metruyenchu.user_interface.explore.components.BookFilter
import com.example.metruyenchu.user_interface.explore.components.BookListItem
import com.example.metruyenchu.user_interface.explore.components.FilterBottomSheet


@Composable
fun ListOfStoryScreen(
    filter: BookFilter,
    onBack: () -> Unit = {},
    onBookClick: (Long) -> Unit = {},
    viewModel: ListOfStoryViewModel = viewModel()
) {

    // ==================================================
    // FILTER HIỆN TẠI
    // ==================================================

    var currentFilter by remember {
        mutableStateOf(filter)
    }


    // ==================================================
    // HIỆN / ẨN FILTER
    // ==================================================

    var showFilterSheet by remember {
        mutableStateOf(false)
    }


    // ==================================================
    // DATA
    // ==================================================

    val books by viewModel.books.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading
        .collectAsStateWithLifecycle()

    val error by viewModel.error
        .collectAsStateWithLifecycle()


    // ==================================================
    // GỌI API MỖI KHI FILTER THAY ĐỔI
    // ==================================================

    LaunchedEffect(currentFilter) {

        viewModel.loadBooks(currentFilter)
    }


    // ==================================================
    // CONVERT API MODEL -> UI MODEL
    // ==================================================

    val bookItems = remember(books) {

        books.map {
            it.toBookListItemData()
        }
    }


    // ==================================================
    // UI
    // ==================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF292929))
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {

            // BACK

            IconButton(
                onClick = onBack,
                modifier = Modifier.align(
                    Alignment.CenterStart
                )
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription = "Quay lại",

                    tint = Color.White,

                    modifier = Modifier.size(29.dp)
                )
            }


            // TITLE

            Text(
                text = "Danh sách truyện",

                color = Color.White,

                fontSize = 23.sp,

                fontWeight = FontWeight.Bold,

                modifier = Modifier.align(
                    Alignment.Center
                )
            )


            // FILTER

            IconButton(
                onClick = {
                    showFilterSheet = true
                },

                modifier = Modifier.align(
                    Alignment.CenterEnd
                )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FilterList,

                    contentDescription = "Bộ lọc",

                    tint = Color.White,

                    modifier = Modifier.size(29.dp)
                )
            }
        }


        // ==================================================
        // LINE
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Color(0xFF383838)
                )
        )


        // ==================================================
        // LOADING
        // ==================================================

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.White
                )
            }
        }


        // ==================================================
        // ERROR
        // ==================================================

        else if (error != null) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = error
                        ?: "Có lỗi xảy ra",

                    color = Color(0xFFAAAAAA),

                    fontSize = 14.sp
                )
            }
        }


        // ==================================================
        // KHÔNG CÓ TRUYỆN
        // ==================================================

        else if (bookItems.isEmpty()) {

            Column(
                modifier = Modifier.fillMaxSize(),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.SentimentDissatisfied,

                    contentDescription =
                        "Không có truyện",

                    tint = Color(0xFF777777),

                    modifier = Modifier.size(70.dp)
                )

                Text(
                    text = "Không tìm thấy truyện",

                    color = Color(0xFF999999),

                    fontSize = 16.sp,

                    modifier = Modifier.padding(
                        top = 14.dp
                    )
                )
            }
        }


        // ==================================================
        // DANH SÁCH
        // ==================================================

        else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 8.dp,
                    bottom = 15.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(5.dp)
            ) {

                items(
                    items = bookItems,

                    key = { book ->
                        "${book.title}_${book.author}"
                    }

                ) { book ->

                    BookListItem(
                        book = book,

                        onClick = {
                            onBookClick(book.bookId)
                        }
                    )
                }
            }
        }
    }


    // ==================================================
    // FILTER BOTTOM SHEET
    // ==================================================

    if (showFilterSheet) {

        FilterBottomSheet(

            initialFilter = currentFilter,

            onDismiss = {
                showFilterSheet = false
            },

            onApply = { newFilter ->

                // Chỉ thay filter.
                // LaunchedEffect(currentFilter)
                // sẽ tự gọi API lại.

                currentFilter = newFilter
            }
        )
    }
}