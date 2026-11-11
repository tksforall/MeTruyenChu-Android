package com.example.metruyenchu.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.repository.CommentRepository
import com.example.metruyenchu.user_interface.Book.CommentUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {

    private val repository =
        CommentRepository()


    // ==================================================
    // DANH SÁCH COMMENT
    // ==================================================

    private val _comments =
        MutableStateFlow<List<CommentUiModel>>(emptyList())

    val comments: StateFlow<List<CommentUiModel>> =
        _comments.asStateFlow()


    // ==================================================
    // LOADING
    // ==================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ==================================================
    // ERROR
    // ==================================================

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()


    // ==================================================
    // LOAD COMMENT
    // ==================================================

    fun loadComments(
        bookId: Long
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            val commentResult =
                repository.getMainComments(bookId)

            commentResult.fold(

                onSuccess = { commentList ->

                    val result =
                        mutableListOf<CommentUiModel>()

                    for (comment in commentList) {

                        // ----------------------------------
                        // USER
                        // ----------------------------------

                        val user =
                            repository
                                .getUser(comment.userId)
                                .getOrNull()


                        // ----------------------------------
                        // CHAPTER
                        // ----------------------------------

                        val chapter =
                            comment.chapterId?.let {

                                repository
                                    .getChapter(it)
                                    .getOrNull()
                            }


                        // ----------------------------------
                        // LIKE
                        // ----------------------------------

                        val likeStatus =
                            repository
                                .getLikeStatus(
                                    comment.commentId
                                )
                                .getOrNull()


                        // ----------------------------------
                        // UI MODEL
                        // ----------------------------------

                        result.add(

                            CommentUiModel(

                                commentId =
                                    comment.commentId,

                                userId =
                                    comment.userId,

                                userName =
                                    user?.userName
                                        ?: "User",

                                avatarUrl =
                                    user?.avatarUrl,

                                content =
                                    comment.content,

                                chapterId =
                                    comment.chapterId,

                                chapterNumber =
                                    chapter?.chapterNumber,

                                chapterTitle =
                                    chapter?.title,

                                likeCount =
                                    comment.likeCount,

                                replyCount =
                                    comment.replyCount,

                                isLiked =
                                    likeStatus?.liked
                                        ?: false,

                                createdAt =
                                    comment.createdAt
                            )
                        )
                    }

                    _comments.value = result
                },

                onFailure = {

                    _error.value =
                        it.message
                            ?: "Không thể tải bình luận"
                }
            )

            _isLoading.value = false
        }
    }


    // ==================================================
    // LIKE / UNLIKE
    // ==================================================

    fun toggleLike(
        commentId: Long
    ) {

        viewModelScope.launch {

            val currentList =
                _comments.value

            val comment =
                currentList.find {
                    it.commentId == commentId
                }
                    ?: return@launch


            if (comment.isLiked) {

                val result =
                    repository.unlikeComment(
                        commentId
                    )

                if (result.isSuccess) {

                    _comments.value =
                        currentList.map {

                            if (
                                it.commentId ==
                                commentId
                            ) {

                                it.copy(
                                    isLiked = false,

                                    likeCount =
                                        maxOf(
                                            0,
                                            it.likeCount - 1
                                        )
                                )

                            } else {

                                it
                            }
                        }
                }

            } else {

                val result =
                    repository.likeComment(
                        commentId
                    )

                if (result.isSuccess) {

                    _comments.value =
                        currentList.map {

                            if (
                                it.commentId ==
                                commentId
                            ) {

                                it.copy(
                                    isLiked = true,

                                    likeCount =
                                        it.likeCount + 1
                                )

                            } else {

                                it
                            }
                        }
                }
            }
        }
    }
}