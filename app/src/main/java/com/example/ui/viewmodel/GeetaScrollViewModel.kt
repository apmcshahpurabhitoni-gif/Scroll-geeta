package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.local.GitaDatabase
import com.example.data.remote.VedicScripturesApi
import com.example.data.repository.GitaScriptureRepository
import com.example.data.repository.GitaSearchResult
import com.example.data.repository.UserStudyRepository
import com.example.model.ChapterHierarchy
import com.example.model.ChapterSummary
import com.example.model.DailyVerseInfo
import com.example.model.ReadVerseRecord
import com.example.model.ReadingProgress
import com.example.model.Shloka
import com.example.model.VerseBookmark
import com.example.model.VerseContextBundle
import com.example.model.VerseReflection
import com.example.model.verseKey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface ShlokaCardState {
    data object Loading : ShlokaCardState
    data class Ready(val shloka: Shloka) : ShlokaCardState
    data class Error(val message: String) : ShlokaCardState
}

class GeetaScrollViewModel(
    private val scriptureRepository: GitaScriptureRepository,
    private val userStudyRepository: UserStudyRepository,
    private val currentUserId: String
) : ViewModel() {

    val chapters: List<ChapterSummary> = scriptureRepository.chapters
    val totalVerses: Int = scriptureRepository.totalVerses
    val dailyVerse: DailyVerseInfo = scriptureRepository.getDailyVerse()

    val chapterHierarchy: StateFlow<List<ChapterHierarchy>> =
        scriptureRepository.observeChapterHierarchy()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = chapters.map { ChapterHierarchy(it, emptyMap()) }
            )

    private val _shlokaStates = MutableStateFlow<Map<String, ShlokaCardState>>(emptyMap())
    val shlokaStates: StateFlow<Map<String, ShlokaCardState>> = _shlokaStates.asStateFlow()

    private val _currentPageIndex = MutableStateFlow(0)
    val currentPageIndex: StateFlow<Int> = _currentPageIndex.asStateFlow()

    private val _jumpToPageEvent = MutableSharedFlow<Int>(extraBufferCapacity = 4)
    val jumpToPageEvent: SharedFlow<Int> = _jumpToPageEvent.asSharedFlow()

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val _explainerContextBundle = MutableStateFlow<VerseContextBundle?>(null)
    val explainerContextBundle: StateFlow<VerseContextBundle?> = _explainerContextBundle.asStateFlow()

    private val _isExplainerContextLoading = MutableStateFlow(false)
    val isExplainerContextLoading: StateFlow<Boolean> = _isExplainerContextLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow(GitaSearchResult())
    val searchResults: StateFlow<GitaSearchResult> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _selectedBrowseChapter = MutableStateFlow<Int>(1)
    val selectedBrowseChapter: StateFlow<Int> = _selectedBrowseChapter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val browseChapterShlokas: StateFlow<Map<Int, Shloka>> = _selectedBrowseChapter
        .flatMapLatest { chapterNum ->
            scriptureRepository.observeChapterShlokas(chapterNum)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = emptyMap()
        )

    private var contextBundleJob: Job? = null
    private var searchJob: Job? = null
    private var prefetchJob: Job? = null
    private var initialPositionApplied = false

    val bookmarksState: StateFlow<UiState<List<VerseBookmark>>> =
        userStudyRepository.observeBookmarks(currentUserId)
            .map<List<VerseBookmark>, UiState<List<VerseBookmark>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing bookmarks", root)
                emit(UiState.Error(root.message ?: "Failed to load bookmarks"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )

    val reflectionsState: StateFlow<UiState<List<VerseReflection>>> =
        userStudyRepository.observeReflections(currentUserId)
            .map<List<VerseReflection>, UiState<List<VerseReflection>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing reflections", root)
                emit(UiState.Error(root.message ?: "Failed to load reflections"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )

    val progressState: StateFlow<UiState<ReadingProgress?>> =
        userStudyRepository.observeProgress(currentUserId)
            .map<ReadingProgress?, UiState<ReadingProgress?>> { progress ->
                if (!initialPositionApplied && progress != null) {
                    initialPositionApplied = true
                    val target = scriptureRepository.chapterVerseToIndex(
                        progress.lastChapter.toInt(),
                        progress.lastVerse.toInt()
                    )
                    if (target != _currentPageIndex.value) {
                        _currentPageIndex.value = target
                        _jumpToPageEvent.tryEmit(target)
                    }
                }
                UiState.Success(progress)
            }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing reading progress", root)
                emit(UiState.Error(root.message ?: "Failed to load reading progress"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )

    val readVersesState: StateFlow<UiState<List<ReadVerseRecord>>> =
        userStudyRepository.observeReadVerses(currentUserId)
            .map<List<ReadVerseRecord>, UiState<List<ReadVerseRecord>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing read verses", root)
                emit(UiState.Error(root.message ?: "Failed to load completed shlokas"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )

    init {
        viewModelScope.launch {
            scriptureRepository.ensureSeedCached()
            loadPageAndNeighbors(0)
        }
    }

    fun chapterVerseForIndex(pageIndex: Int): Pair<Int, Int> =
        scriptureRepository.indexToChapterVerse(pageIndex)

    fun chapterForNumber(chapter: Int): ChapterSummary =
        scriptureRepository.getChapter(chapter)

    fun onPageSettled(pageIndex: Int) {
        _currentPageIndex.value = pageIndex
        loadPageAndNeighbors(pageIndex)
        val (chapter, verse) = scriptureRepository.indexToChapterVerse(pageIndex)
        viewModelScope.launch {
            userStudyRepository.recordVerseVisited(currentUserId, chapter, verse)
        }
    }

    fun jumpToVerse(chapter: Int, verse: Int) {
        val index = scriptureRepository.chapterVerseToIndex(chapter, verse)
        _currentPageIndex.value = index
        loadPageAndNeighbors(index)
        _jumpToPageEvent.tryEmit(index)
    }

    fun retryLoadVerse(chapter: Int, verse: Int) {
        val key = verseKey(chapter, verse)
        _shlokaStates.update { it + (key to ShlokaCardState.Loading) }
        loadSingleVerse(chapter, verse, forceRefresh = true)
    }

    private fun loadPageAndNeighbors(pageIndex: Int) {
        for (offset in listOf(0, 1, -1)) {
            val targetIndex = pageIndex + offset
            if (targetIndex in 0 until totalVerses) {
                val (ch, vs) = scriptureRepository.indexToChapterVerse(targetIndex)
                loadSingleVerse(ch, vs, forceRefresh = false)
            }
        }
    }

    private fun loadSingleVerse(chapter: Int, verse: Int, forceRefresh: Boolean) {
        val key = verseKey(chapter, verse)
        val existing = _shlokaStates.value[key]
        if (!forceRefresh && (existing is ShlokaCardState.Ready || existing is ShlokaCardState.Loading)) {
            return
        }
        _shlokaStates.update { it + (key to ShlokaCardState.Loading) }
        viewModelScope.launch {
            val result = scriptureRepository.getShloka(chapter, verse)
            result.fold(
                onSuccess = { shloka ->
                    _shlokaStates.update { it + (key to ShlokaCardState.Ready(shloka)) }
                },
                onFailure = { err ->
                    _shlokaStates.update {
                        it + (key to ShlokaCardState.Error(
                            err.localizedMessage ?: "Unable to fetch BG $chapter.$verse. Check connection."
                        ))
                    }
                }
            )
        }
    }

    fun toggleBookmark(shloka: Shloka, currentlyBookmarked: Boolean) {
        val chapterTitle = scriptureRepository.getChapter(shloka.chapter).englishTitle
        viewModelScope.launch {
            val res = userStudyRepository.toggleBookmark(
                userId = currentUserId,
                shloka = shloka,
                chapterTitle = chapterTitle,
                currentlyBookmarked = currentlyBookmarked
            )
            res.fold(
                onSuccess = {
                    _snackbarMessage.tryEmit(
                        if (currentlyBookmarked) "Removed BG ${shloka.chapter}.${shloka.verse} from bookmarks"
                        else "Saved BG ${shloka.chapter}.${shloka.verse} to bookmarks"
                    )
                },
                onFailure = { err ->
                    _snackbarMessage.tryEmit(err.localizedMessage ?: "Could not update bookmark")
                }
            )
        }
    }

    fun markAsRead(shloka: Shloka) {
        viewModelScope.launch {
            val res = userStudyRepository.markVerseRead(
                userId = currentUserId,
                chapter = shloka.chapter,
                verse = shloka.verse
            )
            res.fold(
                onSuccess = {
                    _snackbarMessage.tryEmit("BG ${shloka.chapter}.${shloka.verse} marked read")
                },
                onFailure = { err ->
                    _snackbarMessage.tryEmit(err.localizedMessage ?: "Could not update reading streak")
                }
            )
        }
    }

    fun saveReflection(
        shloka: Shloka,
        noteText: String,
        moodTag: String,
        existingReflectionId: String? = null
    ) {
        viewModelScope.launch {
            val res = userStudyRepository.saveReflection(
                userId = currentUserId,
                shloka = shloka,
                noteText = noteText,
                moodTag = moodTag,
                existingReflectionId = existingReflectionId
            )
            res.fold(
                onSuccess = {
                    _snackbarMessage.tryEmit(
                        if (existingReflectionId != null) "Reflection updated"
                        else "Reflection saved for BG ${shloka.chapter}.${shloka.verse}"
                    )
                },
                onFailure = { err ->
                    _snackbarMessage.tryEmit(err.localizedMessage ?: "Failed to save reflection")
                }
            )
        }
    }

    fun deleteReflection(reflectionId: String) {
        viewModelScope.launch {
            val res = userStudyRepository.deleteReflection(currentUserId, reflectionId)
            res.fold(
                onSuccess = { _snackbarMessage.tryEmit("Reflection deleted") },
                onFailure = { err ->
                    _snackbarMessage.tryEmit(err.localizedMessage ?: "Failed to delete reflection")
                }
            )
        }
    }

    fun prepareExplainerContext(shloka: Shloka) {
        _explainerContextBundle.value = VerseContextBundle(target = shloka)
        _isExplainerContextLoading.value = true
        contextBundleJob?.cancel()
        contextBundleJob = viewModelScope.launch {
            val bundle = scriptureRepository.getVerseContextBundle(shloka.chapter, shloka.verse)
            _explainerContextBundle.value = bundle
            _isExplainerContextLoading.value = false
        }
    }

    fun clearExplainerContext() {
        contextBundleJob?.cancel()
        _explainerContextBundle.value = null
        _isExplainerContextLoading.value = false
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _isSearching.value = false
            _searchResults.value = GitaSearchResult()
            return
        }
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(180)
            val result = scriptureRepository.searchGita(query)
            _searchResults.value = result
            _isSearching.value = false
        }
    }

    fun selectBrowseChapter(chapterNumber: Int) {
        val clamped = chapterNumber.coerceIn(1, 18)
        _selectedBrowseChapter.value = clamped
        prefetchJob?.cancel()
        prefetchJob = viewModelScope.launch {
            scriptureRepository.prefetchTopRankedForChapter(clamped)
            scriptureRepository.prefetchChapterRange(clamped, 1, 12)
        }
    }

    fun prefetchMoreChapterVerses(chapterNumber: Int, startVerse: Int, endVerse: Int) {
        viewModelScope.launch {
            scriptureRepository.prefetchChapterRange(chapterNumber, startVerse, endVerse)
        }
    }

    companion object {
        private const val TAG = "GeetaScrollVM"
        private const val STOP_TIMEOUT_MS = 5000L

        fun factory(currentUserId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(databaseId)
                val roomDb = GitaDatabase.getInstance(app)
                val api = VedicScripturesApi.create()
                GeetaScrollViewModel(
                    scriptureRepository = GitaScriptureRepository(
                        chapterDao = roomDb.chapterDao(),
                        shlokaDao = roomDb.shlokaDao(),
                        api = api
                    ),
                    userStudyRepository = UserStudyRepository(firestore),
                    currentUserId = currentUserId
                )
            }
        }
    }
}
