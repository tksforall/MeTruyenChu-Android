package com.example.metruyenchu.repository

import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.api.RetrofitClient

class ChapterRepository {

    private val api = RetrofitClient.api

    suspend fun getChaptersByBookId(
        bookId: Long
    ): Result<List<ChapterResponse>> {
        return try {
            val response = api.getChaptersByBookId(bookId)

            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(
                    Exception("Lỗi lấy danh sách chương: HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChapter(
        bookId: Long,
        chapterNumber: Int
    ): Result<ChapterResponse> {
        return try {
            val response = api.getChapter(bookId, chapterNumber)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(
                    Exception("API không trả về dữ liệu chương")
                )
            } else {
                Result.failure(
                    Exception("Lỗi lấy chương: HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun increaseViewCount(
        chapterId: Long
    ): Result<ChapterResponse> {
        return try {
            val response = api.increaseChapterViewCount(chapterId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(
                    Exception("API không trả về chương sau khi tăng view")
                )
            } else {
                Result.failure(
                    Exception("Lỗi tăng lượt đọc: HTTP ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
