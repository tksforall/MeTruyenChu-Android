package com.example.metruyenchu.api

data class BookFilterRequest(

    // ==================================================
    // CHỌN 1
    // ==================================================

    val sort: String = "",

    val status: String = "",

    val chapterRange: String = "",

    val publishDate: String = "",


    // ==================================================
    // CHỌN NHIỀU
    // ==================================================

    val genres: Set<String> = emptySet(),

    val personalities: Set<String> = emptySet(),

    val worlds: Set<String> = emptySet(),

    val tags: Set<String> = emptySet()
)