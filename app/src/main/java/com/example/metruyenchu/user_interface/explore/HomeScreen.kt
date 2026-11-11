package com.example.metruyenchu.user_interface.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.example.metruyenchu.user_interface.explore.components.BookListItemData


// ==========================================================
// HOME SCREEN
// ==========================================================

@Composable
fun HomeScreen(
    onNotificationClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onListOfStoryClick: () -> Unit = {},

    // Các section
    onLatestBooksClick: () -> Unit = {},
    onRecommendedBooksClick: () -> Unit = {},
    onNewReleasedBooksClick: () -> Unit = {},
    onCompletedBooksClick: () -> Unit = {},

    // Mở BookOverview
    onBookClick: (Long) -> Unit = {},

    // Mở ReaderScreen
    onReadClick: (Long) -> Unit = {},

    // Dữ liệu Mới nhất lấy từ API
    latestBooks: List<BookListItemData> = emptyList(),

    // Dữ liệu các section khác
    recommendedBooks: List<BookListItemData> = emptyList(),
    newReleasedBooks: List<BookListItemData> = emptyList(),
    completedBooks: List<BookListItemData> = emptyList()
) {

    // ==========================================================
    // TRUYỆN ĐANG ĐƯỢC CHỌN
    // ==========================================================

    var selectedLatestBookIndex by remember {
        mutableStateOf(0)
    }

    // Nếu API thay đổi số lượng truyện và index cũ không còn hợp lệ
    // thì sử dụng index 0.
    val safeSelectedIndex =
        if (
            latestBooks.isNotEmpty() &&
            selectedLatestBookIndex in latestBooks.indices
        ) {
            selectedLatestBookIndex
        } else {
            0
        }


    // ==========================================================
    // ROOT
    // ==========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202020))
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF202020)),

            contentPadding = PaddingValues(
                bottom = 16.dp
            )
        ) {


            // ==================================================
            // TOP BAR
            // ==================================================

            item {

                TopBar(
                    onNotificationClick = onNotificationClick,
                    onAiClick = onAiClick,
                    onSearchClick = onSearchClick,
                    onListOfStoryClick = onListOfStoryClick
                )
            }


            // ==================================================
            // BANNER
            // ==================================================

            item {

                BannerSection(
                    books = recommendedBooks,
                    onBookClick = { bookId ->
                        onBookClick(bookId)
                    }
                )
            }


            // ==================================================
            // MỚI NHẤT
            // ==================================================


            item {

                SectionTitle(
                    title = "Mới nhất",
                    onMoreClick = onLatestBooksClick
                )
            }


            // ==================================================
            // 6 TRUYỆN MỚI NHẤT
            // ==================================================


            item {

                if (latestBooks.isNotEmpty()) {

                    LatestSmallBooks(
                        books = latestBooks,
                        selectedIndex = safeSelectedIndex,

                        onBookSelected = { index ->
                            selectedLatestBookIndex = index
                        },

                        onBookClick = { bookId ->
                            onBookClick(bookId)
                        }
                    )

                } else {

                    // API chưa trả dữ liệu
                    // nhưng vẫn giữ giao diện cũ

                    LatestSmallBooksPlaceholder()
                }
            }

            // ==================================================
            // TRUYỆN ĐANG ĐƯỢC CHỌN
            // ==================================================


            item {

                if (latestBooks.isNotEmpty()) {

                    LatestMainBook(
                        book = latestBooks[safeSelectedIndex],

                        onBookClick = {
                            onBookClick(
                                latestBooks[safeSelectedIndex].bookId
                            )
                        },

                        onReadClick = {
                            onReadClick(
                                latestBooks[safeSelectedIndex].bookId
                            )
                        }
                    )

                } else {

                    // API chưa trả dữ liệu
                    // vẫn hiển thị đúng layout cũ

                    LatestMainBookPlaceholder()
                }
            }


            // ==================================================
            // ĐỀ CỬ
            // ==================================================

            item {

                SectionTitle(
                    title = "Đề cử",
                    onMoreClick = onRecommendedBooksClick
                )
            }


            item {

                RecommendedBookGrid(
                    books = recommendedBooks,
                    onBookClick = { bookId ->
                        onBookClick(bookId)
                    }
                )
            }


            // ==================================================
            // MỚI ĐĂNG
            // ==================================================

            item {

                SectionTitle(
                    title = "Mới đăng",
                    onMoreClick = onNewReleasedBooksClick
                )
            }


            item {

                NewReleasedBookRow(
                    books = newReleasedBooks,
                    onBookClick = { bookId ->
                        onBookClick(bookId)
                    }
                )
            }


            // ==================================================
            // MỚI HOÀN THÀNH
            // ==================================================

            item {

                SectionTitle(
                    title = "Mới hoàn thành",
                    onMoreClick = onCompletedBooksClick
                )
            }


            item {

                CompletedBookList(
                    books = completedBooks,
                    onBookClick = { bookId ->
                        onBookClick(bookId)
                    }
                )
            }
        }
    }
}


// ==========================================================
// TOP BAR
// ==========================================================

@Composable
fun TopBar(
    onNotificationClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onListOfStoryClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 14.dp,
                end = 14.dp,
                top = 12.dp,
                bottom = 8.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // THÔNG BÁO

        IconButton(
            onClick = onNotificationClick
        ) {

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Thông báo",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(8.dp)
        )


        // AI

        Text(
            text = "AI",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,

            modifier = Modifier
                .weight(1f)
                .clickable {

                    onAiClick()
                }
        )


        // ==================================================
        // KÍNH LÚP
        // ==================================================

        Text(
            text = "⌕",
            color = Color.White,
            fontSize = 28.sp,

            modifier = Modifier.clickable {

                onSearchClick()
            }
        )


        Spacer(
            modifier = Modifier.width(18.dp)
        )


        // ==================================================
        // MENU 3 GẠCH
        // ==================================================

        Text(
            text = "☰",
            color = Color.White,
            fontSize = 22.sp,

            modifier = Modifier.clickable {

                onListOfStoryClick()
            }
        )
    }
}


// ==========================================================
// BANNER
// ==========================================================

@Composable
fun BannerSection(
    books: List<BookListItemData>,
    onBookClick: (Long) -> Unit = {}
) {

    val bannerBooks = books.take(6)

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val selectedIndex by androidx.compose.runtime.remember {
        androidx.compose.runtime.derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // ==================================================
        // BANNER
        // ==================================================

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(
                lazyListState = listState
            )
        ) {

            items(bannerBooks) { book ->

                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(145.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            onBookClick(book.bookId)
                        }
                ) {

                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        // ==================================================
        // DẤU CHUYỂN BANNER
        // ==================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            bannerBooks.forEachIndexed { index, _ ->

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(
                            width = if (index == selectedIndex) {
                                20.dp
                            } else {
                                8.dp
                            },
                            height = 3.dp
                        )
                        .background(
                            if (index == selectedIndex) {
                                Color.White
                            } else {
                                Color.Gray
                            }
                        )
                )
            }
        }
    }
}


// ==========================================================
// SECTION TITLE
// ==========================================================

@Composable
fun SectionTitle(
    title: String,
    onMoreClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 14.dp,
                end = 10.dp,
                top = 18.dp,
                bottom = 8.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,

            modifier = Modifier.weight(1f)
        )


        Text(
            text = "›",
            color = Color.White,
            fontSize = 28.sp,

            modifier = Modifier.clickable {

                onMoreClick()
            }
        )
    }
}


// ==========================================================
// MỚI NHẤT - 6 ẢNH NHỎ
// ==========================================================

@Composable
fun LatestSmallBooks(
    books: List<BookListItemData>,
    selectedIndex: Int,
    onBookSelected: (Int) -> Unit,
    onBookClick: (Long) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),

        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        books
            .take(6)
            .forEachIndexed { index, book ->

                AsyncImage(
                    model = book.imageUrl,

                    contentDescription = book.title,

                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.68f)
                        .clip(
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {

                            onBookSelected(index)
                        }
                )
            }
    }
}


// ==========================================================
// TRUYỆN ĐANG ĐƯỢC CHỌN
// ==========================================================

@Composable
fun LatestMainBook(
    book: BookListItemData,
    onBookClick: () -> Unit = {},
    onReadClick: () -> Unit = {}
) {

    var isBookmarked by remember(book.bookId) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            )
            .clickable {

                onBookClick()
            },

        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF292929)
                )
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // THỂ LOẠI
                Text(
                    text = book.genre,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                // TÊN TRUYỆN
                Text(
                    text = book.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                // GIỚI THIỆU
                Text(
                    text = book.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // RATING
                Text(
                    text = "★★★★★  ${book.rating}",
                    color = Color(0xFFFFC107),
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row {

                    Button(
                        onClick = {
                            onReadClick()
                        },
                        modifier = Modifier.height(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Đọc")
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Button(
                        onClick = {
                            isBookmarked = !isBookmarked
                        },
                        modifier = Modifier.height(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isBookmarked) {
                                "−"
                            } else {
                                "+"
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            AsyncImage(
                model = book.imageUrl,

                contentDescription = book.title,

                contentScale = ContentScale.Crop,

                modifier = Modifier
                    .size(
                        width = 105.dp,
                        height = 145.dp
                    )
                    .clip(
                        RoundedCornerShape(7.dp)
                    )
            )
        }
    }
}
// ==========================================================
// PLACEHOLDER CHO TRUYỆN ĐANG ĐƯỢC CHỌN
// ==========================================================

@Composable
fun LatestMainBookPlaceholder() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),

        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF292929)
                )
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Đang cập nhật",
                    color = Color.Gray,
                    fontSize = 12.sp
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                Text(
                    text = "Chưa có dữ liệu truyện",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )


                Spacer(
                    modifier = Modifier.height(5.dp)
                )


                Text(
                    text = "Đang tải dữ liệu...",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }


            Spacer(
                modifier = Modifier.width(10.dp)
            )


            Box(
                modifier = Modifier
                    .size(
                        width = 105.dp,
                        height = 145.dp
                    )
                    .clip(
                        RoundedCornerShape(7.dp)
                    )
                    .background(
                        Color(0xFF454545)
                    )
            )
        }
    }
}


// ==========================================================
// ĐỀ CỬ
// ==========================================================

@Composable
fun RecommendedBookGrid(
    books: List<BookListItemData>,
    onBookClick: (Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {

        val displayBooks = books.take(6)

        displayBooks
            .chunked(3)
            .forEach { rowBooks ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    rowBooks.forEach { book ->

                        SimpleBookCard(
                            book = book,
                            onBookClick = {
                                onBookClick(book.bookId)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Nếu hàng cuối thiếu sách thì giữ khoảng trống
                    repeat(3 - rowBooks.size) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }

                if (rowBooks != displayBooks.chunked(3).last()) {
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
    }
}

// ==========================================================
// MỚI ĐĂNG
// ==========================================================

@Composable
fun NewReleasedBookRow(
    books: List<BookListItemData>,
    onBookClick: (Long) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),

        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        books
            .take(4)
            .forEach { book ->

                SimpleBookCard(
                    book = book,
                    onBookClick = {
                        onBookClick(book.bookId)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
    }
}

// ==========================================================
// CARD TRUYỆN ĐƠN GIẢN
// ==========================================================

@Composable
fun SimpleBookCard(
    book: BookListItemData,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clickable {
                onBookClick()
            }
    ) {

        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(
                    RoundedCornerShape(7.dp)
                )
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = book.genre,
            color = Color.Gray,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = book.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}


// ==========================================================
// BOOK COVER
// ==========================================================

@Composable
fun BookCover(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(7.dp)
            )
            .background(
                Color(0xFF555555)
            ),

        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "ẢNH\nTRUYỆN",
            color = Color.LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ==========================================================
// DANH SÁCH TRUYỆN NGANG
// ==========================================================

@Composable
fun HorizontalBookSection(
    books: List<String>
) {

    LazyRow(
        contentPadding = PaddingValues(
            horizontal = 10.dp
        ),

        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(books) { book ->

            SmallBookCard(
                title = book
            )
        }
    }
}


// ==========================================================
// CARD TRUYỆN NHỎ
// ==========================================================

@Composable
fun SmallBookCard(
    title: String
) {

    Column(
        modifier = Modifier
            .width(105.dp)
            .clickable {

                // Chỗ này hiện chưa có callback.
                // Giữ nguyên vì function này chưa được dùng
                // cho các section chính.
            }
    ) {

        BookCover(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
        )


        Spacer(
            modifier = Modifier.height(5.dp)
        )


        Text(
            text = "Thể Loại",
            color = Color.Gray,
            fontSize = 10.sp
        )


        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 2
        )
    }
}


// ==========================================================
// MỚI HOÀN THÀNH
// ==========================================================

@Composable
fun CompletedBookList(
    books: List<BookListItemData>,
    onBookClick: (Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {

        books
            .take(6)
            .chunked(2)
            .forEachIndexed { rowIndex, rowBooks ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    rowBooks.forEach { book ->

                        CompletedBookItem(
                            book = book,
                            modifier = Modifier.weight(1f),
                            onBookClick = {
                                onBookClick(book.bookId)
                            }
                        )
                    }

                    // Nếu hàng cuối chỉ có 1 truyện
                    if (rowBooks.size == 1) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (rowIndex < books.take(6).chunked(2).size - 1) {

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
    }
}

// ==========================================================
// MỘT TRUYỆN TRONG MỚI HOÀN THÀNH
// ==========================================================

@Composable
fun CompletedBookItem(
    book: BookListItemData,
    modifier: Modifier = Modifier,
    onBookClick: () -> Unit
) {

    Row(
        modifier = modifier
            .height(95.dp)
            .clickable {
                onBookClick()
            },

        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(55.dp)
                .height(80.dp)
                .clip(
                    RoundedCornerShape(7.dp)
                )
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = book.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = book.genre,
                color = Color.Gray,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "★★★★★  ${book.rating}",
                color = Color(0xFFFFC107),
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LatestSmallBooksPlaceholder() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),

        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        repeat(6) { index ->

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.68f)
                    .clip(
                        RoundedCornerShape(6.dp)
                    )
                    .background(
                        if (index == 0) {
                            Color(0xFF666666)
                        } else {
                            Color(0xFF555555)
                        }
                    ),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "${index + 1}",
                    color = Color.LightGray
                )
            }
        }
    }
}
