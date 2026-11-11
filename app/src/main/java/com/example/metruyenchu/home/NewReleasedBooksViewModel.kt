package com.example.metruyenchu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.user_interface.explore.components.BookListItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewReleasedBooksViewModel : ViewModel() {

    private val _books =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val books: StateFlow<List<BookListItemData>> = _books


    fun loadBooks() {

        viewModelScope.launch {

            try {

                println("========== NEW RELEASED BOOKS ==========")
                println("CALLING /api/books/new-released/all")

                val response =
                    RetrofitClient.api.getAllNewReleasedBooks()

                println("HTTP CODE = ${response.code()}")

                if (response.isSuccessful) {

                    val body =
                        response.body().orEmpty()

                    println(
                        "NEW RELEASED BOOKS COUNT = ${body.size}"
                    )

                    _books.value =
                        body.map {
                            it.toBookListItemData()
                        }

                } else {

                    println(
                        "NEW RELEASED BOOKS FAILED = ${response.code()}"
                    )

                    println(
                        "ERROR = ${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "NEW RELEASED BOOKS EXCEPTION = ${e.message}"
                )

                e.printStackTrace()
            }
        }
    }
}