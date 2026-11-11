package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.ReadingHistoryResponse
import com.example.metruyenchu.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val repository = LibraryRepository()
    private val _books = MutableStateFlow<List<BookResponse>>(emptyList())
    val books: StateFlow<List<BookResponse>> = _books.asStateFlow()
    private val _history = MutableStateFlow<List<ReadingHistoryResponse>>(emptyList())
    val history: StateFlow<List<ReadingHistoryResponse>> = _history.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadHistory() = viewModelScope.launch {
        _isLoading.value = true; _error.value = null
        try {
            val history = repository.getMyReadingHistory().getOrElse { throw it }
                .sortedByDescending { it.lastReadAt }
            val allBooks = repository.getAllBooks().getOrElse { throw it }
            _history.value = history
            _books.value = history.mapNotNull { h -> allBooks.firstOrNull { it.bookId == h.bookId } }
        } catch (e: Exception) { _error.value = e.message ?: "Không thể tải lịch sử" }
        finally { _isLoading.value = false }
    }
}
