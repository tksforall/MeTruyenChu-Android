package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.ChapterResponse
import com.example.metruyenchu.repository.ChapterRepository
import com.example.metruyenchu.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReaderViewModel : ViewModel() {
    private val chapterRepository = ChapterRepository()
    private val libraryRepository = LibraryRepository()
    private val _chapter = MutableStateFlow<ChapterResponse?>(null)
    val chapter: StateFlow<ChapterResponse?> = _chapter.asStateFlow()
    private val _chapters = MutableStateFlow<List<ChapterResponse>>(emptyList())
    val chapters: StateFlow<List<ChapterResponse>> = _chapters.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(bookId: Long, chapterNumber: Int, userId: Long) = viewModelScope.launch {
        _isLoading.value = true; _error.value = null
        try {
            val chapters = chapterRepository.getChaptersByBookId(bookId).getOrElse { throw it }
                .sortedBy { it.chapterNumber }
            _chapters.value = chapters
            val chapter = chapterRepository.getChapter(bookId, chapterNumber).getOrElse { throw it }
            _chapter.value = chapter
            chapterRepository.increaseViewCount(chapter.chapterId)
            if (userId > 0) libraryRepository.saveReadingHistory(userId, bookId, chapter.chapterId, 0.0)
        } catch (e: Exception) { _error.value = e.message ?: "Không thể tải chương" }
        finally { _isLoading.value = false }
    }

    fun selectChapter(bookId: Long, chapterNumber: Int, userId: Long) = viewModelScope.launch {
        try {
            val chapter = chapterRepository.getChapter(bookId, chapterNumber).getOrElse { throw it }
            _chapter.value = chapter
            chapterRepository.increaseViewCount(chapter.chapterId)
            if (userId > 0) libraryRepository.saveReadingHistory(userId, bookId, chapter.chapterId, 0.0)
        } catch (e: Exception) { _error.value = e.message ?: "Không thể chuyển chương" }
    }

    fun saveProgress(userId: Long, bookId: Long, progress: Double) {
        val chapter = _chapter.value ?: return
        if (userId <= 0) return
        viewModelScope.launch { libraryRepository.saveReadingHistory(userId, bookId, chapter.chapterId, progress.coerceIn(0.0, 1.0)) }
    }
}
