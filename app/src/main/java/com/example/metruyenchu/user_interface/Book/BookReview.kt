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
    reviewViewModel: BookReviewViewModel = viewModel()
) {
    val reviews by reviewViewModel.reviews.collectAsState()
    val likedReviews by reviewViewModel.likedReviews.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    val error by reviewViewModel.error.collectAsState()

    LaunchedEffect(book.bookId) {
        reviewViewModel.loadReviews(book.bookId)
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

    var isAddedToLibrary by remember { mutableStateOf(false) }

    var showReportSheet by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<ReviewData?>(null) }

    val reviewItems = reviews.map { review ->
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
                            onClick = onReadClick,
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
                                .clickable {
                                    isAddedToLibrary = !isAddedToLibrary
                                    onAddToLibraryClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAddedToLibrary) {
                                    Icons.Default.Remove
                                } else {
                                    Icons.Default.Add
                                },
                                contentDescription = if (isAddedToLibrary) {
                                    "Xóa khỏi tủ"
                                } else {
                                    "Thêm vào tủ"
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
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        BookHeader(
                            book = book,
                            onBack = onBack,
                            onReadClick = onReadClick,
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
                        ReviewHeader(reviewCount = reviewItems.size)
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

                                onLikeClick = {
                                    reviewViewModel.toggleLike(review.reviewId)
                                },

                                onCommentClick = {
                                    onReviewCommentClick(review.reviewId)
                                },

                                onReportClick = {
                                    selectedReview = review
                                    showReportSheet = true
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
        val dateTime = java.time.OffsetDateTime.parse(
            createdAt.replace("Z", "+00:00")
        )

        val createdTime = dateTime.toInstant()
        val now = java.time.Instant.now()

        val seconds = java.time.Duration
            .between(createdTime, now)
            .seconds

        when {
            seconds < 3600 ->
                "vừa xong"

            seconds < 86400 ->
                "${seconds / 3600} tiếng trước"

            seconds < 2592000 ->
                "${seconds / 86400} ngày trước"

            seconds < 31536000 ->
                "${seconds / 2592000} tháng trước"

            else ->
                "${seconds / 31536000} năm trước"
        }

    } catch (e: Exception) {
        "vừa xong"
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
    onSortChange: (String) -> Unit = {}
) {
    var selectedSort by remember { mutableStateOf("Mới") }

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
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            bottomStart = 20.dp
                        )
                    )
                    .clickable {
                        selectedSort = "Mới"
                        onSortChange("Mới")
                    }
                    .background(
                        if (selectedSort == "Mới") {
                            Color(0xFFE0E0E0)
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Mới",
                    color = if (selectedSort == "Mới") {
                        Color(0xFF333333)
                    } else {
                        Color(0xFFD0D0D0)
                    },
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Mới") {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }

            Box(
                modifier = Modifier
                    .clickable {
                        selectedSort = "Cũ"
                        onSortChange("Cũ")
                    }
                    .background(
                        if (selectedSort == "Cũ") {
                            Color(0xFFE0E0E0)
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Cũ",
                    color = if (selectedSort == "Cũ") {
                        Color(0xFF333333)
                    } else {
                        Color(0xFFD0D0D0)
                    },
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Cũ") {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .clickable {
                        selectedSort = "Thích"
                        onSortChange("Thích")
                    }
                    .background(
                        if (selectedSort == "Thích") {
                            Color(0xFFE0E0E0)
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Thích",
                    color = if (selectedSort == "Thích") {
                        Color(0xFF333333)
                    } else {
                        Color(0xFFD0D0D0)
                    },
                    fontSize = 13.sp,
                    fontWeight = if (selectedSort == "Thích") {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: ReviewData,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onReportClick: () -> Unit = {}
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
                modifier = Modifier.clickable {
                    onLikeClick()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = if (isLiked) {
                        "Bỏ thích"
                    } else {
                        "Thích"
                    },
                    tint = if (isLiked) {
                        Color(0xFF2196F3)
                    } else {
                        Color(0xFF888888)
                    },
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = review.likes.toString(),
                    color = if (isLiked) {
                        Color(0xFF2196F3)
                    } else {
                        Color(0xFF888888)
                    },
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
