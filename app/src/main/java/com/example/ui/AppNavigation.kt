package com.example.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firebase.attemptAutoSignIn
import com.example.firebase.authStateFlow
import com.example.firebase.signOutUser
import com.example.model.KrishnaScene
import com.example.model.Shloka
import com.example.model.verseKey
import com.example.ui.components.AdMobBannerBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChaptersBrowserScreen
import com.example.ui.screens.JourneyScreen
import com.example.ui.screens.SavedReflectionsScreen
import com.example.ui.screens.ScrollFeedScreen
import com.example.ui.viewmodel.GeetaScrollViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val ROUTE_SCROLL = "scroll"
private const val ROUTE_CHAPTERS = "chapters"
private const val ROUTE_JOURNEY = "journey"
private const val ROUTE_SAVED = "saved"

private data class NavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val NAV_TABS = listOf(
    NavTab(ROUTE_SCROLL, "Scroll", Icons.Rounded.AutoStories, Icons.Outlined.AutoStories, "nav_scroll"),
    NavTab(ROUTE_CHAPTERS, "Chapters", Icons.Rounded.MenuBook, Icons.Outlined.MenuBook, "nav_chapters"),
    NavTab(ROUTE_JOURNEY, "Journey", Icons.Rounded.Insights, Icons.Outlined.Insights, "nav_journey"),
    NavTab(ROUTE_SAVED, "Saved", Icons.Rounded.Bookmarks, Icons.Outlined.Bookmarks, "nav_saved")
)

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            AuthenticatedGeetaApp(
                currentUserId = user.uid,
                userEmail = user.email,
                onSignOut = {
                    signOutUser(credentialManager = credentialManager, scope = scope)
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedGeetaApp(
    currentUserId: String,
    userEmail: String?,
    onSignOut: () -> Unit
) {
    val viewModel: GeetaScrollViewModel = viewModel(
        key = currentUserId,
        factory = GeetaScrollViewModel.factory(currentUserId)
    )
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val shlokaStates by viewModel.shlokaStates.collectAsStateWithLifecycle()
    val currentPageIndex by viewModel.currentPageIndex.collectAsStateWithLifecycle()
    val bookmarksState by viewModel.bookmarksState.collectAsStateWithLifecycle()
    val reflectionsState by viewModel.reflectionsState.collectAsStateWithLifecycle()
    val progressState by viewModel.progressState.collectAsStateWithLifecycle()
    val readVersesState by viewModel.readVersesState.collectAsStateWithLifecycle()
    val chapterHierarchy by viewModel.chapterHierarchy.collectAsStateWithLifecycle()
    val explainerContextBundle by viewModel.explainerContextBundle.collectAsStateWithLifecycle()
    val isExplainerContextLoading by viewModel.isExplainerContextLoading.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val selectedBrowseChapter by viewModel.selectedBrowseChapter.collectAsStateWithLifecycle()

    val bookmarkedIds = remember(bookmarksState) {
        (bookmarksState as? UiState.Success)?.data?.map { it.verseId }?.toSet().orEmpty()
    }
    val readVersesList = remember(readVersesState) {
        (readVersesState as? UiState.Success)?.data.orEmpty()
    }
    val readVerseIds = remember(readVersesList) {
        readVersesList.map { it.verseId }.toSet()
    }
    val progress = remember(progressState) {
        (progressState as? UiState.Success)?.data
    }

    LaunchedEffect(viewModel.snackbarMessage) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ROUTE_SCROLL

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(ROUTE_SCROLL) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!useRail) {
                    Column(modifier = Modifier.navigationBarsPadding()) {
                        AdMobBannerBar()
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NAV_TABS.forEach { tab ->
                                val selected = currentRoute == tab.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(tab.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = { Text(tab.label) },
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                        NAV_TABS.forEach { tab ->
                            val selected = currentRoute == tab.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_SCROLL,
                        modifier = Modifier.weight(1f)
                    ) {
                        composable(ROUTE_SCROLL) {
                            ScrollFeedScreen(
                                totalVerses = viewModel.totalVerses,
                                chapters = viewModel.chapters,
                                dailyVerse = viewModel.dailyVerse,
                                currentPageIndex = currentPageIndex,
                                jumpToPageEvent = viewModel.jumpToPageEvent,
                                shlokaStates = shlokaStates,
                                bookmarkedIds = bookmarkedIds,
                                readVerseIds = readVerseIds,
                                progress = progress,
                                explainerContextBundle = explainerContextBundle,
                                isExplainerContextLoading = isExplainerContextLoading,
                                chapterVerseForIndex = viewModel::chapterVerseForIndex,
                                chapterForNumber = viewModel::chapterForNumber,
                                onPageSettled = viewModel::onPageSettled,
                                onJumpToVerse = viewModel::jumpToVerse,
                                onRetryLoadVerse = viewModel::retryLoadVerse,
                                onToggleBookmark = viewModel::toggleBookmark,
                                onMarkAsRead = viewModel::markAsRead,
                                onSaveReflection = { shloka, note, mood ->
                                    viewModel.saveReflection(shloka, note, mood)
                                },
                                onPrepareExplainer = viewModel::prepareExplainerContext,
                                onClearExplainer = viewModel::clearExplainerContext
                            )
                        }

                    composable(ROUTE_CHAPTERS) {
                        ChaptersBrowserScreen(
                            chapterHierarchy = chapterHierarchy,
                            searchQuery = searchQuery,
                            searchResults = searchResults,
                            isSearching = isSearching,
                            selectedBrowseChapter = selectedBrowseChapter,
                            bookmarkedIds = bookmarkedIds,
                            readVerses = readVersesList,
                            onSearchQueryChange = viewModel::updateSearchQuery,
                            onSelectChapter = viewModel::selectBrowseChapter,
                            onPrefetchChapterRange = viewModel::prefetchMoreChapterVerses,
                            onOpenVerse = { ch, vs ->
                                viewModel.jumpToVerse(ch, vs)
                                navigateToTab(ROUTE_SCROLL)
                            }
                        )
                    }

                    composable(ROUTE_JOURNEY) {
                        JourneyScreen(
                            userEmail = userEmail,
                            dailyVerse = viewModel.dailyVerse,
                            chapters = viewModel.chapters,
                            totalVerses = viewModel.totalVerses,
                            progress = progress,
                            readVerses = readVersesList,
                            onOpenVerse = { ch, vs ->
                                viewModel.jumpToVerse(ch, vs)
                                navigateToTab(ROUTE_SCROLL)
                            },
                            onSignOut = onSignOut
                        )
                    }

                    composable(ROUTE_SAVED) {
                        SavedReflectionsScreen(
                            bookmarksState = bookmarksState,
                            reflectionsState = reflectionsState,
                            onOpenVerse = { ch, vs ->
                                viewModel.jumpToVerse(ch, vs)
                                navigateToTab(ROUTE_SCROLL)
                            },
                            onRemoveBookmark = { bm ->
                                val stubShloka = Shloka(
                                    id = verseKey(bm.chapter.toInt(), bm.verse.toInt()),
                                    chapter = bm.chapter.toInt(),
                                    verse = bm.verse.toInt(),
                                    speakerContext = "",
                                    sanskrit = bm.sanskritPreview,
                                    transliteration = "",
                                    hindi = bm.hindiPreview,
                                    english = bm.englishPreview,
                                    explainers = emptyList(),
                                    scene = KrishnaScene.KURUKSHETRA
                                )
                                viewModel.toggleBookmark(stubShloka, currentlyBookmarked = true)
                            },
                            onUpdateReflection = { reflection, text, mood ->
                                val stubShloka = Shloka(
                                    id = reflection.verseId,
                                    chapter = reflection.chapter.toInt(),
                                    verse = reflection.verse.toInt(),
                                    speakerContext = "",
                                    sanskrit = "",
                                    transliteration = "",
                                    hindi = "",
                                    english = "",
                                    explainers = emptyList(),
                                    scene = KrishnaScene.KURUKSHETRA
                                )
                                viewModel.saveReflection(
                                    shloka = stubShloka,
                                    noteText = text,
                                    moodTag = mood,
                                    existingReflectionId = reflection.id
                                )
                            },
                            onDeleteReflection = viewModel::deleteReflection
                        )
                    }
                    }
                    if (useRail) {
                        AdMobBannerBar(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}
