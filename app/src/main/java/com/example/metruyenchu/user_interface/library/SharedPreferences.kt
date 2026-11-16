package com.example.metruyenchu.user_interface.library

import android.content.Context

object LibrarySortPrefs {
    private const val PREF_NAME = "library_sort_prefs"
    private const val KEY_HISTORY_SORT = "history_sort"
    private const val KEY_BOOKMARK_SORT = "bookmark_sort"

    fun getHistorySort(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HISTORY_SORT, "Mới đọc") ?: "Mới đọc"
    }

    fun setHistorySort(context: Context, sort: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_HISTORY_SORT, sort).apply()
    }

    fun getBookmarkSort(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_BOOKMARK_SORT, "Mới lưu") ?: "Mới lưu"
    }

    fun setBookmarkSort(context: Context, sort: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_BOOKMARK_SORT, sort).apply()
    }
}