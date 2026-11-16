package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metruyenchu.user_interface.Book.components.ReportBottomSheet
import com.example.metruyenchu.home.CommentViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.metruyenchu.api.CommentCreateRequest



@Composable
fun BookComment(
    bookId: Long,

    onBack: () -> Unit = {},

    onCommentDetailClick: (Long) -> Unit = {}
) {

    // ==================================================
    // VIEWMODEL
    // ==================================================

    val viewModel: CommentViewModel =
        viewModel()

    val comments by
    viewModel.comments.collectAsState()

    val isLoading by
    viewModel.isLoading.collectAsState()

    val error by
    viewModel.error.collectAsState()


    // ==================================================
    // LOAD COMMENT
    // ==================================================

    var currentUserId by remember { mutableStateOf<Long?>(null) }
    var targetChapterId by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(bookId) {
        viewModel.loadComments(bookId)
        try {
            val userRes = RetrofitClient.api.getCurrentUser()
            if (userRes.isSuccessful) {
                currentUserId = userRes.body()?.userId
            }
        } catch (_: Exception) {}

        // 1. Ưu tiên lấy chương đang đọc dở từ lịch sử đọc của user
        try {
            val historyRes = RetrofitClient.api.getMyReadingHistory()
            if (historyRes.isSuccessful) {
                val bookHistory = historyRes.body()
                    .orEmpty()
                    .find { it.bookId == bookId }
                if (bookHistory != null) {
                    targetChapterId = bookHistory.chapterId
                }
            }
        } catch (_: Exception) {}

        // 2. Nếu chưa đọc chương nào -> tự động lấy Chương 1 của truyện
        if (targetChapterId == null) {
            try {
                val chaptersRes = RetrofitClient.api.getChaptersByBookId(bookId)
                if (chaptersRes.isSuccessful) {
                    val chapters = chaptersRes.body().orEmpty()
                    val chapterOne = chapters.find { it.chapterNumber == 1 }
                        ?: chapters.minByOrNull { it.chapterNumber }
                    targetChapterId = chapterOne?.chapterId
                }
            } catch (_: Exception) {}
        }
    }


    // ==================================================
    // INPUT
    // ==================================================

    var commentText by remember {
        mutableStateOf("")
    }

    var isSending by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    // ==================================================
    // SORT
    // ==================================================

    var selectedSort by remember {
        mutableStateOf("Mới nhất")
    }


    // ==================================================
    // REPORT
    // ==================================================

    var showReportSheet by remember {
        mutableStateOf(false)
    }


    var selectedReportComment by remember {
        mutableStateOf<CommentUiModel?>(null)
    }


    // ==================================================
    // SORT DATA
    // ==================================================

    val displayedComments = when (selectedSort) {

        "Cũ nhất" ->
            comments.sortedBy {
                it.createdAt
            }

        "Nhiều thích" ->
            comments.sortedByDescending {
                it.likeCount
            }

        else ->
            comments.sortedByDescending {
                it.createdAt
            }
    }


    // ==================================================
    // SCREEN
    // ==================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF303030)
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize()
        ) {

            // ==================================================
            // TOP BAR
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        Color(0xFF282828)
                    )
            ) {

                IconButton(
                    onClick = onBack,

                    modifier =
                        Modifier.align(
                            Alignment.CenterStart
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Quay lại",

                        tint =
                            Color.White,

                        modifier =
                            Modifier.size(30.dp)
                    )
                }


                Text(
                    text = "Bình luận",

                    color =
                        Color.White,

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Medium,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        )
                )
            }


            // ==================================================
            // COMMENT AREA
            // ==================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(
                            top = 10.dp,
                            bottom = 10.dp
                        )
                ) {

                    // ==========================================
                    // SORT
                    // ==========================================

                    item {

                        SortCommentBar(
                            selectedSort =
                                selectedSort,

                            onSelected = {
                                selectedSort = it
                            }
                        )
                    }


                    // ==========================================
                    // ERROR
                    // ==========================================

                    if (error != null) {

                        item {

                            Text(
                                text =
                                    error ?: "",

                                color =
                                    Color(0xFFFF7777),

                                fontSize =
                                    14.sp,

                                modifier =
                                    Modifier.padding(
                                        20.dp
                                    )
                            )
                        }
                    }


                    // ==========================================
                    // LOADING
                    // ==========================================

                    if (isLoading) {

                        item {

                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            30.dp
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                CircularProgressIndicator(
                                    color =
                                        Color.White
                                )
                            }
                        }
                    }


                    // ==========================================
                    // COMMENTS
                    // ==========================================

                    items(
                        items = displayedComments,
                        key = { it.commentId }
                    ) { comment ->
                        val displayComment = comment.copy(
                            userName = comment.userName.takeIf { it.isNotBlank() }
                                ?: "User #${comment.userId}"
                        )

                        CommentItem(
                            comment = displayComment,
                            isMyComment = currentUserId != null && comment.userId == currentUserId,
                            onLikeClick = {
                                viewModel.toggleLike(comment.commentId)
                            },
                            onReportClick = {
                                selectedReportComment = displayComment
                                showReportSheet = true
                            },
                            onDeleteClick = {
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.deleteComment(comment.commentId)
                                        if (response.isSuccessful) {
                                            viewModel.loadComments(bookId)
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("BookComment", "Lỗi xóa bình luận", e)
                                    }
                                }
                            },
                            onCommentDetailClick = {
                                onCommentDetailClick(comment.commentId)
                            }
                        )
                    }


                    item {

                        Spacer(
                            modifier =
                                Modifier.height(10.dp)
                        )
                    }
                }
            }




            // ==================================================
            // INPUT (GIỐNG BÊN REVIEW COMMENT)
            // ==================================================

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
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Thêm bình luận",
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
                            .clickable(enabled = commentText.isNotBlank() && !isSending && currentUserId != null) {
                                val contentToSend = commentText.trim()
                                val uid = currentUserId ?: return@clickable
                                if (contentToSend.isEmpty()) return@clickable

                                scope.launch {
                                    isSending = true
                                    try {
                                        val request = CommentCreateRequest(
                                            userId = uid,
                                            bookId = bookId,
                                            parentCommentId = null,
                                            chapterId = targetChapterId,
                                            content = contentToSend
                                        )

                                        val response = RetrofitClient.api.createComment(request)
                                        if (response.isSuccessful) {
                                            commentText = ""
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                            viewModel.loadComments(bookId)
                                        } else {
                                            android.util.Log.e(
                                                "BookComment",
                                                "Lỗi gửi bình luận: HTTP ${response.code()} - ${response.errorBody()?.string()}"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("BookComment", "Exception gửi bình luận", e)
                                    } finally {
                                        isSending = false
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Gửi",
                            tint = if (commentText.isNotBlank() && !isSending) Color.White else Color(0xFF666666),
                            modifier = Modifier.size(27.dp)
                        )
                    }
                }
            }
        }


        // ==================================================
        // REPORT
        // ==================================================

        if (
            showReportSheet &&
            selectedReportComment != null
        ) {

            ReportBottomSheet(

                username =
                    selectedReportComment!!
                        .userName,

                contentType =
                    "Bình luận",

                onDismiss = {

                    showReportSheet =
                        false

                    selectedReportComment =
                        null
                },

                onSubmit = {

                    showReportSheet =
                        false

                    selectedReportComment =
                        null
                }
            )
        }
    }
}


// ==========================================================
// SORT BAR
// ==========================================================

@Composable
private fun SortCommentBar(
    selectedSort: String,

    onSelected: (String) -> Unit
) {

    val options =
        listOf(
            "Cũ nhất",
            "Mới nhất",
            "Nhiều thích"
        )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 6.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(24.dp)
                )
                .background(
                    Color(0xFF4A4A4A)
                )
        ) {

            options.forEachIndexed {
                    index,
                    option ->

                val selected =
                    option == selectedSort

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {

                            onSelected(
                                option
                            )
                        }
                        .background(
                            if (selected) {
                                Color(0xFFE0E0E0)
                            } else {
                                Color.Transparent
                            }
                        )
                        .padding(
                            vertical = 9.dp
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            option,

                        color =
                            if (selected) {
                                Color(0xFF333333)
                            } else {
                                Color.White
                            },

                        fontSize =
                            14.sp
                    )
                }
            }
        }
    }
}


// ==========================================================
// COMMENT ITEM
// ==========================================================

@Composable
private fun CommentItem(
    comment: CommentUiModel,
    isMyComment: Boolean = false,
    onLikeClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    onCommentDetailClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = 8.dp
            )
    ) {

        // ==================================================
        // USER
        // ==================================================

        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ----------------------------------------------
            // AVATAR
            // ----------------------------------------------

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF666666)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                if (
                    comment.avatarUrl
                        .isNullOrBlank()
                ) {

                    Text(
                        text =
                            comment.userName
                                .firstOrNull()
                                ?.uppercase()
                                ?: "U",

                        color =
                            Color.White,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                } else {

                    // ------------------------------------------
                    // Tạm hiển thị chữ cái.
                    // Nếu project đã có Coil thì sau đó đổi
                    // sang AsyncImage.
                    // ------------------------------------------

                    Text(
                        text =
                            comment.userName
                                .firstOrNull()
                                ?.uppercase()
                                ?: "U",

                        color =
                            Color.White,

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )


            // ----------------------------------------------
            // USERNAME + TIME
            // ----------------------------------------------

            Column {

                Text(
                    text =
                        comment.userName,

                    color =
                        Color.White,

                    fontSize =
                        15.sp,

                    fontWeight =
                        FontWeight.Medium
                )

                Text(
                    text =
                        formatCommentTime(
                            comment.createdAt
                        ),

                    color =
                        Color(0xFF888888),

                    fontSize =
                        12.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isMyComment) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa bình luận",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onReportClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Báo cáo",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }


        // ==================================================
        // CONTENT
        // ==================================================

        Text(
            text =
                comment.content,

            color =
                Color.White,

            fontSize =
                16.sp,

            lineHeight =
                21.sp,

            modifier =
                Modifier.padding(
                    start = 52.dp,
                    top = 7.dp
                )
        )



        // ==================================================
        // CHAPTER + LIKE + REPLY (CÙNG 1 HÀNG)
        // ==================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 52.dp,
                    top = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ----------------------------------------------
            // CHAPTER (BÊN TRÁI - NHỎ GỌN)
            // ----------------------------------------------
            Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                if (comment.chapterNumber != null) {
                    Text(
                        text = buildString {
                            append("Chương ")
                            append(comment.chapterNumber)
                            if (!comment.chapterTitle.isNullOrBlank()) {
                                append(": ")
                                append(comment.chapterTitle)
                            }
                        },
                        color = Color(0xFF888888),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // ----------------------------------------------
            // LIKE (BÊN PHẢI)
            // ----------------------------------------------
            Row(
                modifier = Modifier.clickable {
                    onLikeClick()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Thích",
                    tint = if (comment.isLiked) {
                        Color(0xFF42A5F5)
                    } else {
                        Color(0xFF888888)
                    },
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = comment.likeCount.toString(),
                    color = if (comment.isLiked) {
                        Color(0xFF42A5F5)
                    } else {
                        Color(0xFF888888)
                    },
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ----------------------------------------------
            // REPLY (BÊN PHẢI)
            // ----------------------------------------------
            Row(
                modifier = Modifier.clickable {
                    onCommentDetailClick()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Bình luận",
                    tint = Color(0xFF888888),
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = comment.replyCount.toString(),
                    color = Color(0xFF888888),
                    fontSize = 12.sp
                )
            }
        }
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Color(0xFF3D3D3D)
                    )
        )
    }



// ==========================================================
// FORMAT TIME
// ==========================================================

private fun formatCommentTime(
    time: String
): String {
    return try {
        val cleanTime = time.trim()
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
        time
    }
}