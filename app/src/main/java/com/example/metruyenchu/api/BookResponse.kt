package com.example.metruyenchu.api

data class BookResponse(
    val bookId: Long,
    val title: String,
    val author: String,
    val description: String?,
    val coverUrl: String?,
    val status: String?,
    val chapterCount: Int?,
    val viewCount: Long?,
    val weeklyViewCount: Long?,
    val recommendationCount: Long?,
    val weeklyRecommendationCount: Long?,
    val commentCount: Long?,
    val bookmarkCount: Long?,
    val ratingCount: Long?,
    val ratingScore: Double?,
    val createdAt: String?,
    val bookGenres: List<BookGenreResponse>?,
    val bookTags: List<BookTagResponse>? = null
)

data class BookGenreResponse(
    val id: BookGenreIdResponse?,
    val genre: GenreResponse?
)

data class BookGenreIdResponse(
    val bookId: Long?,
    val genreId: Long?
)

data class GenreResponse(
    val genreId: Long?,
    val genreName: String?
)

data class BookTagResponse(
    val id: BookTagIdResponse?,
    val tag: TagResponse?
)

data class BookTagIdResponse(
    val bookId: Long?,
    val tagId: Long?
)

data class TagResponse(
    val tagId: Long?,
    val tagName: String?
)
