package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ReviewCreateRequest
import com.example.metruyenchu.api.ReviewResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.user_interface.Book.components.ReportBottomSheet
import kotlinx.coroutines.launch
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
@Composable
fun ReviewComment(
    bookId: Long,
    reviewId: Long,
    onBack: () -> Unit = {}
) {
    var mainReview by remember {
        mutableStateOf<ReviewResponse?>(null)
    }
    var replies by remember {
        mutableStateOf<List<ReviewResponse>>(emptyList())
    }
    var userNames by remember {
        mutableStateOf<Map<Long, String>>(emptyMap())
    }

    var bookTitle by remember {
        mutableStateOf("Đang tải truyện...")
    }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var replyText by remember { mutableStateOf("") }
    var selectedReview by remember {
        mutableStateOf<ReviewResponse?>(null)
    }
    var showReportSheet by remember { mutableStateOf(false) }
    var replyingTo by remember {
        mutableStateOf<ReviewResponse?>(null)
    }
    var isSending by remember { mutableStateOf(false) }

    // Trạng thái Like và số Like cho từng review.
    var likedReviews by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }
    var likeCounts by remember {
        mutableStateOf<Map<Long, Long>>(emptyMap())
    }
    var likingReviews by remember {
        mutableStateOf<Set<Long>>(emptySet())
    }

    val scope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun openReplyInput() {
        scope.launch {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun loadReviews() {
        scope.launch {
            isLoading = true
            error = null


             //  tên truyện trên Header
            try {
                val bookResponse = RetrofitClient.api.getBookById(bookId)

                if (bookResponse.isSuccessful) {
                    bookTitle = bookResponse.body()?.title
                        ?: "Truyện #$bookId"
                } else {
                    bookTitle = "Truyện #$bookId"
                }
            } catch (_: Exception) {
                bookTitle = "Truyện #$bookId"
            }

            try {
                // Lấy đúng bình luận được chọn.
                val reviewResponse =
                    RetrofitClient.api.getReview(reviewId)

                if (!reviewResponse.isSuccessful) {
                    error =
                        "Không thể tải bình luận (${reviewResponse.code()})"
                    return@launch
                }

                val rootReview = reviewResponse.body()

                if (rootReview == null) {
                    error = "Không tìm thấy bình luận này."
                    return@launch
                }

                android.util.Log.d(
                    "ReviewUserDebug",
                    "Root review: userId=${rootReview.userId}, userName=${rootReview.userName}"
                )

                if (rootReview.bookId != bookId) {
                    error = "Bình luận không thuộc truyện này."
                    return@launch
                }

                // Lấy các câu trả lời trực tiếp của bình luận.
                val repliesResponse =
                    RetrofitClient.api.getReviewReplies(reviewId)

                if (!repliesResponse.isSuccessful) {
                    error =
                        "Không thể tải câu trả lời (${repliesResponse.code()})"
                    return@launch
                }
                val replyListRaw = repliesResponse.body().orEmpty()

                // Dùng cùng API với BookReview để lấy tên người dùng.
                val mainReviews = try {
                    val response = RetrofitClient.api.getMainReviews(bookId)
                    if (response.isSuccessful) {
                        response.body().orEmpty()
                    } else {
                        emptyList()
                    }
                } catch (_: Exception) {
                    emptyList()
                }

                // Tạo bảng ánh xạ userId -> userName.
                val loadedUserNames = userNames.toMutableMap()

                mainReviews.forEach { review ->
                    val name = review.userName?.takeIf { it.isNotBlank() }

                    if (name != null) {
                        loadedUserNames[review.userId] = name
                    }
                }

                // Bổ sung tên cho bình luận gốc.
                val enrichedRootReview = rootReview.copy(
                    userName = rootReview.userName
                        ?.takeIf { it.isNotBlank() }
                        ?: loadedUserNames[rootReview.userId]
                )

                // Bổ sung tên cho các câu trả lời.
                val replyList = replyListRaw
                    .sortedBy { it.createdAt }
                    .map { reply ->
                        reply.copy(
                            userName = reply.userName
                                ?.takeIf { it.isNotBlank() }
                                ?: loadedUserNames[reply.userId]
                        )
                    }


                 // Lưu tên người dùng.
                userNames = loadedUserNames

                mainReview = enrichedRootReview
                replies = replyList

                // Mặc định ô nhập là trả lời bình luận gốc.
                replyingTo = enrichedRootReview

                // Gộp bình luận gốc và các câu trả lời.
                val allReviews = listOf(enrichedRootReview) + replyList

                // Cập nhật số Like từ dữ liệu server.
                likeCounts = allReviews.associate { review ->
                    review.reviewId to review.likeCount
                }

                // Lấy trạng thái Like hiện tại cho từng review.
                val likedIds = mutableSetOf<Long>()

                allReviews.forEach { review ->
                    try {
                        val statusResponse =
                            RetrofitClient.api.getReviewLikeStatus(
                                review.reviewId
                            )

                        if (statusResponse.isSuccessful &&
                            statusResponse.body()?.liked == true
                        ) {
                            likedIds.add(review.reviewId)
                        }
                    } catch (_: Exception) {
                        // Lỗi tải trạng thái Like không làm hỏng màn hình.
                    }
                }

                likedReviews = likedIds
            } catch (e: Exception) {
                error = e.message ?: "Lỗi kết nối đến máy chủ"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleLike(review: ReviewResponse) {
        val id = review.reviewId

        if (id in likingReviews) return

        scope.launch {
            likingReviews = likingReviews + id

            try {
                val currentlyLiked = id in likedReviews

                val response = if (currentlyLiked) {
                    RetrofitClient.api.unlikeReview(id)
                } else {
                    RetrofitClient.api.likeReview(id)
                }

                if (response.isSuccessful) {
                    likedReviews = if (currentlyLiked) {
                        likedReviews - id
                    } else {
                        likedReviews + id
                    }

                    val currentCount =
                        likeCounts[id] ?: review.likeCount

                    likeCounts = likeCounts + (
                            id to if (currentlyLiked) {
                                (currentCount - 1).coerceAtLeast(0L)
                            } else {
                                currentCount + 1
                            }
                            )
                }
            } catch (_: Exception) {
                // Giữ nguyên trạng thái UI nếu request thất bại.
            } finally {
                likingReviews = likingReviews - id
            }
        }
    }

    LaunchedEffect(bookId, reviewId) {
        loadReviews()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF292929))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = bookTitle,
                    color = Color.White,
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 40.dp, end = 8.dp)
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "Đã xảy ra lỗi",
                            color = Color.White,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Nhấn để thử lại",
                            color = Color(0xFF64B5F6),
                            modifier = Modifier.clickable {
                                loadReviews()
                            }
                        )
                    }
                }

                mainReview == null -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không tìm thấy bình luận.",
                            color = Color(0xFFAAAAAA)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        item(key = "main_review_${reviewId}") {
                            val review = mainReview!!

                            ReviewCommentItem(
                                review = review,
                                displayUserName = userNames[review.userId]
                                    ?: review.userName?.takeIf { it.isNotBlank() }
                                    ?: "User #${review.userId}",
                                isMainComment = true,
                                isLiked = review.reviewId in likedReviews,
                                likeCount = likeCounts[review.reviewId]
                                    ?: review.likeCount,
                                isLiking = review.reviewId in likingReviews,
                                onLikeClick = {
                                    toggleLike(review)
                                },

                                onReplyClick = {
                                    replyingTo = review
                                    openReplyInput()
                                },
                                onReportClick = {
                                    selectedReview = review
                                    showReportSheet = true
                                }
                            )
                        }

                        if (replies.isEmpty()) {
                            item(key = "empty_replies") {
                                Text(
                                    text = "Chưa có câu trả lời nào.",
                                    color = Color(0xFF858585),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(
                                        start = 60.dp,
                                        top = 8.dp,
                                        bottom = 16.dp
                                    )
                                )
                            }
                        } else {
                            items(
                                items = replies,
                                key = { it.reviewId }
                            ) { reply ->
                                ReviewCommentItem(
                                    review = reply,
                                    displayUserName = userNames[reply.userId]
                                        ?: reply.userName?.takeIf { it.isNotBlank() }
                                        ?: "User #${reply.userId}",
                                    isMainComment = false,
                                    isLiked = reply.reviewId in likedReviews,
                                    likeCount = likeCounts[reply.reviewId]
                                        ?: reply.likeCount,
                                    isLiking = reply.reviewId in likingReviews,
                                    onLikeClick = {
                                        toggleLike(reply)
                                    },
                                    onReplyClick = {
                                        // Trả lời tiếp trong cùng luồng
                                        // bằng cách gắn vào bình luận gốc.
                                        replyingTo = mainReview

                                        val username = userNames[reply.userId]
                                            ?: reply.userName?.takeIf { it.isNotBlank() }
                                            ?: "User #${reply.userId}"

                                        replyText = "@$username "
                                        openReplyInput()
                                    },
                                    onReportClick = {
                                        selectedReview = reply
                                        showReportSheet = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            ReplyInput(
                text = replyText,
                replyTo = replyingTo,
                replyToUserName = replyingTo?.let { target ->
                    userNames[target.userId]
                        ?: target.userName?.takeIf { it.isNotBlank() }
                        ?: "User #${target.userId}"
                },
                enabled = !isSending,
                focusRequester = focusRequester,
                onTextChange = { replyText = it },
                onCancelReply = {
                    // Không để gửi nhầm thành bình luận gốc.
                    replyingTo = mainReview
                },
                onSendClick = send@{
                    if (replyText.isBlank() || isSending) {
                        return@send
                    }

                    val parent = replyingTo ?: mainReview
                    if (parent == null) {
                        error = "Không tìm thấy bình luận để trả lời."
                        return@send
                    }

                    scope.launch {
                        isSending = true
                        error = null

                        try {
                            val request = ReviewCreateRequest(
                                bookId = bookId,
                                parentReviewId = parent.reviewId,
                                rating = null,
                                content = replyText.trim()
                            )

                            val response =
                                RetrofitClient.api.createReview(request)

                            if (response.isSuccessful) {
                                replyText = ""
                                replyingTo = mainReview
                                loadReviews()
                            } else {
                                error =
                                    "Không thể gửi trả lời (${response.code()})"
                            }
                        } catch (e: Exception) {
                            error = e.message ?: "Lỗi khi gửi trả lời"
                        } finally {
                            isSending = false
                        }
                    }
                }
            )
        }

        if (showReportSheet && selectedReview != null) {
            val reportReview = selectedReview!!

            ReportBottomSheet(
                username = userNames[reportReview.userId]
                    ?: reportReview.userName?.takeIf { it.isNotBlank() }
                    ?: "User #${reportReview.userId}",
                bookName = "Truyện #$bookId",
                contentType = "Bình luận",
                onDismiss = {
                    showReportSheet = false
                    selectedReview = null
                },
                onSubmit = { reason ->
                    // Chưa gửi báo cáo lên backend ở đoạn này.
                    // Cần nối API createReport để lưu báo cáo thật.
                    println(
                        "Báo cáo review ${reportReview.reviewId}: $reason"
                    )
                    showReportSheet = false
                    selectedReview = null
                }
            )
        }
    }
}

@Composable
private fun ReviewCommentItem(
    review: ReviewResponse,
    displayUserName: String,
    isMainComment: Boolean,
    isLiked: Boolean,
    likeCount: Long,
    isLiking: Boolean,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val avatarSize = 42.dp
    val contentStart = 56.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isMainComment) Color(0xFF202020)
                else Color(0xFF303030)
            )
            .padding(
                horizontal = if (isMainComment) 32.dp else 60.dp,
                vertical = if (isMainComment) 10.dp else 8.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5E5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Avatar",
                    tint = Color(0xFF8A8A8A),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayUserName,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )

                if (review.rating != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★",
                            color = Color(0xFFFFC107),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = review.rating.toString(),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "Báo cáo",
                tint = Color(0xFFAAAAAA),
                modifier = Modifier
                    .size(19.dp)
                    .clickable { onReportClick() }
            )
        }

        Text(
            text = review.content,
            color = Color.White,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = contentStart,
                    top = 8.dp,
                    end = 4.dp
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = contentStart, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMainComment) {
                Text(
                    text = formatReviewTime(review.createdAt.trim()),
                    color = Color(0xFF858585),
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            } else {
                //Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Trả lời $displayUserName",
                    color = Color(0xFF858585),
                    fontSize = 10.sp,
                    modifier = Modifier
                        .clickable { onReplyClick() }
                        .padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .clickable(enabled = !isLiking) {
                        onLikeClick()
                    }
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = if (isLiked) "Bỏ thích" else "Thích",
                    tint = if (isLiked) Color(0xFF2196F3)
                    else Color(0xFF858585),
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = likeCount.toString(),
                    color = if (isLiked) Color(0xFF2196F3)
                    else Color(0xFF858585),
                    fontSize = 13.sp
                )
            }


            /*

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier
                    .clickable { onReplyClick() }
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Trả lời",
                    tint = Color(0xFF858585),
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = review.replyCount.toString(),
                    color = Color(0xFF858585),
                    fontSize = 13.sp
                )
            }
            */
        }
    }
}





private fun formatReviewTime(createdAt: String): String {
    return try {
        val dateTime = java.time.LocalDateTime.parse(
            createdAt.trim(),
            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )

        val createdTime = dateTime
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()

        val now = java.time.Instant.now()

        val seconds = java.time.Duration
            .between(createdTime, now)
            .seconds
            .coerceAtLeast(0)

        when {
            seconds < 60 ->
                "vừa xong"

            seconds < 3600 ->
                "${seconds / 60} phút trước"

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
        android.util.Log.e(
            "ReviewTime",
            "Lỗi timestamp: $createdAt",
            e
        )
        createdAt
    }
}




@Composable
private fun ReplyInput(
    text: String,
    replyTo: ReviewResponse?,
    replyToUserName: String?,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onTextChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF292929))
            .imePadding()
            .padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 2.dp
            )
    ) {
        Column {
//            if (replyTo != null) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Đang trả lời ${replyToUserName ?: "User #${replyTo?.userId}"}",
//                        color = Color(0xFFAAAAAA),
//                        fontSize = 12.sp,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    Text(
//                        text = "Hủy",
//                        color = Color.White,
//                        fontSize = 12.sp,
//                        modifier = Modifier.clickable {
//                            onCancelReply()
//                        }
//                    )
//                }
//            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Bình luận",
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    enabled = enabled,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            text = if (replyTo == null)
                                "Thêm bình luận"
                            else
                                "Thêm trả lời",
                            color = Color(0xFF858585),
                            fontSize = 15.sp
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(enabled = enabled) {
                            onSendClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi",
                        tint = Color.White,
                        modifier = Modifier.size(27.dp)
                    )
                }
            }
        }
    }
}