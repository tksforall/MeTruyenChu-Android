package com.example.metruyenchu.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ==================================================
    // AUTH
    // ==================================================

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<UserResponse>


    @POST("api/auth/login")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<LoginResponse>


    // ==================================================
    // USER
    // ==================================================

    @GET("api/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long
    ): Response<UserResponse>


    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<UserResponse>


    // ==================================================
    // PROFILE
    // ==================================================

    @PUT("api/users/me")
    suspend fun updateCurrentUser(
        @Body request: UpdateProfileRequest
    ): Response<UserResponse>


    // ==================================================
    // PASSWORD
    // ==================================================

    @PUT("api/users/me/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<okhttp3.ResponseBody>


    // ==================================================
    // EMAIL
    // ==================================================

    @PUT("api/users/me/email")
    suspend fun changeEmail(
        @Body request: ChangeEmailRequest
    ): Response<okhttp3.ResponseBody>


    // ==================================================
    // USER SETTINGS
    // ==================================================

    @GET("api/users/me/settings")
    suspend fun getUserSettings():
            Response<UserSettingsResponse>


    @PUT("api/users/me/settings")
    suspend fun updateUserSettings(
        @Body request: UserSettingsRequest
    ): Response<UserSettingsResponse>


    // ==================================================
    // BOOK
    // ==================================================

    @GET("api/books")
    suspend fun getAllBooks():
            Response<List<BookResponse>>


    @GET("api/books/{id}")
    suspend fun getBookById(
        @Path("id") id: Long
    ): Response<BookResponse>


    @POST("api/books/{bookId}/recommend")
    suspend fun recommendBook(
        @Path("bookId") bookId: Long
    ): Response<BookResponse>


    @GET("api/books/latest")
    suspend fun getLatestBooks():
            Response<List<BookResponse>>


    // ==================================================
    // HOME - TOP BOOKS
    // ==================================================

    @GET("api/books/recommended")
    suspend fun getRecommendedBooks():
            Response<List<BookResponse>>


    @GET("api/books/new-released")
    suspend fun getNewReleasedBooks():
            Response<List<BookResponse>>


    @GET("api/books/completed")
    suspend fun getCompletedBooks():
            Response<List<BookResponse>>


    // ==================================================
    // BOOK LIST - ALL
    // ==================================================

    @GET("api/books/recommended/all")
    suspend fun getAllRecommendedBooks():
            Response<List<BookResponse>>


    @GET("api/books/new-released/all")
    suspend fun getAllNewReleasedBooks():
            Response<List<BookResponse>>


    @GET("api/books/completed/all")
    suspend fun getAllCompletedBooks():
            Response<List<BookResponse>>


    // ==================================================
    // BOOK SEARCH
    // ==================================================

    @GET("api/books/search")
    suspend fun searchBooks(
        @Query("keyword") keyword: String
    ): Response<List<BookResponse>>


    // ==================================================
    // BOOK FILTER
    // ==================================================

    @POST("api/books/filter")
    suspend fun filterBooks(
        @Body request: BookFilterRequest
    ): Response<List<BookResponse>>


    // ==================================================
    // CHAPTER
    // ==================================================

    @GET("api/chapters")
    suspend fun getAllChapters():
            Response<List<ChapterResponse>>


    @GET("api/chapters/{id}")
    suspend fun getChapterById(
        @Path("id") id: Long
    ): Response<ChapterResponse>


    @GET("api/chapters/book/{bookId}")
    suspend fun getChaptersByBookId(
        @Path("bookId") bookId: Long
    ): Response<List<ChapterResponse>>


    @GET("api/chapters/book/{bookId}/chapter/{chapterNumber}")
    suspend fun getChapter(
        @Path("bookId") bookId: Long,
        @Path("chapterNumber") chapterNumber: Int
    ): Response<ChapterResponse>


    @POST("api/chapters/{id}/view")
    suspend fun increaseChapterViewCount(
        @Path("id") id: Long
    ): Response<ChapterResponse>


    // ==================================================
    // READING HISTORY
    // ==================================================

    @GET("api/reading-history/me")
    suspend fun getMyReadingHistory():
            Response<List<ReadingHistoryResponse>>


    @POST("api/reading-history/user/{userId}/book/{bookId}")
    suspend fun saveReadingHistory(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long,
        @Query("chapterId") chapterId: Long,
        @Query("progress") progress: Double
    ): Response<ReadingHistoryResponse>


    // ==================================================
    // BOOKMARK / FOLLOW
    // ==================================================

    @GET("api/follows/me")
    suspend fun getMyFollows():
            Response<List<BookFollowResponse>>


    @POST("api/follows/user/{userId}/book/{bookId}")
    suspend fun followBook(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long
    ): Response<BookFollowResponse>


    @DELETE("api/follows/user/{userId}/book/{bookId}")
    suspend fun unfollowBook(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long
    ): Response<Unit>


    // ==================================================
    // LIBRARY
    // ==================================================

    @GET("api/library/me")
    suspend fun getMyLibrary(): Response<List<BookLibraryResponse>>

    @POST("api/library/user/{userId}/book/{bookId}")
    suspend fun addBookToLibrary(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long
    ): Response<BookLibraryResponse>

    @DELETE("api/library/user/{userId}/book/{bookId}")
    suspend fun removeBookFromLibrary(
        @Path("userId") userId: Long,
        @Path("bookId") bookId: Long
    ): Response<Unit>


    // ==================================================
    // NOTIFICATION
    // ==================================================

    @GET("api/notifications/me")
    suspend fun getMyNotifications():
            Response<List<NotificationResponse>>


    @PUT("api/notifications/{notificationId}/read")
    suspend fun markNotificationRead(
        @Path("notificationId") notificationId: Long
    ): Response<NotificationResponse>


    // ==================================================
    // COMMENT
    // ==================================================

    @GET("api/comments/book/{bookId}/main")
    suspend fun getMainComments(
        @Path("bookId") bookId: Long
    ): Response<List<CommentResponse>>


    @GET("api/comments/{commentId}")
    suspend fun getComment(
        @Path("commentId") commentId: Long
    ): Response<CommentResponse>


    @GET("api/comments/{commentId}/replies")
    suspend fun getCommentReplies(
        @Path("commentId") commentId: Long
    ): Response<List<CommentResponse>>


    @POST("api/comments")
    suspend fun createComment(
        @Body request: CommentCreateRequest
    ): Response<CommentResponse>


    // ==================================================
    // COMMENT LIKE
    // ==================================================

    @POST("api/comments/{commentId}/like")
    suspend fun likeComment(
        @Path("commentId") commentId: Long
    ): Response<Any>


    @DELETE("api/comments/{commentId}/like")
    suspend fun unlikeComment(
        @Path("commentId") commentId: Long
    ): Response<Unit>


    @POST("api/comment-likes/comment/{commentId}")
    suspend fun addCommentLike(
        @Path("commentId") commentId: Long
    ): Response<Any>


    @DELETE("api/comment-likes/comment/{commentId}")
    suspend fun removeCommentLike(
        @Path("commentId") commentId: Long
    ): Response<Unit>


    @GET("api/comment-likes/comment/{commentId}/status")
    suspend fun getCommentLikeStatus(
        @Path("commentId") commentId: Long
    ): Response<CommentLikeStatusResponse>


    // ==================================================
    // REVIEW
    // ==================================================

    @GET("api/reviews/book/{bookId}/main")
    suspend fun getMainReviews(
        @Path("bookId") bookId: Long
    ): Response<List<ReviewResponse>>


    @GET("api/reviews/{reviewId}")
    suspend fun getReview(
        @Path("reviewId") reviewId: Long
    ): Response<ReviewResponse>


    @GET("api/reviews/{reviewId}/replies")
    suspend fun getReviewReplies(
        @Path("reviewId") reviewId: Long
    ): Response<List<ReviewResponse>>


    @POST("api/reviews")
    suspend fun createReview(
        @Body request: ReviewCreateRequest
    ): Response<ReviewResponse>


    // ==================================================
    // REVIEW LIKE
    // ==================================================

    @POST("api/review-likes/review/{reviewId}")
    suspend fun likeReview(
        @Path("reviewId") reviewId: Long
    ): Response<Any>


    @DELETE("api/review-likes/review/{reviewId}")
    suspend fun unlikeReview(
        @Path("reviewId") reviewId: Long
    ): Response<Unit>


    @GET("api/review-likes/review/{reviewId}/status")
    suspend fun getReviewLikeStatus(
        @Path("reviewId") reviewId: Long
    ): Response<ReviewLikeStatusResponse>


    // ==================================================
    // REPORT
    // ==================================================

    @POST("api/reports")
    suspend fun createReport(
        @Body request: ReportCreateRequest
    ): Response<ReportResponse>



    @GET("api/reviews/book/{bookId}")
    suspend fun getReviewsByBook(
        @Path("bookId") bookId: Long
    ): Response<List<ReviewResponse>>


    // ==================================================
    // AI SEARCH
    // ==================================================

    @POST("api/ai/search")
    suspend fun searchByAiDescription(
        @Body request: AiSearchRequest
    ): Response<AiSearchResponse>


}
