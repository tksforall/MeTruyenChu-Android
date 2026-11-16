package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.ReviewResponse
import com.example.metruyenchu.user_interface.Book.components.BookHeader
import com.example.metruyenchu.user_interface.Book.components.ReportBottomSheet
import com.example.metruyenchu.user_interface.home.BookReviewViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch

data class ReviewData(
    val reviewId: Long,
    val userId: Long,
    val userName: String,
    val rating: Float,
    val comment: String,
    val time: String,
    val likes: Long,
    val comments: Long
)

@Composable
fun BookReview(
    book: BookResponse,
    newReview: ReviewData? = null,
    onBack: () -> Unit = {},
    onReadClick: () -> Unit = {},
    onAddToLibraryClick: () -> Unit = {},
    onOverviewClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onChapterListClick: () -> Unit = {},
    onReviewCommentClick: (Long) -> Unit = {},
    onWriteReviewClick: () -> Unit = {},
    reviewViewModel: BookReviewViewModel = viewModel(),
    onReadChapter: (Int) -> Unit = {},
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    val likedReviews by reviewViewModel.likedReviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    val error by reviewViewModel.error.collectAsState()

    var currentUserId by remember { mutableStateOf<Long?>(null) }
    var isBookmarked by remember { mutableStateOf(false) }
    var isTogglingBookmark by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(book.bookId) {
        try {
            val userResponse = RetrofitClient.api.getCurrentUser()
            if (userResponse.isSuccessful) {
                currentUserId = userResponse.body()?.userId
            }

            // Kiểm tra truyện đã nằm trong danh sách Đánh dấu chưa
            val followsResponse = RetrofitClient.api.getMyFollows()
            if (followsResponse.isSuccessful) {
                val followList = followsResponse.body().orEmpty()
                isBookmarked = followList.any { it.bookId == book.bookId }
            }
        } catch (_: Exception) {}
    }

    LaunchedEffect(book.bookId) {
        reviewViewModel.loadReviews(book.bookId)
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    androidx.compose.runtime.DisposableEffect(lifecycleOwner, book.bookId) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                reviewViewModel.loadReviews(book.bookId)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(reviews) {
        reviews.forEach { review ->
            reviewViewModel.loadLikeStatus(review.reviewId)
        }
    }

    val mainGenre = book.bookGenres
        .orEmpty()
        .mapNotNull { it.genre?.genreName }
        .firstOrNull()
        ?: "Chưa phân loại"

    var showReportSheet by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<ReviewData?>(null) }

    // 1. Lưu trạng thái bộ lọc đang chọn (Mặc định là "Mới")
    var selectedSort by remember { mutableStateOf("Mới") }

    // 2. Sắp xếp danh sách reviews gốc dựa theo tab đang chọn
    val sortedReviews = remember(reviews, selectedSort) {
        when (selectedSort) {
            "Mới" -> reviews.sortedByDescending { it.createdAt } // Hoặc sortedByDescending { it.reviewId }
            "Cũ" -> reviews.sortedBy { it.createdAt }            // Hoặc sortedBy { it.reviewId }
            "Thích" -> reviews.sortedByDescending { it.likeCount }
            else -> reviews
        }
    }

    // 3. Map từ danh sách đã sắp xếp sang ReviewData
    val reviewItems = sortedReviews.map { review ->
        ReviewData(
            reviewId = review.reviewId,
            userId = review.userId,
            userName = review.userName ?: "User #${review.userId}",
            rating = (review.rating ?: 0.0).toFloat(),
            comment = review.content,
            time = formatReviewTime(review.createdAt),
            likes = review.likeCount,
            comments = review.replyCount
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF2A2A2A))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = mainGenre,
                                color = Color(0xFF9E9E9E),
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        Button(
                            onClick = {
                                val uid = currentUserId
                                if (uid != null) {
                                    scope.launch {
                                        try {
                                            val chaptersRes = RetrofitClient.api.getChaptersByBookId(book.bookId)
                                            val firstChapterId = chaptersRes.body()?.firstOrNull()?.chapterId ?: 1L

                                            RetrofitClient.api.saveReadingHistory(
                                                userId = uid,
                                                bookId = book.bookId,
                                                chapterId = firstChapterId,
                                                progress = 0.0
                                            )
                                        } catch (_: Exception) {}
                                    }
                                }
                                onReadClick()
                            },
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Đọc", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1976D2))
                                .clickable(enabled = !isTogglingBookmark && currentUserId != null) {
                                    val uid = currentUserId ?: return@clickable
                                    scope.launch {
                                        isTogglingBookmark = true
                                        try {
                                            val response = if (isBookmarked) {
                                                RetrofitClient.api.unfollowBook(
                                                    userId = uid,
                                                    bookId = book.bookId
                                                )
                                            } else {
                                                RetrofitClient.api.followBook(
                                                    userId = uid,
                                                    bookId = book.bookId
                                                )
                                            }

                                            if (response.isSuccessful) {
                                                isBookmarked = !isBookmarked
                                                onAddToLibraryClick()
                                            } else {
                                                android.util.Log.e(
                                                    "BookReview",
                                                    "Lỗi API đánh dấu: HTTP ${response.code()} - ${response.errorBody()?.string()}"
                                                )
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("BookReview", "Exception đánh dấu", e)
                                        } finally {
                                            isTogglingBookmark = false
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) {
                                    Icons.Default.Remove
                                } else {
                                    Icons.Default.Add
                                },
                                contentDescription = if (isBookmarked) {
                                    "Bỏ đánh dấu"
                                } else {
                                    "Đánh dấu"
                                },
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF303030))
                        // Chỉ lấy bottom padding của Scaffold, bỏ top padding thừa ở trên cùng
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        BookHeader(
                            book = book,
                            onBack = onBack,
                            onReadClick = onReadClick,
                            onReadChapterClick = { chapterNumber ->
                                onReadChapter(chapterNumber)
                            },
                            onAddToLibraryClick = onAddToLibraryClick
                        )
                    }

                    item {
                        ReviewTabBar(
                            onOverviewClick = onOverviewClick,
                            onCommentClick = onCommentClick,
                            onChapterListClick = onChapterListClick
                        )
                    }

                    item {
                        ReviewHeader(
                            reviewCount = reviewItems.size,
                            selectedSort = selectedSort,
                            onSortChange = { newSort ->
                                selectedSort = newSort
                            }
                        )
                    }

                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                            }
                        }
                    } else if (error != null) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Không thể tải đánh giá",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = error ?: "Lỗi không xác định",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        reviewViewModel.loadReviews(book.bookId)
                                    }
                                ) {
                                    Text("Thử lại")
                                }
                            }
                        }
                    } else if (reviewItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Chưa có đánh giá nào.",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(
                            items = reviewItems,
                            key = { it.reviewId }
                        ) { review ->
                            ReviewItem(
                                review = review,
                                isLiked = likedReviews.contains(review.reviewId),
                                isMyReview = currentUserId != null && review.userId == currentUserId,
                                onLikeClick = {
                                    reviewViewModel.toggleLike(review.reviewId)
                                },
                                onCommentClick = {
                                    onReviewCommentClick(review.reviewId)
                                },
                                onReportClick = {
                                    selectedReview = review
                                    showReportSheet = true
                                },
                                onDeleteClick = {
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.api.deleteReview(review.reviewId)
                                            if (response.isSuccessful) {
                                                reviewViewModel.loadReviews(book.bookId)
                                            }
                                        } catch (e: Exception) {
                                            android.util.Log.e("BookReview", "Lỗi xóa đánh giá", e)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 85.dp)
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2))
                        .clickable {
                            onWriteReviewClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Viết đánh giá",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }

        if (showReportSheet && selectedReview != null) {
            ReportBottomSheet(
                username = "User #${selectedReview!!.userId}",
                bookName = book.title,
                contentType = "Đánh giá",
                onDismiss = {
                    showReportSheet = false
                    selectedReview = null
                },
                onSubmit = { reason ->
                    println(
                        "Báo cáo đánh giá ${selectedReview!!.reviewId}: $reason"
                    )
                    showReportSheet = false
                    selectedReview = null
                }
            )
        }
    }
}

private fun formatReviewTime(createdAt: String): String {
    return try {
        val cleanTime = createdAt.trim()
        val createdInstant = if (cleanTime.endsWith("Z") || cleanTime.contains("+")) {
            java.time.OffsetDateTime.parse(cleanTime.replace("Z", "+00:00")).toInstant()
        } else {
            java.time.LocalDateTime.parse(
                cleanTime,
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ).atZone(java.time.ZoneId.systemDefault()).toInstant()
        }

        val now = java.time.Instant.now()
        val seconds = java.time.Duration
            .between(createdInstant, now)
            .seconds
            .coerceAtLeast(0)

        when {
            seconds < 60 -> "vừa xong"
            seconds < 3600 -> "${seconds / 60} phút trước"
            seconds < 86400 -> "${seconds / 3600} tiếng trước"
            seconds < 2592000 -> "${seconds / 86400} ngày trước"
            seconds < 31536000 -> "${seconds / 2592000} tháng trước"
            else -> "${seconds / 31536000} năm trước"
        }
    } catch (e: Exception) {
        createdAt
    }
}

@Composable
fun ReviewTabBar(
    onOverviewClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onChapterListClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF282828))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Giới Thiệu",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onOverviewClick() }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Đánh Giá",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(2.dp)
                        .background(Color.White)
                )
            }

            Text(
                text = "Bình Luận",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onCommentClick() }
            )

            Text(
                text = "D.S Chương",
                color = Color(0xFFB0B0B0),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onChapterListClick() }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF454545))
        )
    }
}

@Composable
fun ReviewHeader(
    reviewCount: Int = 0,
    selectedSort: String = "Mới",
    onSortChange: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đánh giá",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "($reviewCount)",
                color = Color(0xFF888888),
                fontSize = 17.sp
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF484848))
        ) {
            // TAB MỚI
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .clickable { onSortChange("Mới") }
                    .background(
                        if (selectedSort == "Mới") Color(0xFFE0E0E0) else Color.Transparent
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Mới",
                    color = if (selectedSort == "Mới") Color(0xFF333333) else Color(0xFFD0D0D0),
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Mới") FontWeight.Bold else FontWeight.Normal
                )
            }

            // TAB CŨ
            Box(
                modifier = Modifier
                    .clickable { onSortChange("Cũ") }
                    .background(
                        if (selectedSort == "Cũ") Color(0xFFE0E0E0) else Color.Transparent
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Cũ",
                    color = if (selectedSort == "Cũ") Color(0xFF333333) else Color(0xFFD0D0D0),
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Cũ") FontWeight.Bold else FontWeight.Normal
                )
            }

            // TAB THÍCH
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
                    .clickable { onSortChange("Thích") }
                    .background(
                        if (selectedSort == "Thích") Color(0xFFE0E0E0) else Color.Transparent
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Thích",
                    color = if (selectedSort == "Thích") Color(0xFF333333) else Color(0xFFD0D0D0),
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Thích") FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: ReviewData,
    isLiked: Boolean = false,
    isMyReview: Boolean = false,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF555555)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.userName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Đánh giá",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (review.rating % 1 == 0f) {
                            review.rating.toInt().toString()
                        } else {
                            review.rating.toString()
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // GÓC TRÊN BÊN PHẢI: Nút Xóa (bên trái) + Nút Lá cờ Báo cáo (bên phải)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMyReview) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa đánh giá",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onReportClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Báo cáo",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (review.comment.isNotBlank()) {
            Text(
                text = review.comment,
                color = Color(0xFFE0E0E0),
                fontSize = 15.sp,
                lineHeight = 23.sp,
                modifier = Modifier.padding(
                    start = 66.dp,
                    top = 10.dp,
                    end = 10.dp
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 66.dp, top = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = review.time,
                color = Color(0xFF888888),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.clickable { onLikeClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = if (isLiked) "Bỏ thích" else "Thích",
                    tint = if (isLiked) Color(0xFF2196F3) else Color(0xFF888888),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = review.likes.toString(),
                    color = if (isLiked) Color(0xFF2196F3) else Color(0xFF888888),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Row(
                modifier = Modifier.clickable { onCommentClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Bình luận",
                    tint = Color(0xFF888888),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = review.comments.toString(),
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF3D3D3D))
        )
    }
}


