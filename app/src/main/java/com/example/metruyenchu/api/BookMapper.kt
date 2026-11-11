package com.example.metruyenchu.api

import com.example.metruyenchu.user_interface.explore.components.BookListItemData

fun BookResponse.toBookListItemData(): BookListItemData {

    val genres = bookGenres
        .orEmpty()
        .mapNotNull {
            it.genre?.genreName
        }
        .distinct()

    return BookListItemData(
        bookId = bookId,

        imageUrl = cleanImageUrl(coverUrl),

        title = title,

        author = author,

        description = description ?: "",
        genre = genres.getOrNull(0) ?: "",

        subGenre = genres.getOrNull(1) ?: "",

        rating = String.format(
            "%.1f",
            ratingScore ?: 0.0
        ),

        bookmarks = (bookmarkCount ?: 0L).toString(),

        viewCount = viewCount ?: 0L,

        recommendationCount = recommendationCount ?: 0L,

        commentCount = commentCount ?: 0L
    )
}

private fun cleanImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null

    return if (url.startsWith("[") && "](" in url) {
        url.substringAfter("[")
            .substringBefore("](")
    } else {
        url
    }
}