package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookOverviewViewModel : ViewModel() {
    private val repository = LibraryRepository()
    private val _book = MutableStateFlow<BookResponse?>(null)
    val book: StateFlow<BookResponse?> = _book.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _isInLibrary = MutableStateFlow(false)
    val isInLibrary: StateFlow<Boolean> = _isInLibrary.asStateFlow()

    fun loadBook(bookId: Long) = viewModelScope.launch {
        _isLoading.value = true; _error.value = null
        try { _book.value = repository.getBookById(bookId).getOrElse { throw it } }
        catch (e: Exception) { _error.value = e.message ?: "Không thể tải thông tin truyện" }
        finally { _isLoading.value = false }
    }

    fun loadLibraryState(bookId: Long) = viewModelScope.launch {
        try { _isInLibrary.value = repository.getMyLibrary().getOrElse { throw it }.any { it.bookId == bookId } }
        catch (_: Exception) { /* guest/offline: keep current state */ }
    }

    fun toggleLibrary(userId: Long, bookId: Long) = viewModelScope.launch {
        if (userId <= 0) return@launch
        try {
            if (_isInLibrary.value) repository.removeBookFromLibrary(userId, bookId).getOrElse { throw it }
            else repository.addBookToLibrary(userId, bookId).getOrElse { throw it }
            _isInLibrary.value = !_isInLibrary.value
        } catch (e: Exception) { _error.value = e.message ?: "Không thể cập nhật tủ truyện" }
    }
}
