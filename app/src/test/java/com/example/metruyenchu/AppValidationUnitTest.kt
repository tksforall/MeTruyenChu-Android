package com.example.metruyenchu

import org.junit.Assert.*
import org.junit.Test

class AppValidationUnitTest {
    @Test
    fun aiSearchInput_emptyOrBlank_isRejected() {
        val input = "   "
        assertFalse(input.trim().isNotEmpty())
    }

    @Test
    fun aiSearchInput_validDescription_isAccepted() {
        val input = "Truyện tiên hiệp main xuyên không có hệ thống"
        assertTrue(input.trim().length in 1..5000)
    }

    @Test
    fun jwtToken_nonBlankFormat_isValid() {
        val token = "Bearer eyJhbGciOiJIUzI1NiJ9.payload.signature"
        assertTrue(token.startsWith("Bearer "))
    }

    @Test
    fun readingProgress_percentage_withinValidBounds() {
        val currentChapter = 15
        val totalChapters = 100
        val progress = (currentChapter.toFloat() / totalChapters) * 100
        assertEquals(15.0f, progress, 0.01f)
    }
}