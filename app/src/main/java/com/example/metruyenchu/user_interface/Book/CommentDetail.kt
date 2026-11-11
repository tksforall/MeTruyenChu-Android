package com.example.metruyenchu.user_interface.Book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.api.CommentResponse
import com.example.metruyenchu.api.UserResponse
import com.example.metruyenchu.user_interface.Book.components.ReportBottomSheet


// ==========================================================
// COMMENT DETAIL
// ==========================================================

@Composable
fun CommentDetail(
    comment: CommentResponse,
    replies: List<CommentResponse> = emptyList(),

    users: Map<Long, UserResponse> = emptyMap(),

    chapters: Map<Long, ChapterResponse> = emptyMap(),

    onBack: () -> Unit = {},

    onLikeComment: (CommentResponse) -> Unit = {},

    onReportComment: (CommentResponse) -> Unit = {},

    onSendReply: (String, CommentResponse) -> Unit = {} as (String, CommentResponse) -> Unit
) {

    var replyText by remember {
        mutableStateOf("")
    }

    var showReportSheet by remember {
        mutableStateOf(false)
    }

    var reportComment by remember {
        mutableStateOf<CommentResponse?>(null)
    }


    // ======================================================
    // MÀU
    // ======================================================

    val backgroundColor = Color(0xFF2D2D2D)

    val mainCommentColor = Color(0xFF212121)

    val replyCommentColor = Color(0xFF2D2D2D)

    val textColor = Color(0xFFF5F5F5)

    val secondaryTextColor = Color(0xFF858585)

    val iconColor = Color(0xFF9A9A9A)


    // ======================================================
    // KÍCH THƯỚC
    // ======================================================

    val horizontalPadding = 20.dp

    val avatarSize = 34.dp

    val avatarTextGap = 9.dp

    val usernameSize = 16.sp

    val timeSize = 12.sp

    val contentSize = 15.sp

    val contentLineHeight = 21.sp


    // ======================================================
    // USER COMMENT CHÍNH
    // ======================================================

    val mainUser =
        users[comment.userId]

    val mainChapter =
        comment.chapterId?.let {
            chapters[it]
        }


    // ======================================================
    // ROOT
    // ======================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {

            // ==================================================
            // HEADER
            // ==================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
                    .height(40.dp)
                    .padding(horizontal = 18.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(50.dp)
                )

                Text(
                    text = "Chi tiết bình luận",
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
            }


            // ==================================================
            // COMMENT + REPLIES
            // ==================================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {

                // ==================================================
                // COMMENT CHÍNH
                // ==================================================

                item {

                    MainComment(
                        comment = comment,

                        user = mainUser,

                        chapter = mainChapter,

                        backgroundColor = mainCommentColor,
                        textColor = textColor,
                        secondaryTextColor = secondaryTextColor,
                        iconColor = iconColor,

                        horizontalPadding = horizontalPadding,
                        avatarSize = avatarSize,
                        avatarTextGap = avatarTextGap,

                        usernameSize = usernameSize,
                        timeSize = timeSize,
                        contentSize = contentSize,
                        contentLineHeight = contentLineHeight,

                        onLikeClick = {
                            onLikeComment(comment)
                        }
                    )
                }


                // ==================================================
                // REPLIES
                // ==================================================

                items(
                    items = replies,
                    key = {
                        it.commentId
                    }
                ) { reply ->

                    ReplyComment(
                        comment = reply,

                        user = users[reply.userId],

                        chapter = reply.chapterId?.let {
                            chapters[it]
                        },

                        backgroundColor = replyCommentColor,
                        textColor = textColor,
                        secondaryTextColor = secondaryTextColor,
                        iconColor = iconColor,

                        horizontalPadding = horizontalPadding,
                        avatarSize = avatarSize,
                        avatarTextGap = avatarTextGap,

                        usernameSize = usernameSize,
                        timeSize = timeSize,
                        contentSize = contentSize,
                        contentLineHeight = contentLineHeight,

                        onLikeClick = {
                            onLikeComment(reply)
                        },

                        onReportClick = {

                            reportComment = reply

                            showReportSheet = true
                        }
                    )
                }
            }


            // ==================================================
            // Ô NHẬP TRẢ LỜI
            // ==================================================

            ReplyInput(
                value = replyText,

                onValueChange = {
                    replyText = it
                },

                onSend = {

                    if (replyText.isNotBlank()) {

                        onSendReply(
                            replyText.trim(),
                            comment
                        )

                        replyText = ""
                    }
                }
            )
        }


        // ======================================================
        // REPORT BOTTOM SHEET
        // ======================================================

        if (showReportSheet) {

            ReportBottomSheet(

                onDismiss = {

                    showReportSheet = false

                    reportComment = null
                },

                onSubmit = { reason ->

                    reportComment?.let {

                        onReportComment(it)
                    }

                    showReportSheet = false

                    reportComment = null
                }
            )
        }
    }
}


// ==========================================================
// COMMENT CHÍNH
// ==========================================================

@Composable
private fun MainComment(
    comment: CommentResponse,

    user: UserResponse?,

    chapter: ChapterResponse?,

    backgroundColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    iconColor: Color,

    horizontalPadding: Dp,
    avatarSize: Dp,
    avatarTextGap: Dp,

    usernameSize: TextUnit,
    timeSize: TextUnit,

    contentSize: TextUnit,
    contentLineHeight: TextUnit,

    onLikeClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                horizontal = horizontalPadding,
                vertical = 9.dp
            )
    ) {

        // ==================================================
        // USER
        // ==================================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            UserAvatar(
                avatarUrl = user?.avatarUrl,
                size = avatarSize
            )

            Spacer(
                modifier = Modifier.width(avatarTextGap)
            )

            Text(
                text = user?.userName
                    ?: "Người dùng",

                color = textColor,

                fontSize = usernameSize,

                fontWeight = FontWeight.Normal
            )

            Text(
                text = " · ${formatCommentTime(comment.createdAt)}",

                color = secondaryTextColor,

                fontSize = timeSize
            )
        }


        // ==================================================
        // CONTENT
        // ==================================================

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = comment.content,

            color = textColor,

            fontSize = contentSize,

            lineHeight = contentLineHeight,

            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 43.dp)
        )


        // ==================================================
        // CHAPTER + LIKE
        // ==================================================

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 43.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            if (chapter != null) {

                Text(
                    text = "Chương ${chapter.chapterNumber}",

                    color = secondaryTextColor,

                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )


            Icon(
                imageVector = Icons.Default.ThumbUp,

                contentDescription = "Thích",

                tint = iconColor,

                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onLikeClick()
                    }
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = comment.likeCount.toString(),

                color = secondaryTextColor,

                fontSize = 14.sp
            )
        }
    }
}


// ==========================================================
// REPLY COMMENT
// ==========================================================

@Composable
private fun ReplyComment(
    comment: CommentResponse,

    user: UserResponse?,

    chapter: ChapterResponse?,

    backgroundColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    iconColor: Color,

    horizontalPadding: Dp,
    avatarSize: Dp,
    avatarTextGap: Dp,

    usernameSize: TextUnit,
    timeSize: TextUnit,

    contentSize: TextUnit,
    contentLineHeight: TextUnit,

    onLikeClick: () -> Unit,

    onReportClick: () -> Unit
) {

    val username =
        user?.userName ?: "Người dùng"


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                horizontal = horizontalPadding,
                vertical = 9.dp
            )
    ) {

        // ==================================================
        // USER
        // ==================================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            UserAvatar(
                avatarUrl = user?.avatarUrl,
                size = avatarSize
            )

            Spacer(
                modifier = Modifier.width(avatarTextGap)
            )

            Text(
                text = username,

                color = textColor,

                fontSize = usernameSize
            )

            Text(
                text =
                    " · ${formatCommentTime(comment.createdAt)}",

                color = secondaryTextColor,

                fontSize = timeSize
            )
        }


        // ==================================================
        // CONTENT
        // ==================================================

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = comment.content,

            color = textColor,

            fontSize = contentSize,

            lineHeight = contentLineHeight,

            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 43.dp)
        )


        // ==================================================
        // REPLY + LIKE + REPORT
        // ==================================================

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 43.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Trả lời $username",

                color = secondaryTextColor,

                fontSize = 14.sp
            )


            Spacer(
                modifier = Modifier.weight(1f)
            )


            // ==================================================
            // LIKE
            // ==================================================

            Icon(
                imageVector = Icons.Default.ThumbUp,

                contentDescription = "Thích",

                tint = iconColor,

                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onLikeClick()
                    }
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = comment.likeCount.toString(),

                color = secondaryTextColor,

                fontSize = 14.sp
            )


            Spacer(
                modifier = Modifier.width(20.dp)
            )


            // ==================================================
            // REPORT
            // ==================================================

            Icon(
                imageVector = Icons.Default.Flag,

                contentDescription = "Báo cáo",

                tint = iconColor,

                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onReportClick()
                    }
            )
        }
    }
}


// ==========================================================
// AVATAR
// ==========================================================

@Composable
private fun UserAvatar(
    avatarUrl: String?,
    size: Dp
) {

    if (!avatarUrl.isNullOrBlank()) {

        AsyncImage(

            model = avatarUrl,

            contentDescription = "Avatar",

            modifier = Modifier
                .size(size)
                .clip(CircleShape),

            contentScale = ContentScale.Crop
        )

    } else {

        DefaultAvatar(
            size = size
        )
    }
}


// ==========================================================
// DEFAULT AVATAR
// ==========================================================

@Composable
private fun DefaultAvatar(
    size: Dp
) {

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF9E9E9E)),

        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(size * 0.54f)
                .clip(CircleShape)
                .background(Color(0xFFE1E1E1))
        )
    }
}


// ==========================================================
// REPLY INPUT
// ==========================================================

@Composable
private fun ReplyInput(
    value: String,

    onValueChange: (String) -> Unit,

    onSend: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF292929))
            .imePadding()
            .padding(
                start = 4.dp,
                end = 4.dp
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF292929))
                .clip(
                    RoundedCornerShape(4.dp)
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Comment,

                contentDescription = "Bình luận",

                tint = Color.White,

                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(27.dp)
            )


            TextField(
                value = value,

                onValueChange = onValueChange,

                placeholder = {

                    Text(
                        text = "Thêm trả lời",

                        color = Color(0xFF888888),

                        fontSize = 15.sp
                    )
                },

                modifier = Modifier.weight(1f),

                singleLine = true,

                colors = TextFieldDefaults.colors(

                    focusedTextColor = Color.White,

                    unfocusedTextColor = Color.White,

                    focusedContainerColor =
                        Color.Transparent,

                    unfocusedContainerColor =
                        Color.Transparent,

                    disabledContainerColor =
                        Color.Transparent,

                    cursorColor = Color.White,

                    focusedIndicatorColor =
                        Color.Transparent,

                    unfocusedIndicatorColor =
                        Color.Transparent
                )
            )


            IconButton(
                onClick = onSend,

                modifier = Modifier.size(40.dp)
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


// ==========================================================
// FORMAT TIME
// ==========================================================

private fun formatCommentTime(
    createdAt: String
): String {

    // Tạm thời giữ nguyên timestamp nếu
    // chưa muốn xử lý LocalDateTime.

    return createdAt
}