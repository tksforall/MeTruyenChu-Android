package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val repository = LibraryRepository()
    private val _books = MutableStateFlow<List<BookResponse>>(emptyList())
    val books: StateFlow<List<BookResponse>> = _books.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadLibrary() = viewModelScope.launch {
        _isLoading.value = true
        _error.value = null
        try {
            val ids = repository.getMyLibrary().getOrElse { throw it }.map { it.bookId }.toSet()
            val books = repository.getAllBooks().getOrElse { throw it }
            _books.value = books.filter { it.bookId in ids }
        } catch (e: Exception) {
            _error.value = e.message ?: "Không thể tải tủ truyện"
        } finally { _isLoading.value = false }
    }
}
