package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.repository.BookRepository
import com.example.metruyenchu.user_interface.explore.components.BookFilter
import com.example.metruyenchu.user_interface.explore.components.toRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListOfStoryViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    // ==================================================
    // DANH SÁCH TRUYỆN
    // ==================================================

    private val _books =
        MutableStateFlow<List<BookResponse>>(emptyList())

    val books: StateFlow<List<BookResponse>> =
        _books.asStateFlow()


    // ==================================================
    // LOADING
    // ==================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ==================================================
    // ERROR
    // ==================================================

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()


    // ==================================================
    // FILTER
    // ==================================================

    fun loadBooks(filter: BookFilter) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            val result = repository.filterBooks(
                filter.toRequest()
            )

            result
                .onSuccess { books ->

                    _books.value = books
                }
                .onFailure { exception ->

                    _error.value =
                        exception.message
                            ?: "Không thể tải danh sách truyện"
                }

            _isLoading.value = false
        }
    }
}