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

    LaunchedEffect(bookId) {

        viewModel.loadComments(bookId)
    }


    // ==================================================
    // INPUT
    // ==================================================

    var commentText by remember {
        mutableStateOf("")
    }


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

                        items =
                            displayedComments,

                        key = {
                            it.commentId
                        }

                    ) { comment ->

                        CommentItem(

                            comment =
                                comment,

                            onLikeClick = {

                                viewModel.toggleLike(
                                    comment.commentId
                                )
                            },

                            onReportClick = {

                                selectedReportComment =
                                    comment

                                showReportSheet =
                                    true
                            },

                            onCommentDetailClick = {

                                onCommentDetailClick(
                                    comment.commentId
                                )
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
            // INPUT
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF303030)
                    )
                    .padding(
                        start = 6.dp,
                        end = 6.dp,
                        bottom = 8.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                TextField(
                    value =
                        commentText,

                    onValueChange = {
                        commentText = it
                    },

                    modifier =
                        Modifier
                            .weight(1f)
                            .height(56.dp),

                    placeholder = {

                        Text(
                            text =
                                "Thêm bình luận",

                            color =
                                Color(0xFFAAAAAA),

                            fontSize =
                                16.sp
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Comment,

                            contentDescription =
                                "Bình luận",

                            tint =
                                Color.White
                        )
                    },

                    trailingIcon = {

                        IconButton(

                            onClick = {

                                // TODO:
                                // POST COMMENT BACKEND

                                commentText = ""
                            },

                            enabled =
                                commentText
                                    .isNotBlank()
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Send,

                                contentDescription =
                                    "Gửi",

                                tint =
                                    if (
                                        commentText
                                            .isNotBlank()
                                    ) {
                                        Color.White
                                    } else {
                                        Color(0xFF666666)
                                    }
                            )
                        }
                    },

                    singleLine = true,

                    colors =
                        TextFieldDefaults.colors(

                            focusedContainerColor =
                                Color(0xFF303030),

                            unfocusedContainerColor =
                                Color(0xFF303030),

                            focusedIndicatorColor =
                                Color.Transparent,

                            unfocusedIndicatorColor =
                                Color.Transparent,

                            cursorColor =
                                Color.White,

                            focusedTextColor =
                                Color.White,

                            unfocusedTextColor =
                                Color.White
                        )
                )
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

    onLikeClick: () -> Unit,

    onReportClick: () -> Unit,

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


            Icon(
                imageVector =
                    Icons.Default.Flag,

                contentDescription =
                    "Báo cáo",

                tint =
                    Color(0xFF999999),

                modifier = Modifier
                    .size(20.dp)
                    .clickable {

                        onReportClick()
                    }
            )
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
        // CHAPTER
        // ==================================================

        if (
            comment.chapterNumber != null
        ) {

            Text(
                text =
                    buildString {

                        append(
                            "Chương "
                        )

                        append(
                            comment.chapterNumber
                        )

                        if (
                            !comment.chapterTitle
                                .isNullOrBlank()
                        ) {

                            append(
                                ": "
                            )

                            append(
                                comment.chapterTitle
                            )
                        }
                    },

                color =
                    Color(0xFF888888),

                fontSize =
                    12.sp,

                maxLines =
                    1,

                modifier =
                    Modifier.padding(
                        start = 52.dp,
                        top = 5.dp
                    )
            )
        }


        // ==================================================
        // LIKE + REPLY
        // ==================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 52.dp,
                        top = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            // ----------------------------------------------
            // LIKE
            // ----------------------------------------------

            Row(
                modifier =
                    Modifier.clickable {

                        onLikeClick()
                    },

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ThumbUp,

                    contentDescription =
                        "Thích",

                    tint =
                        if (comment.isLiked) {
                            Color(0xFF42A5F5)
                        } else {
                            Color(0xFF888888)
                        },

                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text =
                        comment.likeCount
                            .toString(),

                    color =
                        if (comment.isLiked) {
                            Color(0xFF42A5F5)
                        } else {
                            Color(0xFF888888)
                        },

                    fontSize =
                        12.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.width(18.dp)
            )


            // ----------------------------------------------
            // REPLY
            // ----------------------------------------------

            Row(
                modifier =
                    Modifier.clickable {

                        onCommentDetailClick()
                    },

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Comment,

                    contentDescription =
                        "Bình luận",

                    tint =
                        Color(0xFF888888),

                    modifier =
                        Modifier.size(19.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )

                Text(
                    text =
                        comment.replyCount
                            .toString(),

                    color =
                        Color(0xFF888888),

                    fontSize =
                        12.sp
                )
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
}


// ==========================================================
// FORMAT TIME
// ==========================================================

private fun formatCommentTime(
    time: String
): String {

    return try {

        // Tạm thời giữ nguyên timestamp
        // từ Backend.
        time

    } catch (
        e: Exception
    ) {

        time
    }
}