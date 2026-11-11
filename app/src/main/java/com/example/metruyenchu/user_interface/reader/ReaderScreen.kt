package com.example.metruyenchu.user_interface.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.user_interface.reader.components.ReaderReportBottomSheet
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged


// ==========================================================
// READER SCREEN
// ==========================================================

@Composable
fun ReaderScreen(
    chapter: ChapterResponse,

    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onChapterListClick: () -> Unit = {},
    onNightModeClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookOverviewClick: () -> Unit = {},

    onPreviousChapter: () -> Unit = {},
    onNextChapter: () -> Unit = {},

    onProgressChanged: (Double) -> Unit = {}
) {

    // ======================================================
    // READER CONFIG
    // ======================================================

    val textColor = Color.White

    val lineHeight = 1.7f
    val horizontalPadding = 12.dp

    // ======================================================
    // STATE
    // ======================================================

    var showReaderSheet by remember {
        mutableStateOf(false)
    }

    var selectedTab by remember {
        mutableStateOf("Thông tin")
    }

    var readerFontSize by remember {
        mutableStateOf(25f)
    }

    var readerBackgroundColor by remember {
        mutableStateOf(Color.Black)
    }

    var readerFont by remember {
        mutableStateOf("Times New Roman")
    }

    var showReaderReportSheet by remember {
        mutableStateOf(false)
    }

    var showDownloadDialog by remember {
        mutableStateOf(false)
    }

    var isBookmarked by remember {
        mutableStateOf(false)
    }

    // ======================================================
    // LAZY LIST STATE
    // ======================================================

    val listState = rememberLazyListState()

    // ======================================================
    // CHAPTER NAVIGATION LOCK
    //
    // Quan trọng:
    // Mỗi chapter có một lock riêng.
    //
    // Chapter 1 -> Chapter 2:
    // lock của Chapter 1 = true
    //
    // Khi Chapter 2 được load:
    // remember(chapter.chapterId) tạo state mới
    // => lock lại = false
    // ======================================================

    var nextChapterTriggered by remember(chapter.chapterId) {
        mutableStateOf(false)
    }

    var previousChapterTriggered by remember(chapter.chapterId) {
        mutableStateOf(false)
    }

    // Người dùng đã thực sự scroll xuống chưa?
    //
    // Dùng để tránh trường hợp vừa mở chapter đã ở item 0
    // rồi lập tức gọi onPreviousChapter().
    var userHasScrolledDown by remember(chapter.chapterId) {
        mutableStateOf(false)
    }

    // ======================================================
    // RESET VỊ TRÍ KHI CHUYỂN CHAPTER
    // ======================================================
    //
    // Đây là phần quan trọng để sửa lỗi:
    //
    // Chapter 1 đọc hết
    // -> load Chapter 2
    // -> Chapter 2 phải bắt đầu từ đầu
    //
    // Không được giữ vị trí scroll của Chapter 1.
    // ======================================================

    LaunchedEffect(chapter.chapterId) {

        listState.scrollToItem(0)

        nextChapterTriggered = false
        previousChapterTriggered = false
        userHasScrolledDown = false
    }

    // ======================================================
    // PROGRESS
    // ======================================================

    LaunchedEffect(listState, chapter.chapterId) {

        snapshotFlow {
            val totalItems =
                listState.layoutInfo.totalItemsCount

            val firstVisible =
                listState.firstVisibleItemIndex

            if (totalItems <= 1) {
                0.0
            } else {
                firstVisible.toDouble() /
                        (totalItems - 1).toDouble()
            }
        }
            .debounce(700)
            .distinctUntilChanged()
            .collectLatest { progress ->

                onProgressChanged(progress)
            }
    }

    // ======================================================
    // AUTO NEXT CHAPTER
    // ======================================================
    //
    // Chỉ chạy khi người dùng thực sự đang scroll.
    //
    // Khi item cuối cùng xuất hiện:
    //
    // Chapter 1
    //      ↓
    // scroll
    //      ↓
    // cuối chapter
    //      ↓
    // onNextChapter()
    //
    // nextChapterTriggered đảm bảo callback chỉ chạy 1 lần.
    // ======================================================

    LaunchedEffect(listState, chapter.chapterId) {

        snapshotFlow {

            val layoutInfo =
                listState.layoutInfo

            val totalItems =
                layoutInfo.totalItemsCount

            val lastVisibleItem =
                layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: -1

            val isScrolling =
                listState.isScrollInProgress

            Triple(
                totalItems,
                lastVisibleItem,
                isScrolling
            )
        }
            .distinctUntilChanged()
            .collectLatest { (totalItems, lastVisibleItem, isScrolling) ->

                if (totalItems <= 0) {
                    return@collectLatest
                }

                // Người dùng đã scroll xuống.
                if (lastVisibleItem > 0) {
                    userHasScrolledDown = true
                }

                val reachedEnd =
                    lastVisibleItem >= totalItems - 1

                if (
                    isScrolling &&
                    reachedEnd &&
                    userHasScrolledDown &&
                    !nextChapterTriggered
                ) {

                    // Khóa ngay trước khi gọi callback.
                    //
                    // Tránh snapshotFlow chạy lại nhiều lần
                    // trong lúc parent đang load chapter mới.
                    nextChapterTriggered = true

                    onNextChapter()
                }
            }
    }

    // ======================================================
    // PREVIOUS CHAPTER
    // ======================================================
    //
    // Khi người dùng đã đọc xuống:
    //
    // Chapter 2
    //      ↓
    // đọc xuống
    //      ↓
    // kéo ngược lên
    //      ↓
    // về item đầu tiên
    //      ↓
    // Chapter 1
    //
    // Không trigger ngay lúc chapter vừa mở vì
    // userHasScrolledDown = false.
    // ======================================================

    LaunchedEffect(listState, chapter.chapterId) {

        snapshotFlow {

            val firstIndex =
                listState.firstVisibleItemIndex

            val firstOffset =
                listState.firstVisibleItemScrollOffset

            val isScrolling =
                listState.isScrollInProgress

            Triple(
                firstIndex,
                firstOffset,
                isScrolling
            )
        }
            .distinctUntilChanged()
            .collectLatest { (firstIndex, firstOffset, isScrolling) ->

                if (
                    isScrolling &&
                    userHasScrolledDown &&
                    !previousChapterTriggered &&
                    !nextChapterTriggered &&
                    firstIndex == 0 &&
                    firstOffset == 0
                ) {

                    previousChapterTriggered = true

                    onPreviousChapter()
                }
            }
    }

    // ======================================================
    // CHAPTER DATA
    // ======================================================

    val chapterTitle =
        "Chương ${chapter.chapterNumber}: ${chapter.title.orEmpty()}"

    val paragraphs =
        chapter.content
            .orEmpty()
            .replace("\r\n", "\n")
            .split(
                Regex("""\n\s*\n""")
            )
            .map {
                it.trim()
            }
            .filter {
                it.isNotBlank()
            }

    // ======================================================
    // READER
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBackgroundColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(readerBackgroundColor)
        ) {

            // ==========================================
            // HEADER CỐ ĐỊNH
            // ==========================================

            ReaderTopBar(
                chapterTitle = chapterTitle,
                progress = "Chương ${chapter.chapterNumber}",
                textColor = textColor,
                onBack = onBack,
                onSettings = {
                    selectedTab = "Cài đặt"
                    showReaderSheet = true
                    onSettings()
                }
            )

            // ==========================================
            // CHỈ NỘI DUNG TRUYỆN ĐƯỢC SCROLL
            // ==========================================

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = 4.dp,
                    bottom = 40.dp
                )
            ) {

                items(paragraphs) { paragraph ->

                    Text(
                        text = paragraph,
                        color = textColor,
                        fontSize = readerFontSize.sp,
                        fontFamily = getReaderFont(readerFont),
                        fontWeight = FontWeight.Normal,
                        lineHeight = (readerFontSize * lineHeight).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(30.dp)
                    )
                }

                // Các item khác của m ở đây
            }
        }



        // ======================================================
        // READER CONTROL SHEET
        // ======================================================

        ReaderControlSheet(

            visible =
                showReaderSheet,

            selectedTab =
                selectedTab,

            onTabSelected = {
                selectedTab = it
            },

            fontSize =
                readerFontSize,

            onFontSizeChange = {
                readerFontSize = it
            },

            backgroundColor =
                readerBackgroundColor,

            onBackgroundColorChange = {
                readerBackgroundColor = it
            },

            selectedFont =
                readerFont,

            onFontChange = {
                readerFont = it
            },

            onChapterListClick = {
                onChapterListClick()
            },

            onNightModeClick = {
                onNightModeClick()
            },

            onCommentClick = {
                onCommentClick()
            },

            onBookOverviewClick = {
                onBookOverviewClick()
            },

            isBookmarked =
                isBookmarked,

            onBookmarkClick = {
                isBookmarked = !isBookmarked
            },

            onDownloadClick = {
                showDownloadDialog = true
            },

            onReportClick = {
                showReaderReportSheet = true
            },

            onDismiss = {
                showReaderSheet = false
            }
        )

        // ======================================================
        // REPORT
        // ======================================================

        ReaderReportBottomSheet(

            visible =
                showReaderReportSheet,

            onDismiss = {
                showReaderReportSheet = false
            },

            onSubmit = { reportType, detail ->

                println(
                    "Báo cáo: $reportType - $detail"
                )

                showReaderReportSheet = false
            }
        )

        // ======================================================
        // DOWNLOAD
        // ======================================================

        if (showDownloadDialog) {

            DownloadChapterDialog(

                onDismiss = {
                    showDownloadDialog = false
                },

                onConfirm = { fromChapter, toChapter ->

                    println(
                        "Tải truyện từ chương $fromChapter đến chương $toChapter"
                    )

                    showDownloadDialog = false
                }
            )
        }
    }
}


// ==========================================================
// READER TOP BAR
// ==========================================================

@Composable
private fun ReaderTopBar(

    chapterTitle: String,

    progress: String,

    textColor: Color,

    onBack: () -> Unit,

    onSettings: () -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ==================================================
        // BACK
        // ==================================================

        IconButton(

            onClick = onBack,

            modifier =
                Modifier.size(40.dp)
        ) {

            Icon(

                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription =
                    "Quay lại",

                tint =
                    textColor,

                modifier =
                    Modifier.size(25.dp)
            )
        }

        // ==================================================
        // CHAPTER TITLE
        // ==================================================

        Text(

            text =
                chapterTitle,

            color =
                textColor,

            fontSize =
                12.sp,

            fontFamily =
                FontFamily.Serif,

            maxLines =
                1,

            modifier =
                Modifier
                    .weight(1f)
                    .padding(
                        start = 2.dp
                    )
        )

        // ==================================================
        // SETTINGS
        // ==================================================

        IconButton(

            onClick =
                onSettings,

            modifier =
                Modifier.size(40.dp)
        ) {

            Icon(

                imageVector =
                    Icons.Default.Settings,

                contentDescription =
                    "Cài đặt đọc",

                tint =
                    textColor,

                modifier =
                    Modifier.size(25.dp)
            )
        }
    }
}


// ==========================================================
// DOWNLOAD CHAPTER DIALOG
// ==========================================================

@Composable
private fun DownloadChapterDialog(

    onDismiss: () -> Unit,

    onConfirm: (
        String,
        String
    ) -> Unit
) {

    var fromChapter by remember {
        mutableStateOf("")
    }

    var toChapter by remember {
        mutableStateOf("")
    }

    AlertDialog(

        onDismissRequest =
            onDismiss,

        title = {

            Text(
                text = "Bạn muốn tải",
                color = Color.White,
                fontSize = 22.sp
            )
        },

        text = {

            Column(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Từ chương",
                    color =
                        Color(0xFFBDBDBD),
                    fontSize = 15.sp
                )

                OutlinedTextField(

                    value =
                        fromChapter,

                    onValueChange = {
                        fromChapter = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    colors =
                        TextFieldDefaults.colors(

                            focusedContainerColor =
                                Color.Transparent,

                            unfocusedContainerColor =
                                Color.Transparent,

                            focusedIndicatorColor =
                                Color.Transparent,

                            unfocusedIndicatorColor =
                                Color.Transparent,

                            focusedTextColor =
                                Color.White,

                            unfocusedTextColor =
                                Color.White,

                            cursorColor =
                                Color.White
                        )
                )

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Color(0xFF222222)
                            )
                )

                Spacer(
                    modifier =
                        Modifier.height(22.dp)
                )

                Text(
                    text = "Đến chương",
                    color =
                        Color(0xFFBDBDBD),
                    fontSize = 15.sp
                )

                OutlinedTextField(

                    value =
                        toChapter,

                    onValueChange = {
                        toChapter = it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    colors =
                        TextFieldDefaults.colors(

                            focusedContainerColor =
                                Color.Transparent,

                            unfocusedContainerColor =
                                Color.Transparent,

                            focusedIndicatorColor =
                                Color.Transparent,

                            unfocusedIndicatorColor =
                                Color.Transparent,

                            focusedTextColor =
                                Color.White,

                            unfocusedTextColor =
                                Color.White,

                            cursorColor =
                                Color.White
                        )
                )

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Color(0xFF777777)
                            )
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick =
                    onDismiss
            ) {

                Text(
                    text = "Hủy",
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        },

        confirmButton = {

            TextButton(

                onClick = {

                    if (
                        fromChapter.isNotBlank() &&
                        toChapter.isNotBlank()
                    ) {

                        onConfirm(
                            fromChapter,
                            toChapter
                        )
                    }
                }
            ) {

                Text(
                    text = "Đồng ý",
                    color = Color.Black,
                    fontSize = 16.sp
                )
            }
        },

        containerColor =
            Color(0xFF4A4A4A)
    )
}


// ==========================================================
// READER CONTROL BOTTOM SHEET
// ==========================================================

@Composable
private fun ReaderControlSheet(

    visible: Boolean,

    selectedTab: String,

    onTabSelected: (String) -> Unit,

    fontSize: Float,

    onFontSizeChange: (Float) -> Unit,

    backgroundColor: Color,

    onBackgroundColorChange: (Color) -> Unit,

    selectedFont: String,

    onFontChange: (String) -> Unit,

    onChapterListClick: () -> Unit,

    onNightModeClick: () -> Unit,

    onCommentClick: () -> Unit,

    onBookOverviewClick: () -> Unit,

    isBookmarked: Boolean,

    onBookmarkClick: () -> Unit,

    onDownloadClick: () -> Unit,

    onReportClick: () -> Unit,

    onDismiss: () -> Unit
) {

    if (!visible) return

    val sheetHeight = 0.65f

    val sheetCornerRadius = 20.dp

    val dragBarWidth = 40.dp

    val dragBarHeight = 4.dp

    val tabTopPadding = 12.dp

    val tabHorizontalPadding = 24.dp

    val infoBoxTopPadding = 18.dp

    val infoBoxHorizontalPadding = 20.dp

    val menuItemVerticalPadding = 8.dp

    val menuItemStartPadding = 24.dp

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        // ==================================================
        // OVERLAY
        // ==================================================

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(
                            alpha = 0.55f
                        )
                    )
                    .clickable {
                        onDismiss()
                    }
        )

        // ==================================================
        // SHEET
        // ==================================================

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(
                        sheetHeight
                    )
                    .align(
                        Alignment.BottomCenter
                    )
                    .clip(
                        RoundedCornerShape(
                            topStart =
                                sheetCornerRadius,

                            topEnd =
                                sheetCornerRadius
                        )
                    )
                    .background(
                        Color(0xFF303030)
                    )
                    .navigationBarsPadding()
        ) {

            // ==================================================
            // DRAG BAR
            // ==================================================

            Box(

                modifier =
                    Modifier
                        .padding(
                            top = 8.dp
                        )
                        .width(
                            dragBarWidth
                        )
                        .height(
                            dragBarHeight
                        )
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .clip(
                            RoundedCornerShape(
                                10.dp
                            )
                        )
                        .background(
                            Color(0xFFAAAAAA)
                        )
            )

            // ==================================================
            // TABS
            // ==================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start =
                                tabHorizontalPadding,

                            end = 24.dp,

                            top =
                                tabTopPadding,

                            bottom = 4.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(

                    horizontalAlignment =
                        Alignment.Start,

                    modifier =
                        Modifier.clickable {
                            onTabSelected(
                                "Thông tin"
                            )
                        }
                ) {

                    Text(

                        text =
                            "Thông tin",

                        color =
                            Color.White,

                        fontSize =
                            15.sp,

                        fontWeight =
                            if (
                                selectedTab ==
                                "Thông tin"
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                    )

                    if (
                        selectedTab ==
                        "Thông tin"
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    7.dp
                                )
                        )

                        Box(

                            modifier =
                                Modifier
                                    .width(
                                        76.dp
                                    )
                                    .height(
                                        3.dp
                                    )
                                    .background(
                                        Color.White
                                    )
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.width(
                            30.dp
                        )
                )

                Column(

                    horizontalAlignment =
                        Alignment.Start,

                    modifier =
                        Modifier.clickable {
                            onTabSelected(
                                "Cài đặt"
                            )
                        }
                ) {

                    Text(

                        text =
                            "Cài đặt",

                        color =
                            Color.White,

                        fontSize =
                            15.sp,

                        fontWeight =
                            if (
                                selectedTab ==
                                "Cài đặt"
                            ) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                    )

                    if (
                        selectedTab ==
                        "Cài đặt"
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    7.dp
                                )
                        )

                        Box(

                            modifier =
                                Modifier
                                    .width(
                                        87.dp
                                    )
                                    .height(
                                        4.dp
                                    )
                                    .background(
                                        Color.White
                                    )
                        )
                    }
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                Icon(

                    imageVector =
                        Icons.Default.KeyboardArrowDown,

                    contentDescription =
                        "Đóng",

                    tint =
                        Color.LightGray,

                    modifier =
                        Modifier
                            .size(38.dp)
                            .clickable {
                                onDismiss()
                            }
                )
            }

            // ==================================================
            // THÔNG TIN
            // ==================================================

            if (
                selectedTab ==
                "Thông tin"
            ) {

                LazyColumn(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),

                    contentPadding =
                        PaddingValues(
                            bottom = 12.dp
                        )
                ) {

                    item {

                        Column(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start =
                                            infoBoxHorizontalPadding,

                                        end =
                                            infoBoxHorizontalPadding,

                                        top =
                                            infoBoxTopPadding
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                                    .background(
                                        Color(0xFF666666)
                                    )
                                    .padding(
                                        horizontal =
                                            18.dp,

                                        vertical =
                                            14.dp
                                    )
                        ) {

                            Text(

                                text =
                                    "1 / 1224",

                                color =
                                    Color.White,

                                fontSize =
                                    14.sp,

                                modifier =
                                    Modifier.align(
                                        Alignment.CenterHorizontally
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        3.dp
                                    )
                            )

                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "‹",
                                    color =
                                        Color.White,
                                    fontSize =
                                        30.sp
                                )

                                Box(

                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .background(
                                                Color.White
                                            )
                                )

                                Text(
                                    text = "›",
                                    color =
                                        Color.White,
                                    fontSize =
                                        30.sp
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )

                            Row(

                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.SpaceEvenly
                            ) {

                                ReaderAction(
                                    icon =
                                        Icons.Default.FormatListBulleted,

                                    text =
                                        "D.S Chương",

                                    onClick =
                                        onChapterListClick
                                )

                                ReaderAction(
                                    icon =
                                        Icons.Default.Settings,

                                    text =
                                        "Bật",

                                    onClick =
                                        onNightModeClick
                                )

                                ReaderAction(

                                    icon =
                                        if (
                                            isBookmarked
                                        ) {
                                            Icons.Default.Bookmark
                                        } else {
                                            Icons.Default.BookmarkBorder
                                        },

                                    text =
                                        if (
                                            isBookmarked
                                        ) {
                                            "Đã đánh dấu"
                                        } else {
                                            "Đánh dấu"
                                        },

                                    onClick =
                                        onBookmarkClick
                                )
                            }
                        }
                    }

                    item {

                        ReaderMenuItem(
                            icon =
                                Icons.Default.Refresh,

                            text =
                                "Tải lại nội dung",

                            startPadding =
                                menuItemStartPadding,

                            verticalPadding =
                                menuItemVerticalPadding
                        )
                    }

                    item {

                        ReaderMenuItem(
                            icon =
                                Icons.Default.ChatBubbleOutline,

                            text =
                                "Bình luận (29)",

                            startPadding =
                                menuItemStartPadding,

                            verticalPadding =
                                menuItemVerticalPadding,

                            onClick =
                                onCommentClick
                        )
                    }

                    item {

                        ReaderMenuItem(
                            icon =
                                Icons.Default.MenuBook,

                            text =
                                "Thông tin truyện",

                            startPadding =
                                menuItemStartPadding,

                            verticalPadding =
                                menuItemVerticalPadding,

                            onClick =
                                onBookOverviewClick
                        )
                    }

                    item {

                        ReaderMenuItem(
                            icon =
                                Icons.Default.CloudDownload,

                            text =
                                "Tải truyện",

                            startPadding =
                                menuItemStartPadding,

                            verticalPadding =
                                menuItemVerticalPadding,

                            onClick =
                                onDownloadClick
                        )
                    }

                    item {

                        ReaderMenuItem(
                            icon =
                                Icons.Default.Flag,

                            text =
                                "Báo lỗi",

                            startPadding =
                                menuItemStartPadding,

                            verticalPadding =
                                menuItemVerticalPadding,

                            onClick =
                                onReportClick
                        )
                    }
                }

            } else {

                // ==================================================
                // SETTINGS
                // ==================================================

                LazyColumn(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),

                    contentPadding =
                        PaddingValues(
                            bottom = 24.dp
                        )
                ) {

                    item {

                        ReaderSettingsContent(

                            fontSize =
                                fontSize,

                            onFontSizeChange =
                                onFontSizeChange,

                            backgroundColor =
                                backgroundColor,

                            onBackgroundColorChange =
                                onBackgroundColorChange,

                            selectedFont =
                                selectedFont,

                            onFontChange =
                                onFontChange
                        )
                    }
                }
            }
        }
    }
}


// ==========================================================
// READER SETTINGS
// ==========================================================

@Composable
private fun ReaderSettingsContent(

    fontSize: Float,

    onFontSizeChange:
        (Float) -> Unit,

    backgroundColor: Color,

    onBackgroundColorChange:
        (Color) -> Unit,

    selectedFont: String,

    onFontChange:
        (String) -> Unit
) {

    val sectionTopSpacing =
        26.dp

    val itemSpacing =
        4.dp

    val fontOptions =
        listOf(
            "Times New Roman",
            "Avenir Next",
            "Arial",
            "Helvetica",
            "Palatino",
            "Verdana",
            "Noto"
        )

    val backgroundOptions =
        listOf(

            Color(0xFFFFFFFF),

            Color(0xFFF2D6C9),

            Color(0xFF8B6F47),

            Color(0xFFB5B5B5),

            Color(0xFF666666),

            Color(0xFF1E1E1E)
        )

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 26.dp,
                    bottom = 20.dp
                )
    ) {

        // ==================================================
        // FONT SIZE
        // ==================================================

        Text(

            text = "Cỡ chữ",

            color = Color.White,

            fontSize = 20.sp,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.padding(
                    bottom = 14.dp
                )
        )

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(
                        RoundedCornerShape(
                            18.dp
                        )
                    )
                    .background(
                        Color(0xFF666666)
                    )
                    .padding(
                        horizontal = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "A",
                color = Color.White,
                fontSize = 18.sp,
                modifier =
                    Modifier.width(32.dp)
            )

            Slider(

                value =
                    fontSize,

                onValueChange =
                    onFontSizeChange,

                valueRange =
                    16f..36f,

                modifier =
                    Modifier.weight(1f)
            )

            Text(
                text = "A",
                color = Color.White,
                fontSize = 34.sp,
                modifier =
                    Modifier.width(40.dp)
            )
        }

        // ==================================================
        // BACKGROUND
        // ==================================================

        Text(

            text = "Màu",

            color = Color.White,

            fontSize = 20.sp,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.padding(
                    top =
                        sectionTopSpacing,

                    bottom = 14.dp
                )
        )

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            backgroundOptions.forEach { color ->

                ReaderBackgroundOption(

                    color =
                        color,

                    selected =
                        color == backgroundColor,

                    onClick = {
                        onBackgroundColorChange(
                            color
                        )
                    }
                )
            }
        }

        // ==================================================
        // FONT
        // ==================================================

        Text(

            text = "Font chữ",

            color = Color.White,

            fontSize = 20.sp,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier.padding(
                    top =
                        sectionTopSpacing,

                    bottom = 8.dp
                )
        )

        fontOptions.forEach { font ->

            ReaderFontOption(

                fontName =
                    font,

                selected =
                    font == selectedFont,

                onClick = {
                    onFontChange(font)
                },

                verticalPadding =
                    itemSpacing
            )
        }
    }
}


// ==========================================================
// READER ACTION
// ==========================================================

@Composable
private fun ReaderAction(

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    text: String,

    onClick: () -> Unit
) {

    Column(

        horizontalAlignment =
            Alignment.CenterHorizontally,

        modifier =
            Modifier
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 8.dp
                )
    ) {

        Icon(

            imageVector =
                icon,

            contentDescription =
                text,

            tint =
                Color.White,

            modifier =
                Modifier.size(28.dp)
        )

        Spacer(
            modifier =
                Modifier.height(2.dp)
        )

        Text(

            text = text,

            color =
                Color.White,

            fontSize =
                13.sp
        )
    }
}


// ==========================================================
// READER MENU ITEM
// ==========================================================

@Composable
private fun ReaderMenuItem(

    icon:
    androidx.compose.ui.graphics.vector.ImageVector,

    text: String,

    startPadding: Dp,

    verticalPadding: Dp,

    onClick: () -> Unit = {}
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(

                    start =
                        startPadding,

                    end = 20.dp,

                    top =
                        verticalPadding,

                    bottom =
                        verticalPadding
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(

            imageVector =
                icon,

            contentDescription =
                text,

            tint =
                Color(0xFFBDBDBD),

            modifier =
                Modifier.size(24.dp)
        )

        Spacer(
            modifier =
                Modifier.width(28.dp)
        )

        Text(

            text = text,

            color =
                Color.White,

            fontSize =
                14.sp
        )
    }
}


// ==========================================================
// BACKGROUND COLOR OPTION
// ==========================================================

@Composable
private fun ReaderBackgroundOption(

    color: Color,

    selected: Boolean,

    onClick: () -> Unit
) {

    Box(

        modifier =
            Modifier
                .size(38.dp)
                .clip(
                    CircleShape
                )
                .background(
                    color
                )
                .border(

                    width = 2.dp,

                    color =
                        if (selected) {
                            Color(0xFF168BD0)
                        } else {
                            Color(0xFFBDBDBD)
                        },

                    shape =
                        CircleShape
                )
                .clickable {
                    onClick()
                },

        contentAlignment =
            Alignment.Center
    ) {

        if (selected) {

            Icon(

                imageVector =
                    Icons.Default.Check,

                contentDescription =
                    "Đang chọn",

                tint =
                    if (
                        color ==
                        Color(0xFF666666) ||
                        color ==
                        Color(0xFF1E1E1E)
                    ) {
                        Color.White
                    } else {
                        Color(0xFF555555)
                    },

                modifier =
                    Modifier.size(22.dp)
            )
        }
    }
}


// ==========================================================
// FONT OPTION
// ==========================================================

@Composable
private fun ReaderFontOption(

    fontName: String,

    selected: Boolean,

    onClick: () -> Unit,

    verticalPadding: Dp
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(
                    vertical =
                        verticalPadding + 6.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text =
                fontName,

            color =
                if (selected) {
                    Color.White
                } else {
                    Color(0xFFD0D0D0)
                },

            fontSize =
                18.sp,

            fontWeight =
                if (selected) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                }
        )

        Spacer(
            modifier =
                Modifier.weight(1f)
        )

        if (selected) {

            Text(

                text = "✓",

                color =
                    Color(0xFF1682C4),

                fontSize =
                    22.sp
            )
        }
    }
}


// ==========================================================
// READER FONT
// ==========================================================

private fun getReaderFont(
    fontName: String
): FontFamily {

    return when (fontName) {

        "Times New Roman" ->
            FontFamily.SansSerif

        "Avenir Next" ->
            FontFamily.SansSerif

        "Arial" ->
            FontFamily.SansSerif

        "Helvetica" ->
            FontFamily.SansSerif

        "Palatino" ->
            FontFamily.Serif

        "Verdana" ->
            FontFamily.SansSerif

        "Noto" ->
            FontFamily.SansSerif

        else ->
            FontFamily.Serif
    }
}