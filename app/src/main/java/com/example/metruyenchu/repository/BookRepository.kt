package com.example.metruyenchu.repository

import com.example.metruyenchu.api.BookFilterRequest
import com.example.metruyenchu.api.BookResponse
import com.example.metruyenchu.api.RetrofitClient

class BookRepository {

    private val api = RetrofitClient.api

    // ==================================================
    // FILTER
    // ==================================================

    suspend fun filterBooks(
        request: BookFilterRequest
    ): Result<List<BookResponse>> {

        return try {

            val response = api.filterBooks(request)

            if (response.isSuccessful) {

                Result.success(
                    response.body() ?: emptyList()
                )

            } else {

                Result.failure(
                    Exception(
                        "Filter API error: ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}