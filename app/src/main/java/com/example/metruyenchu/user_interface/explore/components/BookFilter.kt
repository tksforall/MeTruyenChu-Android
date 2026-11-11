package com.example.metruyenchu.user_interface.explore.components

import com.example.metruyenchu.api.BookFilterRequest

data class BookFilter(

    // Chọn 1
    val sort: String = "",
    val status: String = "",
    val chapterRange: String = "",
    val publishDate: String = "",

    // Chọn nhiều
    val genres: Set<String> = emptySet(),
    val personalities: Set<String> = emptySet(),
    val worlds: Set<String> = emptySet(),
    val tags: Set<String> = emptySet()
)


// ==========================================================
// UI FILTER → API REQUEST
// ==========================================================

fun BookFilter.toRequest(): BookFilterRequest {

    return BookFilterRequest(
        sort = sort,
        status = status,
        chapterRange = chapterRange,
        publishDate = publishDate,
        genres = genres,
        personalities = personalities,
        worlds = worlds,
        tags = tags
    )
}