package com.example.metruyenchu.user_interface.Book.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.runtime.mutableLongStateOf

@Composable
fun BookHeader(
    book: BookResponse,
    onBack: () -> Unit = {},
    onReadClick: () -> Unit = {},
    onReadChapterClick: ((Int) -> Unit)? = null,
    onAddToLibraryClick: () -> Unit = {},
    isAddedToLibrary: Boolean = false
) {
    val scope = rememberCoroutineScope()

    var currentUserId by remember { mutableStateOf<Long?>(null) }
    var inLibraryState by remember(book.bookId, isAddedToLibrary) { mutableStateOf(isAddedToLibrary) }
    var isTogglingLibrary by remember { mutableStateOf(false) }

    var resumeChapterNumber by remember(book.bookId) { mutableIntStateOf(1) }
    var hasReadingHistory by remember(book.bookId) { mutableStateOf(false) }

    var liveRatingScore by remember(book.bookId, book.ratingScore) {
        mutableDoubleStateOf(book.ratingScore ?: 0.0)
    }
    var liveRatingCount by remember(book.bookId, book.ratingCount) {
        mutableLongStateOf(book.ratingCount ?: 0L)
    }

    // Tự động đồng bộ trạng thái Tủ truyện, Lịch sử đọc và Số sao thật
    LaunchedEffect(book.bookId) {
        // 1. Lấy user hiện tại
        try {
            val userRes = RetrofitClient.api.getCurrentUser()
            if (userRes.isSuccessful) {
                currentUserId = userRes.body()?.userId
            }
        } catch (_: Exception) {}

        // 2. Kiểm tra truyện đã có trong tủ truyện chưa
        try {
            val libRes = RetrofitClient.api.getMyLibrary()
            if (libRes.isSuccessful) {
                val found = libRes.body().orEmpty().any { it.bookId == book.bookId }
                inLibraryState = found
            }
        } catch (_: Exception) {}

        // 3. Kiểm tra lịch sử đọc để lấy đúng chương đang đọc dở
        try {
            val historyRes = RetrofitClient.api.getMyReadingHistory()
            if (historyRes.isSuccessful) {
                val bookHistory = historyRes.body().orEmpty().find { it.bookId == book.bookId }
                if (bookHistory != null) {
                    val chaptersRes = RetrofitClient.api.getChaptersByBookId(book.bookId)
                    if (chaptersRes.isSuccessful) {
                        val matchedChapter = chaptersRes.body().orEmpty()
                            .find { it.chapterId == bookHistory.chapterId }
                        if (matchedChapter != null) {
                            resumeChapterNumber = matchedChapter.chapterNumber
                            hasReadingHistory = true
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // 4. Cập nhật điểm sao & số lượng đánh giá mới nhất
        try {
            val reviewsRes = RetrofitClient.api.getMainReviews(book.bookId)
            if (reviewsRes.isSuccessful) {
                val ratedReviews = reviewsRes.body().orEmpty().filter { it.rating != null }
                if (ratedReviews.isNotEmpty()) {
                    liveRatingCount = ratedReviews.size.toLong()
                    liveRatingScore = ratedReviews.mapNotNull { it.rating }.average()
                }
            }
        } catch (_: Exception) {}
    }

    // ==================================================
    // THỂ LOẠI THẬT
    // ==================================================
    val mainGenre = book.bookGenres
        .orEmpty()
        .mapNotNull { it.genre?.genreName }
        .distinct()
        .firstOrNull()
        ?: "Chưa phân loại"

    // ==================================================
    // HEADER
    // ==================================================
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3A3A3A))
            .padding(
                top = 4.dp,
                bottom = 14.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        // BACK
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // ẢNH + THÔNG TIN
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 42.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ẢNH BÌA THẬT
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(182.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                if (!book.coverUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = "Ảnh bìa ${book.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "ẢNH",
                        color = Color(0xFFAAAAAA),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // THÔNG TIN
            Column(modifier = Modifier.weight(1f)) {
                // THỂ LOẠI THẬT
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1976D2), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mainGenre,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // TÊN TRUYỆN THẬT
                Text(
                    text = book.title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(2.dp))

                // TÁC GIẢ THẬT
                Text(
                    text = book.author,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                // ĐÁNH GIÁ THẬT
                val filledStars = liveRatingScore.toInt().coerceIn(0, 5)
                val starString = "★".repeat(filledStars) + "☆".repeat(5 - filledStars)

                Text(
                    text = "$starString ${String.format(Locale.US, "%.1f", liveRatingScore)} ($liveRatingCount đánh giá)",
                    color = Color(0xFFFFC107),
                    fontSize = 12.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                // BUTTONS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // NÚT ĐỌC (LUÔN HIỆN CHỮ "Đọc", BẤM VÀO MỞ ĐÚNG CHƯƠNG ĐANG ĐỌC)
                    Button(
                        onClick = {
                            if (onReadChapterClick != null) {
                                onReadChapterClick(resumeChapterNumber)
                            } else {
                                onReadClick()
                            }
                        },
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Đọc",
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // NÚT TỦ TRUYỆN (NỀN LUÔN TRẮNG, CHỮ NHỎ GỌN)
                    Button(
                        onClick = {
                            val uid = currentUserId
                            if (uid == null || isTogglingLibrary) {
                                onAddToLibraryClick()
                                return@Button
                            }

                            scope.launch {
                                isTogglingLibrary = true
                                try {
                                    if (inLibraryState) {
                                        val res = RetrofitClient.api.removeBookFromLibrary(uid, book.bookId)
                                        if (res.isSuccessful) {
                                            inLibraryState = false
                                        }
                                    } else {
                                        val res = RetrofitClient.api.addBookToLibrary(uid, book.bookId)
                                        if (res.isSuccessful) {
                                            inLibraryState = true
                                        }
                                    }
                                    onAddToLibraryClick()
                                } catch (_: Exception) {
                                } finally {
                                    isTogglingLibrary = false
                                }
                            }
                        },
                        enabled = !isTogglingLibrary,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (inLibraryState) "− Trừ khỏi tủ truyện" else "+ Thêm vào tủ truyện",
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}