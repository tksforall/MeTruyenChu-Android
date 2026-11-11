package com.example.metruyenchu.user_interface.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metruyenchu.api.ReviewCreateRequest
import com.example.metruyenchu.api.ReviewResponse
import com.example.metruyenchu.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookReviewViewModel : ViewModel() {

    // ==========================================================
    // DANH SÁCH REVIEW
    // ==========================================================

    private val _reviews =
        MutableStateFlow<List<ReviewResponse>>(emptyList())

    val reviews: StateFlow<List<ReviewResponse>> =
        _reviews.asStateFlow()


    // ==========================================================
    // CÁC REVIEW USER ĐÃ LIKE
    // ==========================================================

    private val _likedReviews =
        MutableStateFlow<Set<Long>>(emptySet())

    val likedReviews: StateFlow<Set<Long>> =
        _likedReviews.asStateFlow()


    // ==========================================================
    // LOADING
    // ==========================================================

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()


    // ==========================================================
    // ERROR
    // ==========================================================

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()


    // ==========================================================
    // CREATE REVIEW LOADING
    // ==========================================================

    private val _isCreatingReview =
        MutableStateFlow(false)

    val isCreatingReview: StateFlow<Boolean> =
        _isCreatingReview.asStateFlow()


    // ==========================================================
    // CREATE REVIEW ERROR
    // ==========================================================

    private val _createReviewError =
        MutableStateFlow<String?>(null)

    val createReviewError: StateFlow<String?> =
        _createReviewError.asStateFlow()


    // ==========================================================
    // LẤY REVIEW THEO TRUYỆN
    // ==========================================================

    fun loadReviews(bookId: Long) {

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                val response =
                    RetrofitClient.api
                        .getMainReviews(bookId)

                if (response.isSuccessful) {

                    _reviews.value =
                        response.body().orEmpty()

                } else {

                    _error.value =
                        "Không thể tải đánh giá (${response.code()})"
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Lỗi kết nối"

            } finally {

                _isLoading.value = false
            }
        }
    }


    // ==========================================================
    // TẠO REVIEW MỚI
    // ==========================================================

    fun createReview(
        bookId: Long,
        rating: Double?,
        content: String,
        onSuccess: (ReviewResponse) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        viewModelScope.launch {

            _isCreatingReview.value = true
            _createReviewError.value = null

            try {

                val request =
                    ReviewCreateRequest(
                        bookId = bookId,
                        parentReviewId = null,
                        chapterId = null,
                        rating = rating,
                        content = content
                    )

                val response =
                    RetrofitClient.api
                        .createReview(request)

                if (response.isSuccessful) {

                    val newReview =
                        response.body()

                    if (newReview != null) {

                        _reviews.value =
                            _reviews.value + newReview

                        onSuccess(newReview)

                    } else {

                        val message =
                            "Backend không trả về dữ liệu review"

                        _createReviewError.value =
                            message

                        onError(message)
                    }

                } else {

                    val message =
                        "Không thể đăng đánh giá (${response.code()})"

                    _createReviewError.value =
                        message

                    onError(message)
                }

            } catch (e: Exception) {

                val message =
                    e.message ?: "Lỗi kết nối"

                _createReviewError.value =
                    message

                onError(message)

            } finally {

                _isCreatingReview.value = false
            }
        }
    }


    // ==========================================================
    // THÊM REVIEW TRỰC TIẾP
    // ==========================================================

    fun addReview(
        review: ReviewResponse
    ) {

        _reviews.value =
            _reviews.value + review
    }


    // ==========================================================
    // REFRESH
    // ==========================================================

    fun refresh(
        bookId: Long
    ) {

        loadReviews(bookId)
    }


    // ==========================================================
    // XÓA LỖI CREATE REVIEW
    // ==========================================================

    fun clearCreateReviewError() {

        _createReviewError.value = null
    }


    // ==========================================================
    // LẤY TRẠNG THÁI LIKE
    // ==========================================================

    fun loadLikeStatus(
        reviewId: Long
    ) {

        viewModelScope.launch {

            try {

                val response =
                    RetrofitClient.api
                        .getReviewLikeStatus(reviewId)

                if (response.isSuccessful) {

                    val liked =
                        response.body()?.liked ?: false

                    if (liked) {

                        _likedReviews.value =
                            _likedReviews.value + reviewId

                    } else {

                        _likedReviews.value =
                            _likedReviews.value - reviewId
                    }
                }

            } catch (e: Exception) {

                // Không làm crash UI
            }
        }
    }


    // ==========================================================
    // LIKE / UNLIKE
    // ==========================================================

    fun toggleLike(
        reviewId: Long
    ) {

        viewModelScope.launch {

            try {

                val currentlyLiked =
                    _likedReviews.value.contains(reviewId)

                if (currentlyLiked) {

                    // ------------------------------------------------
                    // UNLIKE
                    // ------------------------------------------------

                    val response =
                        RetrofitClient.api
                            .unlikeReview(reviewId)

                    if (response.isSuccessful) {

                        _likedReviews.value =
                            _likedReviews.value - reviewId

                        updateLikeCount(
                            reviewId = reviewId,
                            increase = false
                        )
                    }

                } else {

                    // ------------------------------------------------
                    // LIKE
                    // ------------------------------------------------

                    val response =
                        RetrofitClient.api
                            .likeReview(reviewId)

                    if (response.isSuccessful) {

                        _likedReviews.value =
                            _likedReviews.value + reviewId

                        updateLikeCount(
                            reviewId = reviewId,
                            increase = true
                        )
                    }
                }

            } catch (e: Exception) {

                // Không thay đổi UI nếu API lỗi
            }
        }
    }


    // ==========================================================
    // CẬP NHẬT LIKE COUNT
    // ==========================================================

    private fun updateLikeCount(
        reviewId: Long,
        increase: Boolean
    ) {

        _reviews.value =
            _reviews.value.map { review ->

                if (review.reviewId == reviewId) {

                    review.copy(
                        likeCount =
                            if (increase) {
                                review.likeCount + 1
                            } else {
                                maxOf(
                                    0L,
                                    review.likeCount - 1
                                )
                            }
                    )

                } else {

                    review
                }
            }
    }
}