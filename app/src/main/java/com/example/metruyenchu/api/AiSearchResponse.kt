package com.example.metruyenchu.api

data class AiSearchResponse(
    val summary: String,
    val keywords: List<String>,
    val genres: List<String>
)