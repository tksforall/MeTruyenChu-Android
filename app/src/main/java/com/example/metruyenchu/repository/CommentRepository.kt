package com.example.metruyenchu.repository

import com.example.metruyenchu.api.CommentLikeStatusResponse
import com.example.metruyenchu.api.CommentResponse
import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.UserResponse
import com.example.metruyenchu.api.ChapterResponse

class CommentRepository {

    private val api = RetrofitClient.api

    // ==================================================
    // LẤY COMMENT CHÍNH CỦA BOOK
    // ==================================================

    suspend fun getMainComments(
        bookId: Long
    ): Result<List<CommentResponse>> {

        return try {

            val response = api.getMainComments(bookId)

            if (response.isSuccessful) {

                Result.success(
                    response.body().orEmpty()
                )

            } else {

                Result.failure(
                    Exception(
                        "Lỗi lấy bình luận: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==================================================
    // LẤY USER
    // ==================================================

    suspend fun getUser(
        userId: Long
    ): Result<UserResponse> {

        return try {

            val response = api.getUserById(userId)

            if (response.isSuccessful) {

                response.body()?.let {

                    Result.success(it)

                } ?: Result.failure(
                    Exception("API không trả về User")
                )

            } else {

                Result.failure(
                    Exception(
                        "Lỗi lấy User: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==================================================
    // LẤY CHAPTER
    // ==================================================

    suspend fun getChapter(
        chapterId: Long
    ): Result<ChapterResponse> {

        return try {

            val response =
                api.getChapterById(chapterId)

            if (response.isSuccessful) {

                response.body()?.let {

                    Result.success(it)

                } ?: Result.failure(
                    Exception("API không trả về Chapter")
                )

            } else {

                Result.failure(
                    Exception(
                        "Lỗi lấy Chapter: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==================================================
    // KIỂM TRA LIKE
    // ==================================================

    suspend fun getLikeStatus(
        commentId: Long
    ): Result<CommentLikeStatusResponse> {

        return try {

            val response =
                api.getCommentLikeStatus(commentId)

            if (response.isSuccessful) {

                response.body()?.let {

                    Result.success(it)

                } ?: Result.failure(
                    Exception(
                        "API không trả về trạng thái Like"
                    )
                )

            } else {

                Result.failure(
                    Exception(
                        "Lỗi kiểm tra Like: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==================================================
    // LIKE
    // ==================================================

    suspend fun likeComment(
        commentId: Long
    ): Result<Boolean> {

        return try {

            val response =
                api.addCommentLike(commentId)

            if (response.isSuccessful) {

                Result.success(true)

            } else {

                Result.failure(
                    Exception(
                        "Lỗi Like comment: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // ==================================================
    // UNLIKE
    // ==================================================

    suspend fun unlikeComment(
        commentId: Long
    ): Result<Boolean> {

        return try {

            val response =
                api.removeCommentLike(commentId)

            if (response.isSuccessful) {

                Result.success(false)

            } else {

                Result.failure(
                    Exception(
                        "Lỗi bỏ Like: HTTP ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}