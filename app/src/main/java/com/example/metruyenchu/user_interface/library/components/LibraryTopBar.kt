package com.example.metruyenchu.user_interface.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ==========================================================
// TOP BAR DÙNG CHUNG CHO HISTORY + BOOKMARK
// ==========================================================

@Composable
fun LibraryTopBar(
    searchMode: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCloseSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    // ======================================================
    // FOCUS REQUESTER
    // ======================================================

    // Phải remember để FocusRequester không bị tạo lại
    // mỗi lần Compose recompose.
    val focusRequester = remember {
        FocusRequester()
    }


    // ======================================================
    // TỰ ĐỘNG FOCUS KHI BẬT TÌM KIẾM
    // ======================================================

    LaunchedEffect(searchMode) {

        if (searchMode) {
            focusRequester.requestFocus()
        }
    }


    // ======================================================
    // TOP BAR
    // ======================================================

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF282828)
            )
            .statusBarsPadding()
            .height(64.dp)
    ) {


        // ==================================================
        // CHẾ ĐỘ BÌNH THƯỜNG
        // ==================================================

        if (!searchMode) {

            // ------------------------------------------------
            // TÊN "TỦ TRUYỆN"
            // ------------------------------------------------

            Text(
                text = "Tủ Truyện",

                color = Color.White,

                fontSize = 23.sp,

                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(
                        start = 20.dp
                    )
            )


            // ------------------------------------------------
            // KÍNH LÚP
            // ------------------------------------------------

            IconButton(
                onClick = onSearchClick,

                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        end = 52.dp
                    )
            ) {

                Icon(
                    imageVector = Icons.Default.Search,

                    contentDescription = "Tìm kiếm",

                    tint = Color.White,

                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }


        // ==================================================
        // CHẾ ĐỘ TÌM KIẾM
        // ==================================================

        if (searchMode) {

            // ------------------------------------------------
            // Ô NHẬP "TÌM"
            // ------------------------------------------------

            BasicTextField(
                value = searchText,

                onValueChange = {
                    onSearchTextChange(it)
                },

                singleLine = true,

                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 120.dp
                    )
                    .focusRequester(
                        focusRequester
                    ),

                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp
                ),

                decorationBox = { innerTextField ->

                    Box(
                        modifier = Modifier.fillMaxWidth(),

                        contentAlignment =
                            Alignment.CenterStart
                    ) {

                        // ------------------------------------
                        // PLACEHOLDER
                        // ------------------------------------

                        if (searchText.isEmpty()) {

                            Text(
                                text = "Tìm",

                                color = Color.White,

                                fontSize = 20.sp
                            )
                        }


                        // ------------------------------------
                        // TEXTFIELD THẬT
                        // ------------------------------------

                        // Cursor "|" nhấp nháy thật nằm ở đây.
                        innerTextField()
                    }
                }
            )


            // ------------------------------------------------
            // NÚT X
            // ------------------------------------------------

            IconButton(
                onClick = onCloseSearchClick,

                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(
                        end = 52.dp
                    )
            ) {

                Icon(
                    imageVector = Icons.Default.Close,

                    contentDescription =
                        "Đóng tìm kiếm",

                    tint = Color(0xFF777777),

                    modifier = Modifier
                        .size(25.dp)
                )
            }
        }


        // ==================================================
        // NÚT CÀI ĐẶT
        // LUÔN HIỂN THỊ
        // ==================================================

        IconButton(
            onClick = onSettingsClick,

            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    end = 8.dp
                )
        ) {

            Icon(
                imageVector = Icons.Default.Settings,

                contentDescription =
                    "Sắp xếp tủ truyện",

                tint = Color.White,

                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}