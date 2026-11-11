package com.example.metruyenchu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.toBookListItemData
import com.example.metruyenchu.user_interface.explore.components.BookListItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class HomeViewModel : ViewModel() {

    private val _latestBooks =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val latestBooks: StateFlow<List<BookListItemData>>
        get() = _latestBooks


    private val _recommendedBooks =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val recommendedBooks: StateFlow<List<BookListItemData>>
        get() = _recommendedBooks


    private val _newReleasedBooks =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val newReleasedBooks: StateFlow<List<BookListItemData>>
        get() = _newReleasedBooks


    private val _completedBooks =
        MutableStateFlow<List<BookListItemData>>(emptyList())

    val completedBooks: StateFlow<List<BookListItemData>>
        get() = _completedBooks




    fun loadLatestBooks() {

        Log.d("HOME_API", "loadLatestBooks() ĐÃ ĐƯỢC GỌI")

        viewModelScope.launch {

            try {

                Log.d("HOME_API", "ĐANG GỌI API /api/books/latest")

                val response =
                    RetrofitClient.api.getLatestBooks()

                Log.d("HOME_API", "HTTP CODE = ${response.code()}")

                if (response.isSuccessful) {

                    val body = response.body()

                    Log.d(
                        "HOME_API",
                        "BOOK COUNT = ${body?.size}"
                    )

                    _latestBooks.value =
                        body
                            ?.map {
                                it.toBookListItemData()
                            }
                            ?: emptyList()

                    Log.d(
                        "HOME_API",
                        "LATEST BOOKS LOADED = ${_latestBooks.value.size}"
                    )

                } else {

                    Log.e(
                        "HOME_API",
                        "API ERROR = ${response.code()}"
                    )

                    Log.e(
                        "HOME_API",
                        "ERROR BODY = ${response.errorBody()?.string()}"
                    )
                }

            } catch (e: Exception) {

                Log.e(
                    "HOME_API",
                    "EXCEPTION = ${e.message}",
                    e
                )
            }
        }
    }

    fun loadHomeSections() {

        viewModelScope.launch {

            try {

                println("========== HOME SECTIONS ==========")

                // ==================================================
                // ĐỀ CỬ
                // ==================================================

                val recommendedResponse =
                    RetrofitClient.api.getRecommendedBooks()

                println(
                    "RECOMMENDED HTTP CODE = ${recommendedResponse.code()}"
                )

                if (recommendedResponse.isSuccessful) {

                    _recommendedBooks.value =
                        recommendedResponse
                            .body()
                            .orEmpty()
                            .map {
                                it.toBookListItemData()
                            }

                    println(
                        "RECOMMENDED = ${_recommendedBooks.value.size}"
                    )
                }


                // ==================================================
                // MỚI ĐĂNG
                // ==================================================

                val newReleasedResponse =
                    RetrofitClient.api.getNewReleasedBooks()

                println(
                    "NEW RELEASED HTTP CODE = ${newReleasedResponse.code()}"
                )

                if (newReleasedResponse.isSuccessful) {

                    _newReleasedBooks.value =
                        newReleasedResponse
                            .body()
                            .orEmpty()
                            .map {
                                it.toBookListItemData()
                            }

                    println(
                        "NEW RELEASED = ${_newReleasedBooks.value.size}"
                    )
                }


                // ==================================================
                // MỚI HOÀN THÀNH
                // ==================================================

                val completedResponse =
                    RetrofitClient.api.getCompletedBooks()

                println(
                    "COMPLETED HTTP CODE = ${completedResponse.code()}"
                )

                if (completedResponse.isSuccessful) {

                    _completedBooks.value =
                        completedResponse
                            .body()
                            .orEmpty()
                            .map {
                                it.toBookListItemData()
                            }

                    println(
                        "COMPLETED = ${_completedBooks.value.size}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "HOME SECTIONS EXCEPTION = ${e.message}"
                )

                e.printStackTrace()
            }
        }
    }


    fun loadRecommendedBooks() {

        viewModelScope.launch {

            try {

                val response =
                    RetrofitClient.api.getRecommendedBooks()

                if (response.isSuccessful) {

                    _recommendedBooks.value =
                        response.body()
                            ?.map {
                                it.toBookListItemData()
                            }
                            ?: emptyList()

                } else {

                    println(
                        "RECOMMENDED ERROR = ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "RECOMMENDED EXCEPTION = ${e.message}"
                )
            }
        }
    }


    fun loadNewReleasedBooks() {

        viewModelScope.launch {

            try {

                val response =
                    RetrofitClient.api.getNewReleasedBooks()

                if (response.isSuccessful) {

                    _newReleasedBooks.value =
                        response.body()
                            ?.map {
                                it.toBookListItemData()
                            }
                            ?: emptyList()

                } else {

                    println(
                        "NEW RELEASED ERROR = ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "NEW RELEASED EXCEPTION = ${e.message}"
                )
            }
        }
    }


    fun loadCompletedBooks() {

        viewModelScope.launch {

            try {

                val response =
                    RetrofitClient.api.getCompletedBooks()

                if (response.isSuccessful) {

                    _completedBooks.value =
                        response.body()
                            ?.map {
                                it.toBookListItemData()
                            }
                            ?: emptyList()

                } else {

                    println(
                        "COMPLETED ERROR = ${response.code()}"
                    )
                }

            } catch (e: Exception) {

                println(
                    "COMPLETED EXCEPTION = ${e.message}"
                )
            }
        }
    }

}