package com.example.metruyenchu.user_interface.explore.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit = {},
    onApply: (BookFilter) -> Unit,
    onNavigateToListOfStory: (() -> Unit)? = null,
    initialFilter: BookFilter? = null
) {

    // =========================================================
    // DỮ LIỆU CÁC LỰA CHỌN
    // =========================================================

    // -------------------------
    // Sắp xếp
    // Chỉ chọn 1
    // -------------------------

    val sortOptions = listOf(
        "Mới lên chương",
        "Mới đăng",
        "Lượt đọc",
        "Lượt đọc tuần",
        "Lượt đề cử",
        "Lượt đề cử tuần",
        "Lượt bình luận",
        "Lượt bình luận tuần",
        "Lượt đánh dấu",
        "Lượt đánh giá",
        "Điểm đánh giá",
        "Số chương",
        "Tên truyện"
    )


    // -------------------------
    // Tình trạng
    // Chỉ chọn 1
    // -------------------------

    val statusOptions = listOf(
        "Còn tiếp",
        "Hoàn thành",
        "Tạm dừng"
    )


    // -------------------------
    // Số chương
    // Chỉ chọn 1
    // -------------------------

    val chapterOptions = listOf(
        "< 300",
        "300–600",
        "600–1000",
        "> 1000"
    )


    // -------------------------
    // Ngày xuất bản
    // Chỉ chọn 1
    // -------------------------

    val publishOptions = listOf(
        "Trong 1 tuần",
        "Trong 1 tháng",
        "Trong 3 tháng",
        "Trong 1 năm"
    )


    // -------------------------
    // Thể loại
    // Có thể chọn nhiều
    // -------------------------

    val genreOptions = listOf(
        "Tiên Hiệp",
        "Huyền Huyễn",
        "Khoa Huyễn",
        "Võng Du",
        "Đô Thị",
        "Đồng Nhân",
        "Dã Sử",
        "Cạnh Kỹ",
        "Huyền Nghi",
        "Kiếm Hiệp",
        "Kỳ Ảo",
        "Light Novel",
        "Hiện Đại Ngôn Tình",
        "Huyền Huyễn Ngôn Tình",
        "Tiên Hiệp Kỳ Duyên",
        "Cổ Đại Ngôn Tình",
        "Huyền Nghi Thần Quái",
        "Khoa Huyễn Không Gian",
        "Lãng Mạn Thanh Xuân"
    )


    // -------------------------
    // Tính cách
    // Có thể chọn nhiều
    // -------------------------

    val personalityOptions = listOf(
        "Điềm Đạm",
        "Nhiệt Huyết",
        "Vô Sĩ",
        "Thiết Huyết",
        "Nhẹ Nhàng",
        "Cơ Trí",
        "Lạnh Khốc",
        "Kiêu Ngạo",
        "Ngu Ngốc",
        "Giảo Hoạt"
    )


    // -------------------------
    // Bối cảnh
    // Có thể chọn nhiều
    // -------------------------

    val worldOptions = listOf(
        "Đông Phương Huyền Huyễn",
        "Dị Thế Đại Lục",
        "Cao Võ Thế Giới",
        "Vương Triều Tranh Bá",
        "Tây Phương Kỳ Huyễn",
        "Hiện Đại Ma Pháp",
        "Hắc Ám Huyển Tưởng",
        "Lịch Sử Thần Thoại",
        "Võ Hiệp Huyễn Tưởng",
        "Cổ Võ Tương Lai",
        "Tu Chân Văn Minh",
        "Huyễn Tưởng Tu Tiên",
        "Hiện Đại Tu Chân",
        "Thần Thoại Tu Chân",
        "Cổ Điển Tiên Hiệp",
        "Viễn Cổ Hồng Hoang",
        "Đô Thị Sinh Hoạt",
        "Đô Thị Dị Năng",
        "Thanh Xuân Vườn Trường",
        "Ngu Nhạc Minh Tinh",
        "Dân Gian Truyền Thuyết",
        "Lịch Sử Quan Trường",
        "Du Hí Dị Giới",
        "Cổ Võ Cơ Giáp",
        "Thế Giới Tương Lai",
        "Tinh Tế Văn Minh",
        "Tiến Hóa Biến Dị",
        "Mạt Thế Nguy Cơ",
        "Thời Không Xuyên Toa",
        "Quỷ Bí Huyền Nghi",
        "Thám Hiểm Sinh Tồn",
        "Cung Vi Trạch Đấu",
        "Kinh Thương Chủng Điền",
        "Tiên Lữ Kỳ Duyên",
        "Hào Môn Thế Gia",
        "Dị Tộc Luyến Tình",
        "Linh Khí Khôi Phục",
        "Chư Thiên Vạn Giới",
        "Nguyên Sinh Huyễn Tưởng",
        "Yêu Đương Hằng Ngày",
        "Diễn Sinh Đồng Nhân",
        "Cáo Tiểu Thổ Hào"
    )


    // -------------------------
    // Lưu phái
    // Có thể chọn nhiều
    // -------------------------

    val tagOptions = listOf(
        "Hệ Thống",
        "Lão Gia",
        "Phàm Nhân",
        "Tùy Thân",
        "Vô Địch",
        "Xuyên Qua",
        "Nữ Cường",
        "Khế Ước",
        "Trọng Sinh",
        "Hồng Lâu",
        "Học Viện",
        "Biến Thân",
        "Chuyển Thế",
        "Xuyên Sách",
        "Phế Tài",
        "Dưỡng Thành",
        "Cơm Mềm",
        "Vô Hạn",
        "Cá Mặn",
        "Xây Dựng Thế Lực",
        "Xuyên Nhanh",
        "Nữ Phụ",
        "Vả Mặt",
        "Sảng Văn",
        "Xuyên Không",
        "Ngọt Sủng",
        "Ngự Thú",
        "Điền Viên",
        "Toàn Dân",
        "Mỹ Thực",
        "Phản Phái",
        "Sau Màn",
        "Thiên Tài"
    )


    // =========================================================
    // TRẠNG THÁI LỰA CHỌN
    // =========================================================

    var selectedSort by remember {
        mutableStateOf(
            initialFilter?.sort ?: "Mới lên chương"
        )
    }

    var selectedStatus by remember {
        mutableStateOf(
            initialFilter?.status ?: ""
        )
    }

    var selectedChapter by remember {
        mutableStateOf(
            initialFilter?.chapterRange ?: ""
        )
    }

    var selectedPublish by remember {
        mutableStateOf(
            initialFilter?.publishDate ?: ""
        )
    }

    var selectedGenres by remember {
        mutableStateOf(
            initialFilter?.genres ?: emptySet()
        )
    }

    var selectedPersonalities by remember {
        mutableStateOf(
            initialFilter?.personalities ?: emptySet()
        )
    }

    var selectedWorlds by remember {
        mutableStateOf(
            initialFilter?.worlds ?: emptySet()
        )
    }

    var selectedTags by remember {
        mutableStateOf(
            initialFilter?.tags ?: emptySet()
        )
    }


    // =========================================================
    // BOTTOM SHEET
    // =========================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // =====================================================
        // NỀN TỐI PHÍA SAU
        // =====================================================

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.35f)
                )
                .clickable {
                    // Bấm ra ngoài sheet -> đóng
                    onDismiss()
                }
        )


        // =====================================================
        // PHẦN BOTTOM SHEET
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color(0xFF292929),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp
                    )
                )
        ) {

            // =================================================
            // THANH NHỎ TRÊN ĐẦU
            // =================================================

            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(26.dp)
                    .height(4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(Color.LightGray)
            )


            // =================================================
            // DANH SÁCH FILTER
            // =================================================

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 10.dp,
                    bottom = 16.dp
                ),

                verticalArrangement = Arrangement.spacedBy(
                    10.dp
                )
            ) {

                // =================================================
                // SẮP XẾP
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Sắp xếp"
                    )

                    FilterOptionSingle(
                        options = sortOptions,
                        selectedOption = selectedSort,
                        onSelected = {
                            selectedSort = it
                        }
                    )
                }


                // =================================================
                // TÌNH TRẠNG
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Tình trạng"
                    )

                    FilterOptionSingle(
                        options = statusOptions,
                        selectedOption = selectedStatus,
                        onSelected = {
                            selectedStatus = it
                        }
                    )
                }


                // =================================================
                // SỐ CHƯƠNG
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Số chương"
                    )

                    FilterOptionSingle(
                        options = chapterOptions,
                        selectedOption = selectedChapter,
                        onSelected = {
                            selectedChapter = it
                        }
                    )
                }


                // =================================================
                // NGÀY XUẤT BẢN
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Ngày xuất bản"
                    )

                    FilterOptionSingle(
                        options = publishOptions,
                        selectedOption = selectedPublish,
                        onSelected = {
                            selectedPublish = it
                        }
                    )
                }


                // =================================================
                // THỂ LOẠI
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Thể loại"
                    )

                    FilterOptionGrid(
                        options = genreOptions,
                        selectedOptions = selectedGenres,
                        onSelected = { option ->

                            selectedGenres =
                                if (option in selectedGenres) {
                                    // Bấm lần 2 -> bỏ chọn
                                    selectedGenres - option
                                } else {
                                    // Bấm lần 1 -> thêm vào
                                    selectedGenres + option
                                }
                        }
                    )
                }


                // =================================================
                // TÍNH CÁCH
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Tính cách nhân vật chính"
                    )

                    FilterOptionGrid(
                        options = personalityOptions,
                        selectedOptions = selectedPersonalities,
                        onSelected = { option ->

                            selectedPersonalities =
                                if (option in selectedPersonalities) {
                                    selectedPersonalities - option
                                } else {
                                    selectedPersonalities + option
                                }
                        }
                    )
                }


                // =================================================
                // BỐI CẢNH
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Bối cảnh thế giới"
                    )

                    FilterOptionGrid(
                        options = worldOptions,
                        selectedOptions = selectedWorlds,
                        onSelected = { option ->

                            selectedWorlds =
                                if (option in selectedWorlds) {
                                    selectedWorlds - option
                                } else {
                                    selectedWorlds + option
                                }
                        }
                    )
                }


                // =================================================
                // LƯU PHÁI
                // =================================================

                item {

                    FilterSectionTitle(
                        title = "Lưu phái"
                    )

                    FilterOptionGrid(
                        options = tagOptions,
                        selectedOptions = selectedTags,
                        onSelected = { option ->

                            selectedTags =
                                if (option in selectedTags) {
                                    selectedTags - option
                                } else {
                                    selectedTags + option
                                }
                        }
                    )
                }
            }


            // =================================================
            // NÚT GỬI
            // =================================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 12.dp
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(
                            RoundedCornerShape(22.dp)
                        )
                        .background(Color.Black)
                        .clickable {

                            // =================================
                            // TẠO OBJECT BOOK FILTER
                            // =================================

                            val filter = BookFilter(

                                // Nhóm chọn 1
                                sort = selectedSort,
                                status = selectedStatus,
                                chapterRange = selectedChapter,
                                publishDate = selectedPublish,

                                // Nhóm chọn nhiều
                                genres = selectedGenres,
                                personalities = selectedPersonalities,
                                worlds = selectedWorlds,
                                tags = selectedTags
                            )

                            // Trả filter về Screen đang gọi
                            onApply(filter)

                            onDismiss()

                            onNavigateToListOfStory?.invoke()
                        },

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Gửi",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}


// ==========================================================
// TIÊU ĐỀ NHÓM FILTER
// ==========================================================

@Composable
private fun FilterSectionTitle(
    title: String
) {

    Text(
        text = title,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(
            bottom = 3.dp
        )
    )
}


// ==========================================================
// FILTER CHỌN NHIỀU
// ==========================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterOptionGrid(
    options: List<String>,
    selectedOptions: Set<String>,
    onSelected: (String) -> Unit
) {

    FlowRow(
        modifier = Modifier.fillMaxWidth(),

        // Tối đa 4 option trên một hàng
        maxItemsInEachRow = 4,

        // Khoảng cách ngang
        horizontalArrangement = Arrangement.spacedBy(6.dp),

        // Khoảng cách dọc
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {

        options.forEach { option ->

            FilterOption(
                text = option,

                // Option có nằm trong danh sách đã chọn?
                selected = option in selectedOptions,

                onClick = {
                    onSelected(option)
                }
            )
        }
    }
}


// ==========================================================
// FILTER CHỌN MỘT
// ==========================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterOptionSingle(
    options: List<String>,
    selectedOption: String,
    onSelected: (String) -> Unit
) {

    FlowRow(
        modifier = Modifier.fillMaxWidth(),

        // Tối đa 4 lựa chọn trong 1 hàng
        maxItemsInEachRow = 3,

        // Khoảng cách giống lúc trước
        horizontalArrangement = Arrangement.spacedBy(20.dp),

        // Khoảng cách giữa các hàng
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {

        options.forEach { option ->

            FilterOption(
                text = option,

                selected = option == selectedOption,

                onClick = {

                    // Bấm lại lựa chọn đang chọn
                    // -> bỏ chọn
                    if (option == selectedOption) {
                        onSelected("")
                    } else {
                        onSelected(option)
                    }
                }
            )
        }
    }
}

// ==========================================================
// MỘT OPTION
// ==========================================================

@Composable
private fun FilterOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Text(
        text = text,

        // Được chọn -> xanh
        // Chưa chọn -> xám
        color = if (selected) {
            Color(0xFF1682C4)
        } else {
            Color(0xFF777777)
        },

        // Cỡ chữ
        fontSize = 14.sp,

        // Được chọn -> gạch chân
        textDecoration = if (selected) {
            TextDecoration.Underline
        } else {
            TextDecoration.None
        },

        modifier = Modifier
            .clickable {
                onClick()
            }
    )
}