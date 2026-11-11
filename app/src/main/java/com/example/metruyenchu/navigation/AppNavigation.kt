package com.example.metruyenchu.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.lifecycle.viewmodel.compose.viewModel


import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.metruyenchu.api.RetrofitClient
import com.example.metruyenchu.api.CommentResponse
import com.example.metruyenchu.api.SessionManager
import com.example.metruyenchu.api.UpdateProfileRequest
import com.example.metruyenchu.home.BookOverviewViewModel
import com.example.metruyenchu.home.ReaderViewModel
import com.example.metruyenchu.viewmodel.HomeViewModel

import com.example.metruyenchu.user_interface.account.AccountGuestScreen
import com.example.metruyenchu.user_interface.account.AccountInfoScreen
import com.example.metruyenchu.user_interface.account.AccountScreen
import com.example.metruyenchu.user_interface.account.ChangeEmailScreen
import com.example.metruyenchu.user_interface.account.ChangePasswordScreen
import com.example.metruyenchu.user_interface.account.NightModeScreen
import com.example.metruyenchu.user_interface.account.PersonalSettingsScreen

import com.example.metruyenchu.user_interface.explore.HomeScreen
import com.example.metruyenchu.user_interface.explore.CompletedBooksScreen
import com.example.metruyenchu.user_interface.explore.LastestBooksScreen
import com.example.metruyenchu.user_interface.explore.NewReleasedBooksScreen
import com.example.metruyenchu.user_interface.explore.RecommendedBooksScreen
import com.example.metruyenchu.user_interface.explore.SearchScreen
import com.example.metruyenchu.user_interface.explore.ListOfStoryScreen

import com.example.metruyenchu.user_interface.library.BookmarkScreen
import com.example.metruyenchu.user_interface.library.HistoryScreen
import com.example.metruyenchu.user_interface.library.SortLibraryScreen

import com.example.metruyenchu.user_interface.ranking.RankingScreen

import com.example.metruyenchu.user_interface.notification.NotificationScreen
import com.example.metruyenchu.user_interface.search.AISearchScreen

import com.example.metruyenchu.user_interface.reader.ReaderScreen
import com.example.metruyenchu.user_interface.reader.ChapterListScreen

import com.example.metruyenchu.user_interface.Book.BookOverview
import com.example.metruyenchu.user_interface.Book.BookReview
import com.example.metruyenchu.user_interface.Book.BookComment
import com.example.metruyenchu.user_interface.Book.CommentDetail
import com.example.metruyenchu.user_interface.Book.ReviewComment
import com.example.metruyenchu.user_interface.Book.WriteReview
import com.example.metruyenchu.user_interface.home.BookReviewViewModel

import com.example.metruyenchu.user_interface.login.ForgotPasswordScreen
import com.example.metruyenchu.user_interface.login.LoginScreen
import com.example.metruyenchu.user_interface.login.RegisterScreen
import com.example.metruyenchu.user_interface.login.ResetPasswordScreen
import kotlinx.coroutines.launch


@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    // ==========================================================
    // CONTEXT
    // ==========================================================

    val context = LocalContext.current


    // ==========================================================
    // RETROFIT INITIALIZE
    // ==========================================================

    LaunchedEffect(Unit) {
        RetrofitClient.initialize(context)
    }


    // ==========================================================
    // SESSION
    // ==========================================================

    val sessionManager = remember {
        SessionManager(context)
    }


    // ==========================================================
    // CACHE
    // ==========================================================

    fun clearAppCache() {

        try {

            context.cacheDir.deleteRecursively()

            android.widget.Toast.makeText(
                context,
                "Đã xóa cache",
                android.widget.Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            android.widget.Toast.makeText(
                context,
                "Không thể xóa cache",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            e.printStackTrace()
        }
    }


    // ==========================================================
    // LOGIN STATE
    // ==========================================================

    var isLoggedIn by remember {
        mutableStateOf(
            sessionManager.isLoggedIn()
        )
    }


    var currentBookFilter by remember {
        mutableStateOf(
            com.example.metruyenchu.user_interface.explore.components.BookFilter()
        )
    }




    // ==========================================================
    // COROUTINE
    // ==========================================================

    val coroutineScope = rememberCoroutineScope()


    // ==========================================================
    // HOME VIEW MODEL
    // ==========================================================

    val homeViewModel: HomeViewModel = viewModel()

    val latestBooks by homeViewModel.latestBooks.collectAsState()
    val recommendedBooks by homeViewModel.recommendedBooks.collectAsState()
    val newReleasedBooks by homeViewModel.newReleasedBooks.collectAsState()
    val completedBooks by homeViewModel.completedBooks.collectAsState()


    LaunchedEffect(Unit) {

        homeViewModel.loadLatestBooks()

        homeViewModel.loadHomeSections()
    }





    // ==========================================================
    // ROUTE HIỆN TẠI
    // ==========================================================

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route


    // ==========================================================
    // MAIN ROUTES
    // ==========================================================

    val mainRoutes = listOf(
        "home",
        "ranking",
        "bookmark",
        "history",
        "account"
    )


    val showBottomBar =
        currentRoute in mainRoutes


    // ==========================================================
    // SCAFFOLD
    // ==========================================================

    Scaffold(

        bottomBar = {

            if (showBottomBar) {

                BottomNavigationBar(

                    currentRoute = currentRoute,

                    onItemClick = { route ->

                        navController.navigate(route) {

                            popUpTo(
                                navController.graph.startDestinationId
                            ) {

                                saveState = true
                            }

                            launchSingleTop = true

                            restoreState = true
                        }
                    }
                )
            }
        }

    ) { innerPadding ->


        // ======================================================
        // NAV HOST
        // ======================================================

        NavHost(

            navController = navController,

            startDestination = "home",

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {


            // ==================================================
            // HOME
            // ==================================================

            composable("home") {

                HomeScreen(

                    onNotificationClick = {

                        navController.navigate(
                            "notification"
                        )
                    },

                    onAiClick = {

                        navController.navigate(
                            "ai_search"
                        )
                    },

                    onSearchClick = {

                        navController.navigate(
                            "search"
                        )
                    },

                    onListOfStoryClick = {

                        navController.navigate(
                            "list_of_story"
                        )
                    },

                    onLatestBooksClick = {

                        navController.navigate(
                            "latest_books"
                        )
                    },

                    onRecommendedBooksClick = {

                        navController.navigate(
                            "recommended_books"
                        )
                    },

                    onNewReleasedBooksClick = {

                        navController.navigate(
                            "new_released_books"
                        )
                    },

                    onCompletedBooksClick = {

                        navController.navigate(
                            "completed_books"
                        )
                    },

                    onBookClick = { bookId ->

                        navController.navigate(
                            "book_overview/$bookId/home"
                        )
                    },

                    onReadClick = { bookId ->

                        navController.navigate(
                            "reader/$bookId/1"
                        )
                    },

                    latestBooks = latestBooks,

                    recommendedBooks = recommendedBooks,

                    newReleasedBooks = newReleasedBooks,

                    completedBooks = completedBooks
                )
            }


            // ==================================================
            // DANH SÁCH TRUYỆN
            // ==================================================

            composable("list_of_story") {

                ListOfStoryScreen(

                    filter = currentBookFilter,

                    onBack = {

                        navController.popBackStack(
                            "home",
                            inclusive = false
                        )
                    },

                    onBookClick = { bookId ->

                        navController.navigate(
                            "book_overview/$bookId/list_of_story"
                        )
                    }
                )
            }


            // ==================================================
            // SEARCH
            // ==================================================

            composable("search") {

                SearchScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )


            }


            // ==================================================
            // LATEST BOOKS
            // ==================================================

            composable("latest_books") {

                LastestBooksScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    onApplyFilter = { filter ->
                        currentBookFilter = filter
                    },

                    onNavigateToListOfStory = {

                        navController.navigate("list_of_story") {

                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/${bookId}/latest_books")
                    }
                )
            }


            // ==================================================
            // RECOMMENDED BOOKS
            // ==================================================

            composable("recommended_books") {

                RecommendedBooksScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    onApplyFilter = { filter ->
                        currentBookFilter = filter
                    },

                    onNavigateToListOfStory = {

                        navController.navigate("list_of_story") {

                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/${bookId}/recommended_books")
                    }
                )
            }


            // ==================================================
            // NEW RELEASED BOOKS
            // ==================================================

            composable("new_released_books") {

                NewReleasedBooksScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    onApplyFilter = { filter ->
                        currentBookFilter = filter
                    },

                    onNavigateToListOfStory = {

                        navController.navigate("list_of_story") {

                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/${bookId}/recommended_books")
                    }
                )
            }


            // ==================================================
            // COMPLETED BOOKS
            // ==================================================

            composable("completed_books") {

                CompletedBooksScreen(

                    onBack = {
                        navController.popBackStack()
                    },

                    onApplyFilter = { filter ->
                        currentBookFilter = filter
                    },

                    onNavigateToListOfStory = {

                        navController.navigate("list_of_story") {

                            popUpTo("home") {
                                inclusive = false
                            }
                        }
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/${bookId}/completed_books")
                    }
                )
            }


            // ==================================================
            // NOTIFICATION
            // ==================================================

            composable("notification") {

                NotificationScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // AI SEARCH
            // ==================================================

            composable("ai_search") {

                AISearchScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // RANKING
            // ==================================================

            composable("ranking") {

                RankingScreen(

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/$bookId/ranking")
                    }
                )
            }


            // ==================================================
            // BOOKMARK
            // ==================================================

            composable("bookmark") {

                BookmarkScreen(

                    onNavigateToHistory = {

                        navController.navigate(
                            "history"
                        )
                    },

                    onNavigateToSort = {

                        navController.navigate(
                            "sort_library"
                        )
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/$bookId/bookmark")
                    }
                )
            }


            // ==================================================
            // HISTORY
            // ==================================================

            composable("history") {

                HistoryScreen(

                    onNavigateToBookmark = {

                        navController.navigate(
                            "bookmark"
                        )
                    },

                    onNavigateToSort = {

                        navController.navigate(
                            "sort_library"
                        )
                    },

                    onBookClick = { bookId ->
                        navController.navigate("book_overview/$bookId/history")
                    }
                )
            }


            // ==================================================
            // ACCOUNT
            // ==================================================

            composable("account") {

                if (isLoggedIn) {

                    AccountScreen(

                        userName =
                            sessionManager.getUserName()
                                ?: "Người dùng",

                        onAccountInfoClick = {

                            navController.navigate(
                                "account_info"
                            )
                        },

                        onPersonalSettingsClick = {

                            navController.navigate(
                                "personal_settings"
                            )
                        },

                        onAppSettingsClick = {

                            navController.navigate(
                                "night_mode"
                            )
                        },

                        onLogoutClick = {

                            sessionManager.logout()

                            isLoggedIn = false

                            navController.navigate(
                                "account"
                            ) {

                                launchSingleTop = true
                            }
                        },

                        onClearCacheClick = {

                            clearAppCache()
                        }
                    )

                } else {

                    AccountGuestScreen(

                        onAppSettingsClick = {

                            navController.navigate(
                                "night_mode"
                            )
                        },

                        onLoginClick = {

                            navController.navigate(
                                "login"
                            )
                        }
                    )
                }
            }


            // ==================================================
            // ACCOUNT INFO
            // ==================================================

            composable("account_info") {

                val scope =
                    rememberCoroutineScope()

                AccountInfoScreen(

                    userName =
                        sessionManager.getUserName()
                            ?: "Người dùng",

                    email =
                        sessionManager.getEmail()
                            ?: "",

                    phone = null,

                    gender = null,

                    onSave = { phone, gender ->

                        scope.launch {

                            try {

                                val request =
                                    UpdateProfileRequest(
                                        phone = phone,
                                        gender = gender
                                    )

                                val response =
                                    RetrofitClient.api
                                        .updateCurrentUser(
                                            request
                                        )

                                println(
                                    "========== UPDATE PROFILE =========="
                                )

                                println(
                                    "HTTP CODE = ${response.code()}"
                                )

                                if (response.isSuccessful) {

                                    println(
                                        "UPDATE PROFILE SUCCESS"
                                    )

                                    println(
                                        "PHONE = $phone"
                                    )

                                    println(
                                        "GENDER = $gender"
                                    )

                                } else {

                                    println(
                                        "UPDATE PROFILE FAILED"
                                    )

                                    println(
                                        response
                                            .errorBody()
                                            ?.string()
                                    )
                                }

                            } catch (e: Exception) {

                                println(
                                    "UPDATE PROFILE ERROR"
                                )

                                e.printStackTrace()
                            }
                        }
                    },

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // PERSONAL SETTINGS
            // ==================================================

            composable("personal_settings") {

                PersonalSettingsScreen(

                    onSortLibraryClick = {

                        navController.navigate(
                            "sort_library"
                        )
                    },

                    onChangePasswordClick = {

                        navController.navigate(
                            "change_password"
                        )
                    },

                    onChangeEmailClick = {

                        navController.navigate(
                            "change_email"
                        )
                    },

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // SORT LIBRARY
            // ==================================================

            composable("sort_library") {

                SortLibraryScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // CHANGE PASSWORD
            // ==================================================

            composable("change_password") {

                ChangePasswordScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // CHANGE EMAIL
            // ==================================================

            composable("change_email") {

                ChangeEmailScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // NIGHT MODE
            // ==================================================

            composable("night_mode") {

                NightModeScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // BOOK OVERVIEW
            // ==================================================

            composable(

                route = "book_overview/{bookId}/{returnRoute}",

                arguments = listOf(

                    navArgument("bookId") {
                        type = NavType.LongType
                    },

                    navArgument("returnRoute") {
                        type = NavType.StringType
                    }
                )

            ) { backStackEntry ->

                val bookId =
                    backStackEntry.arguments
                        ?.getLong("bookId")
                        ?: return@composable


                val returnRoute =
                    backStackEntry.arguments
                        ?.getString("returnRoute")
                        ?: "home"


                val bookOverviewViewModel: BookOverviewViewModel =
                    viewModel()


                val book by bookOverviewViewModel.book
                    .collectAsState()


                val isLoading by bookOverviewViewModel.isLoading
                    .collectAsState()


                val error by bookOverviewViewModel.error
                    .collectAsState()

                val currentUserId =
                    SessionManager(context).getUserId()

                LaunchedEffect(bookId) {
                    bookOverviewViewModel.loadBook(bookId)
                    bookOverviewViewModel.loadLibraryState(bookId)
                }


                when {

                    isLoading -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }
                    }


                    error != null -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = error ?: "Có lỗi xảy ra",
                                color = Color.White
                            )
                        }
                    }


                    book != null -> {

                        BookOverview(

                            book = book!!,

                            isInLibrary = bookOverviewViewModel.isInLibrary.collectAsState().value,

                            onBack = {

                                navController.popBackStack()
                            },


                            // ==========================================
                            // ĐỌC
                            // ==========================================

                            onReadClick = {

                                navController.navigate("reader/$bookId/1")
                            },


                            // ==========================================
                            // THÊM VÀO TỦ
                            // ==========================================

                            onAddToLibraryClick = {
                                bookOverviewViewModel.toggleLibrary(
                                    currentUserId,
                                    bookId
                                )
                            },


                            // ==========================================
                            // REVIEW
                            // ==========================================

                            onReviewClick = {

                                navController.navigate(
                                    "book_review/$bookId/$returnRoute"
                                )
                            },


                            // ==========================================
                            // COMMENT
                            // ==========================================

                            onCommentClick = {

                                navController.navigate(
                                    "book_comment/$bookId/$returnRoute"
                                )
                            },


                            // ==========================================
                            // CHAPTER LIST
                            // ==========================================

                            onChapterListClick = {

                                navController.navigate(
                                    "chapter_list/$bookId/1"
                                )
                            },


                            // ==========================================
                            // GENRE
                            // ==========================================

                            onGenreClick = { _ ->
                                navController.navigate("list_of_story")
                            },


                            // ==========================================
                            // TAG
                            // ==========================================

                            onTagClick = { _ ->
                                navController.navigate("list_of_story")
                            }

                        )
                    }
                }
            }


            // ==================================================
            // BOOK COMMENT
            // ==================================================


            composable(

                route = "book_comment/{bookId}/{returnRoute}",

                arguments = listOf(

                    navArgument("bookId") {
                        type = NavType.LongType
                    },

                    navArgument("returnRoute") {
                        type = NavType.StringType
                    }
                )

            ) { backStackEntry ->

                val bookId =
                    backStackEntry.arguments
                        ?.getLong("bookId")
                        ?: return@composable


                val returnRoute =
                    backStackEntry.arguments
                        ?.getString("returnRoute")
                        ?: "home"


                BookComment(

                    bookId = bookId,

                    onBack = {

                        navController.popBackStack()
                    },

                    onCommentDetailClick = { commentId ->

                        navController.navigate(
                            "comment_detail/$commentId"
                        )
                    }
                )
            }


            // ==================================================
            // BOOK REVIEW
            // ==================================================

            composable(
                route = "book_review/{bookId}/{returnRoute}",
                arguments = listOf(
                    navArgument("bookId") {
                        type = NavType.LongType
                    },
                    navArgument("returnRoute") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val bookId =
                    entry.arguments?.getLong("bookId")
                        ?: return@composable

                val returnRoute =
                    entry.arguments?.getString("returnRoute")
                        ?: "home"

                val vm: BookOverviewViewModel =
                    viewModel()

                val book by vm.book.collectAsState()

                val currentUserId =
                    SessionManager(context).getUserId()


                // ======================================================
                // LOAD BOOK
                // ======================================================

                LaunchedEffect(bookId) {

                    vm.loadBook(bookId)

                    vm.loadLibraryState(bookId)
                }


                // ======================================================
                // UI
                // ======================================================

                when {

                    vm.isLoading.collectAsState().value &&
                            book == null -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator()
                        }
                    }


                    book != null -> {

                        BookReview(

                            book = book!!,


                            // ==================================================
                            // BACK
                            // ==================================================

                            onBack = {

                                navController.popBackStack()
                            },


                            // ==================================================
                            // READ
                            // ==================================================

                            onReadClick = {

                                navController.navigate(
                                    "reader/$bookId/1"
                                )
                            },


                            // ==================================================
                            // LIBRARY
                            // ==================================================

                            onAddToLibraryClick = {

                                vm.toggleLibrary(
                                    currentUserId,
                                    bookId
                                )
                            },


                            // ==================================================
                            // OVERVIEW
                            // ==================================================

                            onOverviewClick = {

                                navController.navigate(
                                    "book_overview/$bookId/$returnRoute"
                                )
                            },


                            // ==================================================
                            // COMMENT
                            // ==================================================

                            onCommentClick = {

                                navController.navigate(
                                    "book_comment/$bookId/$returnRoute"
                                )
                            },


                            // ==================================================
                            // CHAPTER LIST
                            // ==================================================

                            onChapterListClick = {

                                navController.navigate(
                                    "chapter_list/$bookId/1"
                                )
                            },


                            // ==================================================
                            // REVIEW COMMENT
                            // ==================================================

                            onReviewCommentClick = { reviewId ->

                                navController.navigate(
                                    "review_comment/$bookId/$reviewId/$returnRoute"
                                )
                            },


                            // ==================================================
                            // WRITE REVIEW
                            // ==================================================

                            onWriteReviewClick = {

                                navController.navigate(
                                    "write_review/$bookId/$returnRoute"
                                )
                            }
                        )
                    }
                }
            }


            // ==================================================
            // REVIEW COMMENT
            // ==================================================

            composable(
                route = "review_comment/{bookId}/{reviewId}/{returnRoute}",                arguments = listOf(
                    navArgument("bookId") { type = NavType.LongType },
                    navArgument("reviewId") { type = NavType.LongType },
                    navArgument("returnRoute") { type = NavType.StringType }
                )
            ) { entry ->
                val bookId = entry.arguments?.getLong("bookId")
                    ?: return@composable
                val reviewId = entry.arguments?.getLong("reviewId")
                        ?: return@composable
                val returnRoute = entry.arguments?.getString("returnRoute")
                        ?: "home"
                ReviewComment(
                    bookId = bookId,
                    reviewId = reviewId,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // WRITE REVIEW
            // ==================================================

            composable(
                route = "write_review/{bookId}/{returnRoute}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.LongType },
                    navArgument("returnRoute") { type = NavType.StringType }
                )

            ) { entry ->

                val bookId =
                    entry.arguments?.getLong("bookId")
                        ?: return@composable

                val returnRoute =
                    entry.arguments?.getString("returnRoute")
                        ?: "home"

                val reviewViewModel: BookReviewViewModel =
                    viewModel()

                val isCreatingReview by
                reviewViewModel.isCreatingReview.collectAsState()

                val createReviewError by
                reviewViewModel.createReviewError.collectAsState()

                WriteReview(

                    bookId = bookId,

                    onBack = {

                        navController.popBackStack()
                    },

                    onSubmit = {
                            rating,
                            character,
                            plot,
                            world,
                            content ->

                        reviewViewModel.createReview(

                            bookId = bookId,

                            rating = rating.toDouble(),

                            content = content

                        ) { createdReview ->

                            // ------------------------------------------
                            // Báo cho BookReview biết đã tạo review
                            // ------------------------------------------

                            navController
                                .previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(
                                    "review_created",
                                    true
                                )

                            // ------------------------------------------
                            // Quay lại BookReview
                            // ------------------------------------------

                            navController.popBackStack()
                        }
                    }
                )
            }


            // ==================================================
            // COMMENT DETAIL
            // ==================================================

            composable(
                route = "comment_detail/{commentId}",
                arguments = listOf(
                    navArgument("commentId") { type = NavType.LongType }
                )
            ) { entry ->
                val commentId = entry.arguments?.getLong("commentId")
                    ?: return@composable

                var comment by remember {
                    mutableStateOf<CommentResponse?>(null)
                }

                LaunchedEffect(commentId) {
                    try {
                        val response = RetrofitClient.api.getComment(commentId)
                        if (response.isSuccessful) {
                            comment = response.body()
                        }
                    } catch (e: Exception) {
                        Log.e("CommentDetail", "Failed to load comment", e)
                    }
                }

                comment?.let { loadedComment ->
                    CommentDetail(
                        comment = loadedComment,
                        onBack = { navController.popBackStack() }
                    )
                }
            }


            // ==================================================
            // READER
            // ==================================================

            composable(
                route = "reader/{bookId}/{chapterNumber}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.LongType },
                    navArgument("chapterNumber") { type = NavType.IntType }
                )
            ) { entry ->
                val bookId = entry.arguments?.getLong("bookId") ?: return@composable
                val chapterNumber = entry.arguments?.getInt("chapterNumber") ?: 1
                val readerViewModel: ReaderViewModel = viewModel()
                val chapter by readerViewModel.chapter.collectAsState()
                val chapters by readerViewModel.chapters.collectAsState()
                val isLoading by readerViewModel.isLoading.collectAsState()
                val error by readerViewModel.error.collectAsState()
                val userId = SessionManager(LocalContext.current).getUserId()

                LaunchedEffect(bookId, chapterNumber) {
                    readerViewModel.load(bookId, chapterNumber, userId)
                }

                when {
                    isLoading && chapter == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    error != null && chapter == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error ?: "Lỗi", color = Color.White) }
                    chapter != null -> ReaderScreen(
                        chapter = chapter!!,
                        onBack = { navController.popBackStack() },
                        onChapterListClick = { navController.navigate("chapter_list/$bookId/$chapterNumber") },
                        onNightModeClick = { navController.navigate("night_mode") },
                        onBookmarkClick = { },
                        onCommentClick = { navController.navigate("book_comment/$bookId/home") },
                        onBookOverviewClick = { navController.navigate("book_overview/$bookId/reader") },
                        onPreviousChapter = {
                            chapters.firstOrNull { it.chapterNumber == chapter!!.chapterNumber - 1 }?.let { readerViewModel.selectChapter(bookId, it.chapterNumber, userId) }
                        },
                        onNextChapter = {
                            chapters.firstOrNull { it.chapterNumber == chapter!!.chapterNumber + 1 }?.let { readerViewModel.selectChapter(bookId, it.chapterNumber, userId) }
                        },
                        onProgressChanged = { readerViewModel.saveProgress(userId, bookId, it) }
                    )
                }
            }

            // ==================================================
            // CHAPTER LIST SCREEN
            // ==================================================

            composable(
                route = "chapter_list/{bookId}/{returnChapter}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.LongType },
                    navArgument("returnChapter") { type = NavType.IntType }
                )
            ) { entry ->
                val bookId = entry.arguments?.getLong("bookId") ?: return@composable
                val returnChapter = entry.arguments?.getInt("returnChapter") ?: 1
                ChapterListScreen(
                    bookId = bookId,
                    currentChapterNumber = returnChapter,
                    onBack = { navController.popBackStack() },
                    onChapterClick = { number -> navController.navigate("reader/$bookId/$number") { popUpTo("chapter_list/$bookId/$returnChapter") { inclusive = true } } }
                )
            }


            // ==================================================
            // BOOK OVERVIEW - READER
            // ==================================================

            composable(
                route = "book_overview_from_reader/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.LongType })
            ) { entry ->
                val bookId = entry.arguments?.getLong("bookId") ?: return@composable
                val vm: BookOverviewViewModel = viewModel()
                val book by vm.book.collectAsState()
                val currentUserId = SessionManager(context).getUserId()
                LaunchedEffect(bookId) {
                    vm.loadBook(bookId)
                    vm.loadLibraryState(bookId)
                }
                if (book != null) {
                    BookOverview(
                        book = book!!,
                        isInLibrary = vm.isInLibrary.collectAsState().value,
                        onBack = { navController.popBackStack() },
                        onReadClick = { navController.navigate("reader/$bookId/1") },
                        onAddToLibraryClick = {
                            vm.toggleLibrary(currentUserId, bookId)
                        },
                        onReviewClick = { navController.navigate("book_review/$bookId/reader") },
                        onCommentClick = { navController.navigate("book_comment/$bookId/reader") },
                        onChapterListClick = { navController.navigate("chapter_list/$bookId/1") },
                        onGenreClick = { _ -> navController.navigate("list_of_story") },
                        onTagClick = { _ -> navController.navigate("list_of_story") },
                    )
                }
            }


            // ==================================================
            // LOGIN
            // ==================================================

            composable("login") {

                LoginScreen(

                    onBack = {

                        navController.popBackStack()
                    },

                    onLoginClick = { email, password ->

                        coroutineScope.launch {

                            try {

                                println(
                                    "========== LOGIN REQUEST =========="
                                )

                                println(
                                    "EMAIL = $email"
                                )


                                val response =
                                    RetrofitClient.api.login(
                                        email = email,
                                        password = password
                                    )


                                println(
                                    "HTTP CODE = ${response.code()}"
                                )


                                if (response.isSuccessful) {

                                    val loginResponse =
                                        response.body()


                                    if (loginResponse != null) {

                                        val token =
                                            loginResponse.token

                                        val user =
                                            loginResponse.user


                                        println(
                                            "========== LOGIN SUCCESS =========="
                                        )

                                        println(
                                            "TOKEN = $token"
                                        )

                                        println(
                                            "USER ID = ${user.userId}"
                                        )

                                        println(
                                            "USER NAME = ${user.userName}"
                                        )

                                        println(
                                            "EMAIL = ${user.email}"
                                        )

                                        println(
                                            "==================================="
                                        )


                                        sessionManager.saveLogin(
                                            loginResponse
                                        )


                                        println(
                                            "========== SESSION SAVED =========="
                                        )

                                        println(
                                            "TOKEN SAVED = ${
                                                sessionManager.getToken()
                                            }"
                                        )

                                        println(
                                            "USER ID SAVED = ${
                                                sessionManager.getUserId()
                                            }"
                                        )

                                        println(
                                            "USER NAME SAVED = ${
                                                sessionManager.getUserName()
                                            }"
                                        )

                                        println(
                                            "==================================="
                                        )


                                        isLoggedIn = true


                                        navController.navigate(
                                            "account"
                                        ) {

                                            popUpTo("login") {

                                                inclusive = true
                                            }

                                            launchSingleTop = true
                                        }
                                    }

                                } else {

                                    println(
                                        "========== LOGIN FAILED =========="
                                    )

                                    println(
                                        "HTTP CODE = ${
                                            response.code()
                                        }"
                                    )

                                    println(
                                        "ERROR = ${
                                            response
                                                .errorBody()
                                                ?.string()
                                        }"
                                    )

                                    println(
                                        "=================================="
                                    )
                                }

                            } catch (e: Exception) {

                                println(
                                    "========== LOGIN ERROR =========="
                                )

                                println(
                                    "ERROR = ${e.message}"
                                )

                                e.printStackTrace()

                                println(
                                    "================================="
                                )
                            }
                        }
                    },

                    onRegisterClick = {

                        navController.navigate(
                            "register"
                        )
                    },

                    onForgotPasswordClick = {

                        navController.navigate(
                            "forgot_password"
                        )
                    },

                    onGoogleLoginClick = {

                        Log.d(
                            "LOGIN",
                            "Google Login chưa được kết nối"
                        )
                    }
                )
            }


            // ==================================================
            // REGISTER
            // ==================================================

            composable("register") {

                RegisterScreen(

                    onBack = {

                        navController.popBackStack()
                    }
                )
            }


            // ==================================================
            // FORGOT PASSWORD
            // ==================================================

            composable("forgot_password") {

                ForgotPasswordScreen(

                    onBack = {

                        navController.popBackStack()
                    },

                    onSendCodeClick = { email ->

                        navController.navigate(
                            "reset_password/$email"
                        )
                    }
                )
            }


            // ==================================================
            // RESET PASSWORD
            // ==================================================

            composable(
                route = "reset_password/{email}"
            ) { backStackEntry ->

                val email =
                    backStackEntry.arguments
                        ?.getString("email")
                        ?: ""


                ResetPasswordScreen(

                    email = email,

                    onBack = {

                        navController.popBackStack()
                    },

                    onResetSuccess = {

                        navController.popBackStack(
                            "login",
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}