package com.example.metruyenchu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.user_interface.explore.components.BookListItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LatestBooksViewModel : ViewModel() {

    private val _books =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val books: StateFlow<List<BookListItemData>>
        get() = _books

    fun loadBooks() {

        viewModelScope.launch {

            try {

                println("========== LATEST BOOKS SCREEN ==========")
                println("CALLING /api/books/latest")

                val response =
                    RetrofitClient.api.getLatestBooks()

                println(
                    "LATEST HTTP CODE = ${response.code()}"
                )

                if (response.isSuccessful) {

                    val body =
                        response.body().orEmpty()

                    println(
                        "LATEST BOOK COUNT = ${body.size}"
                    )

                    _books.value =
                        body.map {
                            it.toBookListItemData()
                        }

                } else {

                    println(
                        "LATEST FAILED = ${response.code()}"
                    )

                    println(
                        "ERROR = ${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "LATEST EXCEPTION = ${e.message}"
                )

                e.printStackTrace()
            }
        }
    }
}