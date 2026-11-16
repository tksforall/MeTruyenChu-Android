package com.example.metruyenchu.user_interface.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LibrarySortViewModel : ViewModel() {
    private val _historySort = MutableStateFlow("Mới đọc")
    val historySort: StateFlow<String> = _historySort.asStateFlow()

    private val _bookmarkSort = MutableStateFlow("Mới lưu")
    val bookmarkSort: StateFlow<String> = _bookmarkSort.asStateFlow()

    fun updateHistorySort(sort: String) {
        _historySort.value = sort
        // Bạn có thể lưu thêm vào SharedPreferences hoặc DataStore ở đây nếu muốn giữ lại sau khi tắt hẳn app
    }

    fun updateBookmarkSort(sort: String) {
        _bookmarkSort.value = sort
        // Lưu SharedPreferences / DataStore nếu cần
    }
}