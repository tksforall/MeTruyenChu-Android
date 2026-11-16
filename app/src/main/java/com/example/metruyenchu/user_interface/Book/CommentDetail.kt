package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.api.CommentCreateRequest
import com.example.metruyenchu.api.CommentResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.SessionManager
import com.example.metruyenchu.api.UserResponse
import com.example.metruyenchu.user_interface.Book.components.ReportBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CommentDetail(
    comment: CommentResponse,
    replies: List<CommentResponse> = emptyList(),
    users: Map<Long, UserResponse> = emptyMap(),
    chapters: Map<Long, ChapterResponse> = emptyMap(),
    onBack: () -> Unit = {},
    onLikeComment: (CommentResponse) -> Unit = {},
    onReportComment: (CommentResponse) -> Unit = {},
    onSendReply: (String, CommentResponse) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var mainComment by remember { mutableStateOf(comment) }
    var replyList by remember { mutableStateOf(replies) }
    var userNames by remember {
        mutableStateOf(
            users.mapValues { (_, u) -> u.userName }
        )
    }
    var chapterNumbers by remember {
        mutableStateOf(
            chapters.mapValues { (_, c) -> c.chapterNumber }
        )
    }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var replyText by remember { mutableStateOf(TextFieldValue("")) }

    var selectedComment by remember { mutableStateOf<CommentResponse?>(null) }
    var showReportSheet by remember { mutableStateOf(false) }
    var replyingTo by remember { mutableStateOf(comment) }
    var isSending by remember { mutableStateOf(false) }

    var likedComments by remember { mutableStateOf(emptySet<Long>()) }
    var likeCounts by remember { mutableStateOf(emptyMap<Long, Long>()) }
    var likingComments by remember { mutableStateOf(emptySet<Long>()) }
    var currentUserId by remember {
        mutableStateOf<Long?>(sessionManager.getUserId().takeIf { it > 0L })
    }

    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun openReplyInput() {
        scope.launch {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun loadCommentDetail() {
        scope.launch {
            isLoading = true
            error = null

            try {
                // 1. Tải lại thông tin mới nhất của comment gốc
                val rootRes = RetrofitClient.api.getComment(comment.commentId)
                val rootComment = if (rootRes.isSuccessful && rootRes.body() != null) {
                    rootRes.body()!!
                } else {
                    comment
                }

                // 2. Tải danh sách replies của comment gốc
                val repliesRes = RetrofitClient.api.getCommentReplies(comment.commentId)
                val rawReplies = if (repliesRes.isSuccessful) {
                    repliesRes.body().orEmpty()
                } else {
                    replyList
                }

                // 3. Lấy tên user cho toàn bộ comment & replies
                val allUserIds = (listOf(rootComment.userId) + rawReplies.map { it.userId }).distinct()
                val fetchedNames = mutableMapOf<Long, String>()

                withContext(Dispatchers.IO) {
                    allUserIds.forEach { uid ->
                        try {
                            val uRes = RetrofitClient.api.getUserById(uid)
                            if (uRes.isSuccessful) {
                                val u = uRes.body()
                                val name = u?.userName?.takeIf { it.isNotBlank() }
                                    ?: u?.email?.takeIf { it.isNotBlank() }
                                if (!name.isNullOrBlank()) {
                                    fetchedNames[uid] = name
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }

                userNames = userNames + fetchedNames

                // 4. Lấy thông tin chương nếu comment gốc có chapterId
                rootComment.chapterId?.let { chapId ->
                    if (!chapterNumbers.containsKey(chapId)) {
                        try {
                            val chapRes = RetrofitClient.api.getChapterById(chapId)
                            if (chapRes.isSuccessful && chapRes.body() != null) {
                                chapterNumbers = chapterNumbers + (chapId to chapRes.body()!!.chapterNumber)
                            }
                        } catch (_: Exception) {}
                    }
                }

                val enrichedRoot = rootComment.copy(
                    userName = userNames[rootComment.userId]
                        ?: rootComment.userName?.takeIf { it.isNotBlank() }
                        ?: "User #${rootComment.userId}"
                )

                val sortedReplies = rawReplies
                    .sortedBy { it.createdAt }
                    .map { rep ->
                        rep.copy(
                            userName = userNames[rep.userId]
                                ?: rep.userName?.takeIf { it.isNotBlank() }
                                ?: "User #${rep.userId}"
                        )
                    }

                mainComment = enrichedRoot
                replyList = sortedReplies
                replyingTo = enrichedRoot

                // 5. Cập nhật số lượt thích & trạng thái đã thích
                val allComments = listOf(enrichedRoot) + sortedReplies
                likeCounts = allComments.associate { it.commentId to it.likeCount }

                val likedIds = mutableSetOf<Long>()
                allComments.forEach { item ->
                    try {
                        val statusRes = RetrofitClient.api.getCommentLikeStatus(item.commentId)
                        if (statusRes.isSuccessful && statusRes.body()?.liked == true) {
                            likedIds.add(item.commentId)
                        }
                    } catch (_: Exception) {}
                }
                likedComments = likedIds
            } catch (e: Exception) {
                error = e.message ?: "Lỗi kết nối đến máy chủ"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleLike(target: CommentResponse) {
        val id = target.commentId
        if (id in likingComments) return

        scope.launch {
            likingComments = likingComments + id
            try {
                val currentlyLiked = id in likedComments
                val response = if (currentlyLiked) {
                    RetrofitClient.api.unlikeComment(id)
                } else {
                    RetrofitClient.api.likeComment(id)
                }

                if (response.isSuccessful) {
                    likedComments = if (currentlyLiked) {
                        likedComments - id
                    } else {
                        likedComments + id
                    }

                    val currentCount = likeCounts[id] ?: target.likeCount
                    likeCounts = likeCounts + (
                            id to if (currentlyLiked) {
                                (currentCount - 1).coerceAtLeast(0L)
                            } else {
                                currentCount + 1
                            }
                            )
                    onLikeComment(target)
                }
            } catch (_: Exception) {
            } finally {
                likingComments = likingComments - id
            }
        }
    }

    LaunchedEffect(comment.commentId) {
        try {
            val userRes = RetrofitClient.api.getCurrentUser()
            if (userRes.isSuccessful) {
                userRes.body()?.userId?.let { currentUserId = it }
            }
        } catch (_: Exception) {}

        loadCommentDetail()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ==================================================
            // HEADER (HIỆN CHỮ "Bình luận")
            // ==================================================
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
                    text = "Bình luận",
                    color = Color.White,
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 40.dp, end = 8.dp)
                )
            }

            // ==================================================
            // NỘI DUNG CHÍNH
            // ==================================================
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
                            modifier = Modifier.clickable { loadCommentDetail() }
                        )
                    }
                }

                else -> {
                    val root = mainComment

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        // BÌNH LUẬN GỐC
                        item(key = "main_comment_${root.commentId}") {
                            CommentDetailItem(
                                comment = root,
                                displayUserName = userNames[root.userId]
                                    ?: root.userName?.takeIf { it.isNotBlank() }
                                    ?: "User #${root.userId}",
                                chapterNumber = root.chapterId?.let { chapterNumbers[it] },
                                isMainComment = true,
                                isLiked = root.commentId in likedComments,
                                likeCount = likeCounts[root.commentId] ?: root.likeCount,
                                isLiking = root.commentId in likingComments,
                                onLikeClick = { toggleLike(root) },
                                onReplyClick = {
                                    replyingTo = root
                                    openReplyInput()
                                },
                                onReportClick = {
                                    selectedComment = root
                                    showReportSheet = true
                                }
                            )
                        }

                        // DANH SÁCH TRẢ LỜI
                        if (replyList.isEmpty()) {
                            item(key = "empty_replies") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 48.dp, bottom = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
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
                                items = replyList,
                                key = { it.commentId }
                            ) { reply ->
                                CommentDetailItem(
                                    comment = reply,
                                    displayUserName = userNames[reply.userId]
                                        ?: reply.userName?.takeIf { it.isNotBlank() }
                                        ?: "User #${reply.userId}",
                                    isMainComment = false,
                                    isLiked = reply.commentId in likedComments,
                                    likeCount = likeCounts[reply.commentId] ?: reply.likeCount,
                                    isLiking = reply.commentId in likingComments,
                                    currentUserId = currentUserId,
                                    onLikeClick = { toggleLike(reply) },
                                    onReplyClick = {
                                        replyingTo = root
                                        val username = userNames[reply.userId]
                                            ?: reply.userName?.takeIf { it.isNotBlank() }
                                            ?: "User #${reply.userId}"
                                        val mention = "@$username "
                                        replyText = TextFieldValue(
                                            text = mention,
                                            selection = TextRange(mention.length)
                                        )
                                        openReplyInput()
                                    },
                                    onReportClick = {
                                        selectedComment = reply
                                        showReportSheet = true
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            try {
                                                val res = RetrofitClient.api.deleteComment(reply.commentId)
                                                if (res.isSuccessful) {
                                                    loadCommentDetail()
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("CommentDetail", "Lỗi xóa reply", e)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ==================================================
            // THANH NHẬP TRẢ LỜI
            // ==================================================
            CommentReplyInput(
                text = replyText,
                replyTo = replyingTo,
                enabled = !isSending,
                focusRequester = focusRequester,
                onTextChange = { replyText = it },
                onSendClick = send@{
                    if (replyText.text.isBlank() || isSending) return@send

                    val parent = replyingTo
                    val uid = currentUserId ?: sessionManager.getUserId()
                    if (uid <= 0L) {
                        error = "Vui lòng đăng nhập để gửi bình luận."
                        return@send
                    }
                    val contentToSend = replyText.text.trim()

                    scope.launch {
                        isSending = true
                        error = null
                        try {
                            val request = CommentCreateRequest(
                                userId = uid,
                                bookId = parent.bookId,
                                parentCommentId = parent.commentId,
                                chapterId = parent.chapterId,
                                content = contentToSend
                            )

                            val response = RetrofitClient.api.createComment(request)
                            if (response.isSuccessful) {
                                onSendReply(contentToSend, parent)
                                replyText = TextFieldValue("")
                                replyingTo = mainComment
                                focusManager.clearFocus()
                                keyboardController?.hide()

                                response.body()?.let { created ->
                                    val displayName = userNames[uid]
                                        ?: sessionManager.getUserName()
                                        ?: "User #$uid"
                                    replyList = replyList + created.copy(userName = displayName)
                                }

                                loadCommentDetail()
                            } else {
                                error = "Không thể gửi trả lời (${response.code()})"
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

        if (showReportSheet && selectedComment != null) {
            val targetReport = selectedComment!!
            ReportBottomSheet(
                username = userNames[targetReport.userId]
                    ?: targetReport.userName?.takeIf { it.isNotBlank() }
                    ?: "User #${targetReport.userId}",
                bookName = "Truyện #${targetReport.bookId}",
                contentType = "Bình luận",
                onDismiss = {
                    showReportSheet = false
                    selectedComment = null
                },
                onSubmit = {
                    onReportComment(targetReport)
                    showReportSheet = false
                    selectedComment = null
                }
            )
        }
    }
}

@Composable
private fun CommentDetailItem(
    comment: CommentResponse,
    displayUserName: String,
    chapterNumber: Int? = null,
    isMainComment: Boolean,
    isLiked: Boolean,
    likeCount: Long,
    isLiking: Boolean,
    currentUserId: Long? = null,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    val avatarSize = if (isMainComment) 44.dp else 30.dp
    val contentStart = 56.dp
    val isMyComment = currentUserId != null && comment.userId == currentUserId

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isMainComment) Color(0xFF202020)
                else Color(0xFF303030)
            )
            .padding(
                start = 32.dp,
                end = 32.dp,
                top = if (isMainComment) 10.dp else 6.dp,
                bottom = if (isMainComment) 10.dp else 4.dp
            )
    ) {
        // --- HEADER (Avatar, Tên, Nút Xóa) ---
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
                    fontSize = if (isMainComment) 17.sp else 15.sp,
                    maxLines = 1
                )

                if (isMainComment && chapterNumber != null) {
                    Text(
                        text = "Chương $chapterNumber",
                        color = Color(0xFF858585),
                        fontSize = 12.sp
                    )
                }
            }

            if (!isMainComment && isMyComment) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa bình luận",
                    tint = Color(0xFFE53935),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDeleteClick() }
                )
            }
        }

        // --- NỘI DUNG BÌNH LUẬN ---
        Text(
            text = comment.content,
            color = Color.White,
            fontSize = if (isMainComment) 15.sp else 13.sp,
            lineHeight = if (isMainComment) 20.sp else 17.sp,
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
                    text = formatCommentDetailTime(comment.createdAt.trim()),
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

            Row(
                modifier = Modifier
                    .wrapContentWidth(Alignment.End)
                    .clickable(enabled = !isLiking) { onLikeClick() }
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

@Composable
private fun CommentReplyInput(
    text: TextFieldValue,
    replyTo: CommentResponse?,
    enabled: Boolean,
    focusRequester: FocusRequester,
    onTextChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF292929))
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
                    .clickable(enabled = enabled && text.text.isNotBlank()) {
                        onSendClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Gửi",
                    tint = if (text.text.isNotBlank() && enabled) Color.White else Color(0xFF666666),
                    modifier = Modifier.size(27.dp)
                )
            }
        }
    }
}

private fun formatCommentDetailTime(createdAt: String): String {
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
    } catch (_: Exception) {
        createdAt
    }
}