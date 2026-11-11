package com.example.metruyenchu.repository

import com.example.metruyenchu.api.BookLibraryResponse
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.ReadingHistoryResponse
import com.example.metruyenchu.api.RetrofitClient

class LibraryRepository {
    private val api = RetrofitClient.api

    suspend fun getMyReadingHistory(): Result<List<ReadingHistoryResponse>> = runCatching {
        val response = api.getMyReadingHistory()
        if (response.isSuccessful) response.body().orEmpty()
        else error("Lỗi lấy lịch sử: HTTP ${response.code()}")
    }

    suspend fun saveReadingHistory(userId: Long, bookId: Long, chapterId: Long, progress: Double): Result<ReadingHistoryResponse> = runCatching {
        val response = api.saveReadingHistory(userId, bookId, chapterId, progress)
        if (response.isSuccessful) response.body() ?: error("API không trả về lịch sử")
        else error("Lỗi lưu tiến độ: HTTP ${response.code()}")
    }

    suspend fun getMyLibrary(): Result<List<BookLibraryResponse>> = runCatching {
        val response = api.getMyLibrary()
        if (response.isSuccessful) response.body().orEmpty()
        else error("Lỗi lấy tủ truyện: HTTP ${response.code()}")
    }

    suspend fun addBookToLibrary(userId: Long, bookId: Long): Result<BookLibraryResponse> = runCatching {
        val response = api.addBookToLibrary(userId, bookId)
        if (response.isSuccessful) response.body() ?: error("API không trả về dữ liệu tủ truyện")
        else error("Lỗi thêm vào tủ: HTTP ${response.code()}")
    }

    suspend fun removeBookFromLibrary(userId: Long, bookId: Long): Result<Unit> = runCatching {
        val response = api.removeBookFromLibrary(userId, bookId)
        if (!response.isSuccessful) error("Lỗi bỏ khỏi tủ: HTTP ${response.code()}")
    }

    suspend fun getAllBooks(): Result<List<BookResponse>> = runCatching {
        val response = api.getAllBooks()
        if (response.isSuccessful) response.body().orEmpty()
        else error("Lỗi lấy danh sách truyện: HTTP ${response.code()}")
    }

    suspend fun getBookById(bookId: Long): Result<BookResponse> = runCatching {
        val response = api.getBookById(bookId)
        if (response.isSuccessful) response.body() ?: error("Không có dữ liệu truyện")
        else error("Lỗi HTTP ${response.code()}")
    }
}
