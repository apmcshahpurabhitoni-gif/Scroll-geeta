package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.GitaSearchResult
import com.example.data.seed.GitaCatalogSeed
import com.example.model.ChapterHierarchy
import com.example.model.ChapterSummary
import com.example.model.ReadVerseRecord
import com.example.model.Shloka
import com.example.model.VerseImportanceTier
import com.example.model.verseKey
import com.example.ui.theme.ForestSage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private enum class VerseFilterMode(val label: String) {
    SEQUENTIAL("All (1..N)"),
    TOP_RANKED("Top Ranked ★"),
    ESSENTIAL_ONLY("Essential Only")
}

@Composable
fun ChaptersBrowserScreen(
    chapterHierarchy: List<ChapterHierarchy>,
    searchQuery: String,
    searchResults: GitaSearchResult,
    isSearching: Boolean,
    selectedBrowseChapter: Int,
    bookmarkedIds: Set<String>,
    readVerses: List<ReadVerseRecord>,
    onSearchQueryChange: (String) -> Unit,
    onSelectChapter: (Int) -> Unit,
    onPrefetchChapterRange: (chapter: Int, start: Int, end: Int) -> Unit,
    onOpenVerse: (chapter: Int, verse: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val readIds = remember(readVerses) { readVerses.map { it.verseId }.toSet() }
    val readCountByChapter = remember(readVerses) {
        readVerses.groupingBy { it.chapter.toInt() }.eachCount()
    }
    val safeHierarchy = remember(chapterHierarchy) {
        if (chapterHierarchy.isNotEmpty()) chapterHierarchy
        else GitaCatalogSeed.chapters.map { ChapterHierarchy(it, emptyMap()) }
    }

    val scope = rememberCoroutineScope()
    val chapterPagerState = rememberPagerState(
        initialPage = (selectedBrowseChapter - 1).coerceIn(0, (safeHierarchy.size - 1).coerceAtLeast(0)),
        pageCount = { safeHierarchy.size }
    )
    val tabRowState = rememberLazyListState()

    LaunchedEffect(chapterPagerState) {
        snapshotFlow { chapterPagerState.settledPage }
            .distinctUntilChanged()
            .collect { settledIndex ->
                val chNum = safeHierarchy.getOrNull(settledIndex)?.chapter?.chapterNumber ?: (settledIndex + 1)
                onSelectChapter(chNum)
                tabRowState.animateScrollToItem(settledIndex.coerceAtLeast(0))
            }
    }

    LaunchedEffect(selectedBrowseChapter) {
        val targetIndex = (selectedBrowseChapter - 1).coerceIn(0, (safeHierarchy.size - 1).coerceAtLeast(0))
        if (chapterPagerState.currentPage != targetIndex) {
            chapterPagerState.animateScrollToPage(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Bhagavad Gita • 18 Chapters",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Swipe chapters, filter by verse rank ★, or search across all 701 shlokas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = "Search Bhagavad Gita")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                        }
                    }
                },
                placeholder = { Text("Search 'karma', '2.47', 'Chapter 11'...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gita_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (searchQuery.isNotBlank()) {
                SearchResultsContent(
                    searchResults = searchResults,
                    isSearching = isSearching,
                    bookmarkedIds = bookmarkedIds,
                    readIds = readIds,
                    onOpenVerse = onOpenVerse,
                    onBrowseChapter = { ch ->
                        onSearchQueryChange("")
                        onSelectChapter(ch)
                        scope.launch {
                            chapterPagerState.animateScrollToPage((ch - 1).coerceIn(0, 17))
                        }
                    },
                    onQuickSuggestion = onSearchQueryChange
                )
            } else {
                // 18-Chapter Tab Strip synced with HorizontalPager
                LazyRow(
                    state = tabRowState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(safeHierarchy.size) { index ->
                        val ch = safeHierarchy[index].chapter
                        val isSelected = chapterPagerState.currentPage == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .clickable {
                                    scope.launch { chapterPagerState.animateScrollToPage(index) }
                                }
                                .testTag("chapter_pager_tab_${ch.chapterNumber}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Ch. ${ch.chapterNumber}",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${ch.versesCount}v",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) Color(0xFFFFC168) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ViewPager-based 18-Chapter Hierarchy
                HorizontalPager(
                    state = chapterPagerState,
                    beyondViewportPageCount = 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("chapters_horizontal_pager")
                ) { pageIndex ->
                    val item = safeHierarchy[pageIndex]
                    val readCount = readCountByChapter[item.chapter.chapterNumber] ?: 0
                    ChapterHierarchyPage(
                        chapter = item.chapter,
                        cachedShlokas = item.cachedShlokas,
                        readCount = readCount,
                        bookmarkedIds = bookmarkedIds,
                        readIds = readIds,
                        onPrefetchRange = { start, end ->
                            onPrefetchChapterRange(item.chapter.chapterNumber, start, end)
                        },
                        onOpenVerse = { verse ->
                            onOpenVerse(item.chapter.chapterNumber, verse)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChapterHierarchyPage(
    chapter: ChapterSummary,
    cachedShlokas: Map<Int, Shloka>,
    readCount: Int,
    bookmarkedIds: Set<String>,
    readIds: Set<String>,
    onPrefetchRange: (start: Int, end: Int) -> Unit,
    onOpenVerse: (verse: Int) -> Unit
) {
    var filterMode by remember(chapter.chapterNumber) { mutableStateOf(VerseFilterMode.SEQUENTIAL) }
    var verseFilterText by remember(chapter.chapterNumber) { mutableStateOf("") }

    val orderedVerses = remember(chapter, filterMode, verseFilterText, cachedShlokas) {
        val base = (1..chapter.versesCount).toList()
        val modeFiltered = when (filterMode) {
            VerseFilterMode.SEQUENTIAL -> base
            VerseFilterMode.TOP_RANKED -> base.sortedByDescending { v ->
                cachedShlokas[v]?.importanceScore ?: GitaCatalogSeed.verseRankingFor(chapter.chapterNumber, v).score
            }
            VerseFilterMode.ESSENTIAL_ONLY -> base.filter { v ->
                val tier = cachedShlokas[v]?.importanceTier
                    ?: GitaCatalogSeed.verseRankingFor(chapter.chapterNumber, v).tier
                tier == VerseImportanceTier.CORE_ESSENCE || tier == VerseImportanceTier.KEY_INSIGHT
            }.sortedByDescending { v ->
                cachedShlokas[v]?.importanceScore ?: GitaCatalogSeed.verseRankingFor(chapter.chapterNumber, v).score
            }
        }

        val q = verseFilterText.trim().lowercase()
        if (q.isEmpty()) {
            modeFiltered
        } else {
            val numQuery = q.toIntOrNull()
            modeFiltered.filter { v ->
                if (numQuery != null && (v == numQuery || "$v".startsWith(q))) {
                    true
                } else {
                    val cached = cachedShlokas[v]
                    cached != null && (
                        cached.sanskrit.lowercase().contains(q) ||
                            cached.english.lowercase().contains(q) ||
                            cached.hindi.lowercase().contains(q) ||
                            cached.contextSummary.lowercase().contains(q)
                        )
                }
            }
        }
    }

    val progress = (readCount.toFloat() / chapter.versesCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    LazyColumn(
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "hero_${chapter.chapterNumber}") {
            Surface(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0x55FFD180)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(208.dp)
                ) {
                    Image(
                        painter = painterResource(id = chapter.scene.drawableRes),
                        contentDescription = chapter.scene.sceneTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to Color(0x520B1022),
                                    0.45f to Color(0x9E0D1326),
                                    1.0f to Color(0xEB0E1220)
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CHAPTER ${chapter.chapterNumber} OF 18 • ${chapter.versesCount} SHLOKAS",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFD180)
                            )
                            Button(
                                onClick = { onOpenVerse(1) },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("scroll_chapter_btn_${chapter.chapterNumber}")
                            ) {
                                Text("Scroll Ch. ${chapter.chapterNumber}", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Column {
                            Text(
                                text = "${chapter.sanskritName} — ${chapter.englishTitle}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = chapter.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.90f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp),
                                    color = Color(0xFFFFC168),
                                    trackColor = Color.White.copy(alpha = 0.25f)
                                )
                                Text(
                                    text = "$readCount/${chapter.versesCount} read",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "controls_${chapter.chapterNumber}") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(VerseFilterMode.entries) { mode ->
                        FilterChip(
                            selected = filterMode == mode,
                            onClick = { filterMode = mode },
                            label = {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.testTag("rank_filter_${mode.name}")
                        )
                    }
                }

                OutlinedTextField(
                    value = verseFilterText,
                    onValueChange = { verseFilterText = it },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (verseFilterText.isNotEmpty()) {
                            IconButton(onClick = { verseFilterText = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear chapter filter")
                            }
                        }
                    },
                    placeholder = { Text("Filter Ch. ${chapter.chapterNumber} by verse # or keyword...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_verse_filter_input")
                )
            }
        }

        items(orderedVerses, key = { "ch${chapter.chapterNumber}_v$it" }) { verseNum ->
            if (verseNum % 12 == 1 && cachedShlokas[verseNum] == null) {
                LaunchedEffect(chapter.chapterNumber, verseNum) {
                    onPrefetchRange(verseNum, (verseNum + 11).coerceAtMost(chapter.versesCount))
                }
            }
            val shloka = cachedShlokas[verseNum]
            val vKey = verseKey(chapter.chapterNumber, verseNum)
            val isBookmarked = vKey in bookmarkedIds
            val isRead = vKey in readIds

            if (shloka != null) {
                RankedShlokaCard(
                    shloka = shloka,
                    isBookmarked = isBookmarked,
                    isRead = isRead,
                    onClick = { onOpenVerse(verseNum) }
                )
            } else {
                val ranking = GitaCatalogSeed.verseRankingFor(chapter.chapterNumber, verseNum)
                UncachedRankedVerseRow(
                    chapter = chapter.chapterNumber,
                    verse = verseNum,
                    score = ranking.score,
                    tier = ranking.tier,
                    requiresContext = ranking.requiresAdjacentContext,
                    contextHint = ranking.contextSummary,
                    isBookmarked = isBookmarked,
                    isRead = isRead,
                    onClick = { onOpenVerse(verseNum) }
                )
            }
        }
    }
}

@Composable
private fun RankedShlokaCard(
    shloka: Shloka,
    isBookmarked: Boolean,
    isRead: Boolean,
    onClick: () -> Unit
) {
    val goldAccent = Color(0xFFFFD180)
    val ivoryText = Color(0xFFFAF3E6)
    val mutedIvory = Color(0xFFD2C7B4)
    val tierColor = when (shloka.importanceTier) {
        VerseImportanceTier.CORE_ESSENCE -> goldAccent
        VerseImportanceTier.KEY_INSIGHT -> Color(0xFFA8CBFF)
        VerseImportanceTier.SUPPORTING_CONTEXT -> Color(0xFFA2E6B8)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF111629),
        border = BorderStroke(
            width = if (shloka.importanceTier == VerseImportanceTier.CORE_ESSENCE) 1.3.dp else 1.dp,
            color = if (shloka.importanceTier == VerseImportanceTier.CORE_ESSENCE) {
                goldAccent.copy(alpha = 0.45f)
            } else {
                Color.White.copy(alpha = 0.16f)
            }
        ),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("shloka_item_${shloka.id}")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = shloka.scene.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color(0xC80D1326),
                            0.5f to Color(0xDF101528),
                            1.0f to Color(0xF00E111E)
                        )
                    )
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = goldAccent,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "BG ${shloka.chapter}.${shloka.verse}",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1A1205),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            color = tierColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, tierColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "${shloka.importanceTier.shortBadge} (${shloka.importanceScore})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = tierColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isRead) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Read",
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (isBookmarked) {
                            Icon(
                                imageVector = Icons.Rounded.Bookmark,
                                contentDescription = "Bookmarked",
                                tint = goldAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = shloka.sanskrit,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFFFFE5AE),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = shloka.english,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = ivoryText,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (shloka.requiresAdjacentContext) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Link,
                            contentDescription = null,
                            tint = mutedIvory,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = shloka.contextSummary.ifBlank { "Builds on prev/next verses" },
                            style = MaterialTheme.typography.labelSmall,
                            color = mutedIvory,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UncachedRankedVerseRow(
    chapter: Int,
    verse: Int,
    score: Int,
    tier: VerseImportanceTier,
    requiresContext: Boolean,
    contextHint: String,
    isBookmarked: Boolean,
    isRead: Boolean,
    onClick: () -> Unit
) {
    val tierColor = when (tier) {
        VerseImportanceTier.CORE_ESSENCE -> MaterialTheme.colorScheme.primary
        VerseImportanceTier.KEY_INSIGHT -> MaterialTheme.colorScheme.secondary
        VerseImportanceTier.SUPPORTING_CONTEXT -> MaterialTheme.colorScheme.tertiary
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("verse_row_${chapter}_$verse")
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$chapter.$verse",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = tierColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${tier.shortBadge} ($score)",
                            style = MaterialTheme.typography.labelSmall,
                            color = tierColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isRead) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Read",
                            tint = ForestSage,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (isBookmarked) {
                        Icon(
                            imageVector = Icons.Rounded.Bookmark,
                            contentDescription = "Bookmarked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (requiresContext) contextHint else "Tap to read BG $chapter.$verse",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchResults: GitaSearchResult,
    isSearching: Boolean,
    bookmarkedIds: Set<String>,
    readIds: Set<String>,
    onOpenVerse: (Int, Int) -> Unit,
    onBrowseChapter: (Int) -> Unit,
    onQuickSuggestion: (String) -> Unit
) {
    if (isSearching && searchResults.directVerseTarget == null &&
        searchResults.matchingChapters.isEmpty() && searchResults.matchingShlokas.isEmpty()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val hasAnyResult = searchResults.directVerseTarget != null ||
        searchResults.matchingChapters.isNotEmpty() ||
        searchResults.matchingShlokas.isNotEmpty()

    if (!hasAnyResult) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No matching chapters or cached shlokas for \"${searchResults.query}\"",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Try a verse reference like 2.47, a chapter like Chapter 12, or keywords such as karma, peace, or devotion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("2.47", "Chapter 11", "karma", "peace", "devotion")) { sample ->
                        OutlinedButton(onClick = { onQuickSuggestion(sample) }) {
                            Text(sample, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        searchResults.directVerseTarget?.let { (ch, vs) ->
            item(key = "direct_${ch}_$vs") {
                val preview = searchResults.directVersePreview
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVerse(ch, vs) }
                        .testTag("direct_verse_result_${ch}_$vs")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DIRECT VERSE MATCH • BG $ch.$vs",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFFC168)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (preview != null) {
                            Text(
                                text = preview.sanskrit,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preview.english,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.88f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = "Open Chapter $ch, Shloka $vs directly in the Scroll reader",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (searchResults.matchingChapters.isNotEmpty()) {
            item(key = "header_chapters") {
                Text(
                    text = "Chapters (${searchResults.matchingChapters.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(searchResults.matchingChapters, key = { "ch_${it.chapterNumber}" }) { ch ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBrowseChapter(ch.chapterNumber) }
                        .testTag("search_chapter_${ch.chapterNumber}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${ch.chapterNumber}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chapter ${ch.chapterNumber}: ${ch.englishTitle}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${ch.sanskritName} • ${ch.versesCount} Shlokas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Open chapter ${ch.chapterNumber}"
                        )
                    }
                }
            }
        }

        if (searchResults.matchingShlokas.isNotEmpty()) {
            item(key = "header_shlokas") {
                Text(
                    text = "Matching Shlokas (${searchResults.matchingShlokas.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(searchResults.matchingShlokas, key = { "sl_${it.id}" }) { shloka ->
                val vKey = verseKey(shloka.chapter, shloka.verse)
                RankedShlokaCard(
                    shloka = shloka,
                    isBookmarked = vKey in bookmarkedIds,
                    isRead = vKey in readIds,
                    onClick = { onOpenVerse(shloka.chapter, shloka.verse) }
                )
            }
        }
    }
}
