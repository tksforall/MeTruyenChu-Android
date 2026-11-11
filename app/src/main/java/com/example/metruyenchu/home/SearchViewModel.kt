package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.user_interface.explore.components.BookListItemData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _books =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val books: StateFlow<List<BookListItemData>> = _books

    private var searchJob: Job? = null


    fun search(keyword: String) {

        searchJob?.cancel()

        if (keyword.isBlank()) {
            _books.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {

            // Chờ một chút để không gọi API
            // sau từng ký tự người dùng nhập
            delay(300)

            try {

                val response =
                    RetrofitClient.api.searchBooks(
                        keyword.trim()
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body().orEmpty()

                    _books.value =
                        body.map {
                            it.toBookListItemData()
                        }

                } else {

                    _books.value = emptyList()

                    println(
                        "SEARCH FAILED = ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                _books.value = emptyList()

                println(
                    "SEARCH EXCEPTION = ${e.message}"
                )

                e.printStackTrace()
            }
        }
    }


    fun clearSearch() {

        searchJob?.cancel()

        _books.value = emptyList()
    }
}