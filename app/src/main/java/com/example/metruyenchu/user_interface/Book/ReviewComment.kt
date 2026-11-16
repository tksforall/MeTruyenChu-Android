package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.ChatBubbleOutline


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
    var replyText by remember { mutableStateOf(TextFieldValue("")) }

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

    // Thêm state lưu ID người dùng hiện tại
    var currentUserId by remember { mutableStateOf<Long?>(null) }

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
//                val mainReviews = try {
//                    val response = RetrofitClient.api.getMainReviews(bookId)
//                    if (response.isSuccessful) {
//                        response.body().orEmpty()
//                    } else {
//                        emptyList()
//                    }
//                } catch (_: Exception) {
//                    emptyList()
//                }
//
//                // Tạo bảng ánh xạ userId -> userName.
//                val loadedUserNames = userNames.toMutableMap()
//
//                val allUserIds = (listOf(rootReview.userId) + replyListRaw.map { it.userId }).distinct()
//
//                mainReviews.forEach { review ->
//                    val name = review.userName?.takeIf { it.isNotBlank() }
//
//                    if (name != null) {
//                        loadedUserNames[review.userId] = name
//                    }
//                }
//
//                // Bổ sung tên cho bình luận gốc.
//                val enrichedRootReview = rootReview.copy(
//                    userName = rootReview.userName
//                        ?.takeIf { it.isNotBlank() }
//                        ?: loadedUserNames[rootReview.userId]
//                )
//
//                // Bổ sung tên cho các câu trả lời.
//                val replyList = replyListRaw
//                    .sortedBy { it.createdAt }
//                    .map { reply ->
//                        reply.copy(
//                            userName = reply.userName
//                                ?.takeIf { it.isNotBlank() }
//                                ?: loadedUserNames[reply.userId]
//                        )
//                    }
                // ----------------------------------------------
                // 1. Ưu tiên dùng luôn userName Backend đã gắn sẵn trong ReviewResponse
                val newFetchedNames = mutableMapOf<Long, String>()
                (listOf(rootReview) + replyListRaw).forEach { rev ->
                    val nameFromApi = rev.userName?.takeIf { it.isNotBlank() }
                    if (nameFromApi != null) {
                        newFetchedNames[rev.userId] = nameFromApi
                    }
                }

                // 2. Chỉ gọi getUserById cho những userId nào Backend chưa trả kèm tên
                val missingUserIds = (listOf(rootReview.userId) + replyListRaw.map { it.userId })
                    .distinct()
                    .filter { !newFetchedNames.containsKey(it) }

                if (missingUserIds.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        missingUserIds.forEach { uid ->
                            try {
                                val uResponse = RetrofitClient.api.getUserById(uid)
                                if (uResponse.isSuccessful) {
                                    val userObj = uResponse.body()
                                    val name = userObj?.userName?.takeIf { it.isNotBlank() }
                                        ?: userObj?.email?.takeIf { it.isNotBlank() }
                                    if (!name.isNullOrBlank()) {
                                        newFetchedNames[uid] = name
                                    }
                                }
                            } catch (_: Exception) {}
                        }
                    }
                }

                // 3. Cập nhật State userNames
                userNames = userNames + newFetchedNames

                // 4. Gán tên chuẩn vào Object hiển thị
                val enrichedRootReview = rootReview.copy(
                    userName = rootReview.userName?.takeIf { it.isNotBlank() }
                        ?: userNames[rootReview.userId]
                        ?: "User #${rootReview.userId}"
                )

                val replyList = replyListRaw
                    .sortedBy { it.createdAt }
                    .map { reply ->
                        reply.copy(
                            userName = reply.userName?.takeIf { it.isNotBlank() }
                                ?: userNames[reply.userId]
                                ?: "User #${reply.userId}"
                        )
                    }

                mainReview = enrichedRootReview
                replies = replyList




                // ---------------------------------------



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
        scope.launch {
            try {
                val userResponse = RetrofitClient.api.getCurrentUser()
                if (userResponse.isSuccessful) {
                    // Lấy đúng trường userId từ UserResponse
                    currentUserId = userResponse.body()?.userId
                }
            } catch (_: Exception) {}
        }

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
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 48.dp, bottom = 24.dp),                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = "Chưa có bình luận",
                                        tint = Color(0xFF777777),
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Chưa có bình luận",
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 15.sp
                                    )
                                }
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
                                    likeCount = likeCounts[reply.reviewId] ?: reply.likeCount,
                                    isLiking = reply.reviewId in likingReviews,
                                    currentUserId = currentUserId,
                                    onLikeClick = { toggleLike(reply) },
                                    onReplyClick = {
                                        replyingTo = mainReview
                                        val username = userNames[reply.userId]
                                            ?: reply.userName?.takeIf { it.isNotBlank() }
                                            ?: "User #${reply.userId}"
                                        val mention = "@$username "
                                        replyText = TextFieldValue(
                                            text = mention,
                                            selection = TextRange(mention.length) // Đưa con trỏ ra sau dấu cách
                                        )
                                        openReplyInput()
                                    },
                                    onReportClick = {
                                        selectedReview = reply
                                        showReportSheet = true
                                    },
                                    onDeleteClick = {
                                        // Gọi API Xóa review ở đây và load lại danh sách
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.api.deleteReview(reply.reviewId)
                                                if (response.isSuccessful) {
                                                    loadReviews() // Tải lại danh sách sau khi xóa thành công
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("DeleteReview", "Lỗi xóa review", e)
                                            }
                                        }
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
                    if (replyText.text.isBlank() || isSending) {
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
                                content = replyText.text.trim()
                            )

                            val response =
                                RetrofitClient.api.createReview(request)

                            if (response.isSuccessful) {
                                replyText = TextFieldValue("")
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
    currentUserId: Long? = null, // Thêm ID user hiện tại để check chính chủ
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit = {} // Callback khi nhấn xóa
) {
    val avatarSize = if (isMainComment) 44.dp else 30.dp
    val contentStart = 56.dp
    val isMyComment = currentUserId != null && review.userId == currentUserId

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isMainComment) Color(0xFF202020)
                else Color(0xFF303030)
            )
            .padding(
                start = 32.dp, // Dùng chung 32.dp để mép trái Avatar con thẳng hàng với Avatar chính
                end = 32.dp,   // Dùng chung 32.dp để mép phải nút Like con thẳng hàng với nút Like chính
                top = if (isMainComment) 10.dp else 6.dp,
                bottom = if (isMainComment) 10.dp else 4.dp
            )
    ) {
        // --- HÀNG HEADER (Avatar, Tên, và Nút Xóa góc trên) ---
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
                    fontSize = if (isMainComment) 17.sp else 15.sp, // Review chính giữ nguyên 17.sp, review con giảm xuống 14.sp (hoặc 13.sp)                    fontWeight = FontWeight.Normal,
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

            if (!isMainComment && isMyComment) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa bình luận",
                    tint = Color(0xFFE53935), // Màu đỏ
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDeleteClick() }
                )
            }
        }

        // --- NỘI DUNG BÌNH LUẬN ---
        Text(
            text = review.content,
            color = Color.White,
            fontSize = if (isMainComment) 15.sp else 13.sp, // Review chính giữ 13.sp, review con giảm còn 12.sp
            lineHeight = if (isMainComment) 20.sp else 17.sp, // Giảm nhẹ chiều cao dòng cho cân đối
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = contentStart,
                    top = 4.dp,
                    end = 4.dp
                )
        )

        // --- HÀNG BOTTOM (Thời gian/Trả lời, Báo cáo, Like) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = contentStart, top = 4.dp),
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

            // NÚT BÁO CÁO (Nằm bên trái nút Like - Chỉ hiện ở Review Con)
            if (!isMainComment) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Báo cáo",
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier
                        .size(17.dp)
                        .clickable { onReportClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // NÚT LIKE
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
                    tint = if (isLiked) Color(0xFF2196F3) else Color(0xFF858585),
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = likeCount.toString(),
                    color = if (isLiked) Color(0xFF2196F3) else Color(0xFF858585),
                    fontSize = 13.sp
                )
            }
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
    text: TextFieldValue,
    replyTo: ReviewResponse?,
    replyToUserName: String?,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onTextChange: (TextFieldValue) -> Unit,
    onCancelReply: () -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF292929))
            // Lấy chiều cao bàn phím nhưng TRỪ ĐI chiều cao thanh điều hướng đáy để không bị dư khoảng trống
            .windowInsetsPadding(
                WindowInsets.ime.exclude(WindowInsets.navigationBars)
            )
            .padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 2.dp
            )
    ) {
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
                        text = if (replyTo == null) "Thêm bình luận" else "Thêm trả lời",
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