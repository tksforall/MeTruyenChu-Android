package com.example.metruyenchu.user_interface.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.home.SearchViewModel
import com.example.metruyenchu.user_interface.explore.components.BookFilter
import com.example.metruyenchu.user_interface.explore.components.BookListItem
import com.example.metruyenchu.user_interface.explore.components.FilterBottomSheet
import com.example.metruyenchu.user_interface.explore.components.toRequest
import kotlinx.coroutines.launch


@Composable
fun SearchScreen(
    onBack: () -> Unit = {}
) {

    // ======================================================
    // VIEW MODEL
    // ======================================================

    val viewModel: SearchViewModel = viewModel()

    val books by viewModel.books.collectAsState()


    // ======================================================
    // SEARCH STATE
    // ======================================================

    var searchText by remember {
        mutableStateOf("")
    }

    var isSearchActive by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // FOCUS
    // ======================================================

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(isSearchActive) {

        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }


    // ======================================================
    // FILTER STATE
    // ======================================================

    var showFilter by remember {
        mutableStateOf(false)
    }

    var currentFilter by remember {
        mutableStateOf(BookFilter())
    }


    // ======================================================
    // FILTER RESULT
    //
    // Chỉ lưu ID những truyện thỏa mãn filter.
    // Không thay đổi kết quả search gốc.
    // ======================================================

    var filteredBookIds by remember {
        mutableStateOf<Set<Long>?>(null)
    }

    var isFilterLoading by remember {
        mutableStateOf(false)
    }


    // ======================================================
    // COROUTINE
    // ======================================================

    val scope = rememberCoroutineScope()


    // ======================================================
    // KIỂM TRA FILTER CÓ ĐƯỢC CHỌN HAY CHƯA
    // ======================================================

    fun hasActiveFilter(filter: BookFilter): Boolean {

        return filter.sort.isNotBlank() ||
                filter.status.isNotBlank() ||
                filter.chapterRange.isNotBlank() ||
                filter.publishDate.isNotBlank() ||
                filter.genres.isNotEmpty() ||
                filter.personalities.isNotEmpty() ||
                filter.worlds.isNotEmpty() ||
                filter.tags.isNotEmpty()
    }


    // ======================================================
    // APPLY FILTER
    // ======================================================

    fun applyFilter(filter: BookFilter) {

        currentFilter = filter

        // --------------------------------------------------
        // KHÔNG CÓ FILTER
        // --------------------------------------------------

        if (!hasActiveFilter(filter)) {

            filteredBookIds = null

            isFilterLoading = false

            return
        }


        // --------------------------------------------------
        // CÓ FILTER
        // --------------------------------------------------

        scope.launch {

            isFilterLoading = true

            try {

                val response =
                    RetrofitClient.api.filterBooks(
                        filter.toRequest()
                    )


                if (response.isSuccessful) {

                    val filterBooks =
                        response.body().orEmpty()


                    // --------------------------------------
                    // Lấy ID các truyện sau khi filter
                    // --------------------------------------

                    filteredBookIds =
                        filterBooks
                            .map {
                                it.bookId
                            }
                            .toSet()

                } else {

                    filteredBookIds =
                        emptySet()

                    println(
                        "SEARCH FILTER FAILED = ${response.code()}"
                    )

                    println(
                        "ERROR = ${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {

                filteredBookIds =
                    emptySet()

                println(
                    "SEARCH FILTER EXCEPTION = ${e.message}"
                )

                e.printStackTrace()

            } finally {

                isFilterLoading = false
            }
        }
    }


    // ======================================================
    // KHI SEARCH THAY ĐỔI
    //
    // Không reset filter.
    // Kết quả search mới sẽ tự động được giao với filter
    // đang chọn.
    // ======================================================

    LaunchedEffect(searchText) {

        if (searchText.isBlank()) {

            filteredBookIds = null
        }
    }


    // ======================================================
    // KẾT QUẢ HIỂN THỊ
    // ======================================================

    val filteredBooks =
        if (filteredBookIds == null) {

            books

        } else {

            books.filter { book ->

                book.bookId in filteredBookIds!!
            }
        }


    // ======================================================
    // UI
    // ======================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF292929)
            )
    ) {


        // ==================================================
        // TOP BAR
        // ==================================================

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {


            // =================================================
            // BACK
            // =================================================

            IconButton(
                onClick = onBack,

                modifier = Modifier.align(
                    Alignment.CenterStart
                )
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,

                    contentDescription =
                        "Quay lại",

                    tint = Color.White,

                    modifier = Modifier.size(30.dp)
                )
            }


            // =================================================
            // CHƯA SEARCH
            // =================================================

            if (!isSearchActive) {

                Text(
                    text = "Tìm",

                    color = Color.White,

                    fontSize = 23.sp,

                    fontWeight = FontWeight.Medium,

                    modifier = Modifier
                        .align(
                            Alignment.CenterStart
                        )
                        .padding(
                            start = 68.dp
                        )
                        .clickable {

                            isSearchActive = true
                        }
                )


                IconButton(
                    onClick = {

                        isSearchActive = true
                    },

                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .padding(
                            end = 58.dp
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            "Tìm kiếm",

                        tint = Color.White,

                        modifier = Modifier.size(30.dp)
                    )
                }
            }


            // =================================================
            // ĐANG SEARCH
            // =================================================

            if (isSearchActive) {

                BasicTextField(

                    value = searchText,

                    onValueChange = {

                        searchText = it

                        viewModel.search(it)
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 62.dp,
                            end = 92.dp
                        )
                        .align(
                            Alignment.Center
                        )
                        .focusRequester(
                            focusRequester
                        ),

                    singleLine = true,

                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    ),

                    decorationBox = {
                            innerTextField ->

                        Box(
                            modifier =
                                Modifier.fillMaxWidth(),

                            contentAlignment =
                                Alignment.CenterStart
                        ) {

                            if (
                                searchText.isEmpty()
                            ) {

                                Text(
                                    text = "Tìm",

                                    color =
                                        Color(0xFF777777),

                                    fontSize = 20.sp
                                )
                            }

                            innerTextField()
                        }
                    }
                )


                IconButton(

                    onClick = {

                        searchText = ""

                        viewModel.clearSearch()

                        // Xóa filter kết quả
                        filteredBookIds = null

                        // Reset filter
                        currentFilter = BookFilter()

                        isSearchActive = false
                    },

                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .padding(
                            end = 58.dp
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Close,

                        contentDescription =
                            "Xóa tìm kiếm",

                        tint = Color.White,

                        modifier = Modifier.size(28.dp)
                    )
                }
            }


            // =================================================
            // FILTER
            // =================================================

            IconButton(

                onClick = {

                    showFilter = true
                },

                modifier = Modifier.align(
                    Alignment.CenterEnd
                )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FilterList,

                    contentDescription =
                        "Bộ lọc",

                    tint = Color.White,

                    modifier = Modifier.size(30.dp)
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
                    Color(0xFF3A3A3A)
                )
        )


        // ==================================================
        // LOADING FILTER
        // ==================================================

        if (isFilterLoading) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = Color.White
                )
            }
        }


        // ==================================================
        // CONTENT
        // ==================================================

        else if (searchText.isBlank()) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        "Nhập tên truyện hoặc tên tác giả để tìm kiếm",

                    color =
                        Color(0xFF777777),

                    fontSize = 14.sp
                )
            }

        }


        // ==================================================
        // KHÔNG CÓ KẾT QUẢ
        // ==================================================

        else if (filteredBooks.isEmpty()) {

            Column(
                modifier =
                    Modifier.fillMaxSize(),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.SentimentDissatisfied,

                    contentDescription =
                        "Không tìm thấy",

                    tint =
                        Color(0xFF777777),

                    modifier =
                        Modifier.size(70.dp)
                )

                Box(
                    modifier =
                        Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Không tìm thấy",

                    color =
                        Color(0xFF999999),

                    fontSize = 16.sp
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

                contentPadding =
                    PaddingValues(
                        start = 14.dp,
                        end = 14.dp,
                        top = 10.dp,
                        bottom = 15.dp
                    ),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)

            ) {

                items(

                    items = filteredBooks,

                    key = { book ->
                        book.bookId
                    }

                ) { book ->

                    BookListItem(

                        highlightKeyword =
                            searchText,

                        book = book
                    )
                }
            }
        }
    }


    // ======================================================
    // FILTER BOTTOM SHEET
    // ======================================================

    if (showFilter) {

        FilterBottomSheet(

            initialFilter = currentFilter,

            onDismiss = {

                showFilter = false
            },

            onApply = { filter ->

                showFilter = false

                applyFilter(filter)
            }
        )
    }
}