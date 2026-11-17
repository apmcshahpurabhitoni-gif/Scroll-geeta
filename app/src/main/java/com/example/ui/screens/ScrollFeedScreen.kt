package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.seed.GitaCatalogSeed
import com.example.model.ChapterSummary
import com.example.model.DailyVerseInfo
import com.example.model.KrishnaScene
import com.example.model.ReadingProgress
import com.example.model.Shloka
import com.example.model.VerseContextBundle
import com.example.model.VerseImportanceTier
import com.example.model.verseKey
import com.example.ui.components.InlineReflectionPanel
import com.example.ui.components.InlineVerseExplainer
import com.example.ui.theme.GoldLeaf
import com.example.ui.theme.KrishnaIndigo
import com.example.ui.theme.SacredSaffron
import com.example.ui.viewmodel.ShlokaCardState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

private enum class CardInlinePanel {
    NONE,
    EXPLAIN,
    REFLECT
}

@Composable
fun ScrollFeedScreen(
    totalVerses: Int,
    chapters: List<ChapterSummary>,
    dailyVerse: DailyVerseInfo,
    currentPageIndex: Int,
    jumpToPageEvent: SharedFlow<Int>,
    shlokaStates: Map<String, ShlokaCardState>,
    bookmarkedIds: Set<String>,
    readVerseIds: Set<String>,
    progress: ReadingProgress?,
    explainerContextBundle: VerseContextBundle?,
    isExplainerContextLoading: Boolean,
    chapterVerseForIndex: (Int) -> Pair<Int, Int>,
    chapterForNumber: (Int) -> ChapterSummary,
    onPageSettled: (Int) -> Unit,
    onJumpToVerse: (Int, Int) -> Unit,
    onRetryLoadVerse: (Int, Int) -> Unit,
    onToggleBookmark: (Shloka, Boolean) -> Unit,
    onMarkAsRead: (Shloka) -> Unit,
    onSaveReflection: (Shloka, String, String) -> Unit,
    onPrepareExplainer: (Shloka) -> Unit,
    onClearExplainer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = currentPageIndex.coerceIn(0, (totalVerses - 1).coerceAtLeast(0)),
        pageCount = { totalVerses }
    )

    var showInlineChapterBar by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settled ->
                onPageSettled(settled)
            }
    }

    LaunchedEffect(jumpToPageEvent) {
        jumpToPageEvent.collect { targetPage ->
            val clamped = targetPage.coerceIn(0, (totalVerses - 1).coerceAtLeast(0))
            if (pagerState.currentPage != clamped) {
                pagerState.scrollToPage(clamped)
            }
        }
    }

    val (activeChapter, activeVerse) = chapterVerseForIndex(pagerState.currentPage)
    val activeScene = remember(activeChapter, activeVerse) {
        GitaCatalogSeed.sceneForVerse(activeChapter, activeVerse)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Ambient backdrop that seamlessly echoes the card's Krishna artwork
        Crossfade(
            targetState = activeScene,
            animationSpec = tween(durationMillis = 650),
            label = "ambient_krishna_backdrop"
        ) { scene ->
            Image(
                painter = painterResource(id = scene.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xD90A0F1F),
                            Color(0xCC0D1224),
                            Color(0xE80A0C16)
                        )
                    )
                )
        )

        // Full-Screen Vertical Swipeable Immersive Shloka Pager
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { pageIndex ->
            val (ch, vs) = chapterVerseForIndex(pageIndex)
            val key = verseKey(ch, vs)
            val cardState = shlokaStates[key] ?: ShlokaCardState.Loading
            val chapterSummary = chapterForNumber(ch)
            val isBookmarked = key in bookmarkedIds
            val isRead = key in readVerseIds

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 64.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                ImmersiveKrishnaShlokaCard(
                    chapter = ch,
                    verse = vs,
                    chapterSummary = chapterSummary,
                    scene = GitaCatalogSeed.sceneForVerse(ch, vs),
                    cardState = cardState,
                    isBookmarked = isBookmarked,
                    isRead = isRead,
                    showSwipeHint = pageIndex == 0,
                    explainerContextBundle = explainerContextBundle,
                    isExplainerContextLoading = isExplainerContextLoading,
                    onRetry = { onRetryLoadVerse(ch, vs) },
                    onToggleBookmark = { shloka -> onToggleBookmark(shloka, isBookmarked) },
                    onMarkRead = { shloka -> onMarkAsRead(shloka) },
                    onSaveReflection = onSaveReflection,
                    onPrepareExplainer = onPrepareExplainer,
                    onClearExplainer = onClearExplainer,
                    onJumpToVerse = onJumpToVerse
                )
            }
        }

        // Floating Top Bar + Inline Chapter Quick-Jump Strip (No separate popup windows)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 620.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xCC11172C),
                    border = BorderStroke(1.dp, GoldLeaf.copy(alpha = 0.5f)),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clickable { showInlineChapterBar = !showInlineChapterBar }
                        .testTag("chapter_picker_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ch. $activeChapter • Shloka $activeVerse",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFFAF3E6)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (showInlineChapterBar) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = "Quick chapter jumper",
                            tint = Color(0xFFFFD180),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SacredSaffron.copy(alpha = 0.94f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .clickable {
                                showInlineChapterBar = false
                                onJumpToVerse(dailyVerse.chapter, dailyVerse.verse)
                            }
                            .testTag("daily_verse_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WbSunny,
                                contentDescription = "Jump to daily verse",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Daily",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xCC11172C),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = "Reading streak",
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${progress?.currentStreak ?: 0}d",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showInlineChapterBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xEC101528),
                    border = BorderStroke(1.dp, GoldLeaf.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .widthIn(max = 620.dp)
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chapters, key = { it.chapterNumber }) { ch ->
                            val isCurrent = ch.chapterNumber == activeChapter
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCurrent) Color(0xFFFFD180) else Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.clickable {
                                    showInlineChapterBar = false
                                    onJumpToVerse(ch.chapterNumber, 1)
                                }
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ch. ${ch.chapterNumber}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCurrent) Color(0xFF1A1205) else Color(0xFFFAF3E6)
                                    )
                                    Text(
                                        text = ch.englishTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isCurrent) Color(0xFF3E2800) else Color(0xFFCBBFA9),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImmersiveKrishnaShlokaCard(
    chapter: Int,
    verse: Int,
    chapterSummary: ChapterSummary,
    scene: KrishnaScene,
    cardState: ShlokaCardState,
    isBookmarked: Boolean,
    isRead: Boolean,
    showSwipeHint: Boolean,
    explainerContextBundle: VerseContextBundle?,
    isExplainerContextLoading: Boolean,
    onRetry: () -> Unit,
    onToggleBookmark: (Shloka) -> Unit,
    onMarkRead: (Shloka) -> Unit,
    onSaveReflection: (Shloka, String, String) -> Unit,
    onPrepareExplainer: (Shloka) -> Unit,
    onClearExplainer: () -> Unit,
    onJumpToVerse: (Int, Int) -> Unit
) {
    var activePanel by remember(chapter, verse) { mutableStateOf(CardInlinePanel.NONE) }
    val cardScrollState = rememberScrollState()

    LaunchedEffect(activePanel) {
        if (activePanel != CardInlinePanel.NONE) {
            cardScrollState.animateScrollTo(cardScrollState.maxValue.coerceAtLeast(240))
        }
    }

    val goldText = Color(0xFFFFD180)
    val ivoryText = Color(0xFFFAF3E6)
    val mutedIvory = Color(0xFFD2C7B4)

    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color(0xFF101528),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.verticalGradient(
                listOf(
                    goldText.copy(alpha = 0.55f),
                    Color.White.copy(alpha = 0.16f),
                    goldText.copy(alpha = 0.40f)
                )
            )
        ),
        shadowElevation = 14.dp,
        modifier = Modifier
            .widthIn(max = 620.dp)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Krishna artwork merged directly inside the full-height verse card
            Image(
                painter = painterResource(id = scene.drawableRes),
                contentDescription = scene.sceneTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Smooth atmospheric gradient merge: clear Krishna painting above, velvet twilight reading surface below
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.00f to Color(0x520B1022),
                            0.24f to Color(0x3D0B1022),
                            0.44f to Color(0xBA0E1428),
                            0.64f to Color(0xE6101528),
                            1.00f to Color(0xF50E111E)
                        )
                    )
            )

            when (cardState) {
                ShlokaCardState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${chapterSummary.sanskritName} • BG $chapter.$verse",
                            style = MaterialTheme.typography.titleMedium,
                            color = goldText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(color = goldText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Loading shloka & commentaries...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ivoryText
                        )
                    }
                }

                is ShlokaCardState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "BG $chapter.$verse • ${chapterSummary.englishTitle}",
                            style = MaterialTheme.typography.titleLarge,
                            color = goldText
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = cardState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ivoryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        OutlinedButton(
                            onClick = onRetry,
                            border = BorderStroke(1.dp, goldText)
                        ) {
                            Text("Retry Loading", color = goldText)
                        }
                    }
                }

                is ShlokaCardState.Ready -> {
                    val shloka = cardState.shloka
                    val tierAccent = when (shloka.importanceTier) {
                        VerseImportanceTier.CORE_ESSENCE -> goldText
                        VerseImportanceTier.KEY_INSIGHT -> Color(0xFFA8CBFF)
                        VerseImportanceTier.SUPPORTING_CONTEXT -> Color(0xFFA2E6B8)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(cardScrollState)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top Header Floating Cleanly Over Krishna Scene
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0x880C1122),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                        Text(
                                            text = "${chapterSummary.englishTitle.uppercase()} • $chapter.$verse",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = goldText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = shloka.speakerContext,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ivoryText.copy(alpha = 0.9f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x990D1224),
                                    border = BorderStroke(1.dp, tierAccent.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        text = shloka.importanceTier.shortBadge,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = tierAccent,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                    )
                                }
                            }

                            // Scenic breathing space so Krishna's artwork shines through clearly
                            Spacer(modifier = Modifier.height(if (activePanel == CardInlinePanel.NONE) 92.dp else 28.dp))
                        }

                        // Merged Editorial Shloka Body
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Frosted Gold-Rimmed Sanskrit Block
                            Surface(
                                color = Color(0x8012172B),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, goldText.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = shloka.sanskrit,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 21.sp,
                                            lineHeight = 32.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = Color(0xFFFFE5AE),
                                        textAlign = TextAlign.Center
                                    )
                                    if (shloka.transliteration.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = shloka.transliteration,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontStyle = FontStyle.Italic,
                                                lineHeight = 19.sp
                                            ),
                                            color = mutedIvory,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "हिन्दी अर्थ",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = goldText
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = shloka.hindi,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.5.sp,
                                    lineHeight = 25.sp
                                ),
                                color = ivoryText
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.16f))
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "ENGLISH MEANING",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = goldText
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = shloka.english,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.5.sp,
                                    lineHeight = 25.sp
                                ),
                                color = ivoryText
                            )

                            if (shloka.requiresAdjacentContext) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Context note: Connects directly with surrounding verses — tap Explain below to unfold.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = mutedIvory
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Row: Read, Bookmark, Inline Reflect, Inline Explain
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalIconButton(
                                    onClick = { onMarkRead(shloka) },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (isRead) Color(0xFF2E6F40) else Color.White.copy(alpha = 0.14f),
                                        contentColor = if (isRead) Color.White else ivoryText
                                    ),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("mark_read_button")
                                ) {
                                    Icon(
                                        imageVector = if (isRead) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
                                        contentDescription = if (isRead) "Marked as read" else "Mark shloka read"
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = { onToggleBookmark(shloka) },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (isBookmarked) SacredSaffron else Color.White.copy(alpha = 0.14f),
                                        contentColor = if (isBookmarked) Color.White else ivoryText
                                    ),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("bookmark_button")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark shloka"
                                    )
                                }

                                FilledTonalIconButton(
                                    onClick = {
                                        activePanel = if (activePanel == CardInlinePanel.REFLECT) {
                                            CardInlinePanel.NONE
                                        } else {
                                            CardInlinePanel.REFLECT
                                        }
                                    },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = if (activePanel == CardInlinePanel.REFLECT) goldText else Color.White.copy(alpha = 0.14f),
                                        contentColor = if (activePanel == CardInlinePanel.REFLECT) Color(0xFF1A1205) else ivoryText
                                    ),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("reflect_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditNote,
                                        contentDescription = "Add personal reflection"
                                    )
                                }

                                val isExplaining = activePanel == CardInlinePanel.EXPLAIN
                                Button(
                                    onClick = {
                                        if (isExplaining) {
                                            onClearExplainer()
                                            activePanel = CardInlinePanel.NONE
                                        } else {
                                            onPrepareExplainer(shloka)
                                            activePanel = CardInlinePanel.EXPLAIN
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isExplaining) Color.White.copy(alpha = 0.16f) else goldText,
                                        contentColor = if (isExplaining) ivoryText else Color(0xFF1A1205)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("expand_explain_button")
                                ) {
                                    Icon(
                                        imageVector = if (isExplaining) Icons.Rounded.ExpandLess else Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isExplaining) "Hide Explain" else "Explain",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                }
                            }

                            // Inline Context-Aware Explainer (No separate window!)
                            AnimatedVisibility(
                                visible = activePanel == CardInlinePanel.EXPLAIN,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                InlineVerseExplainer(
                                    shloka = shloka,
                                    contextBundle = explainerContextBundle,
                                    isContextLoading = isExplainerContextLoading,
                                    onJumpToNeighbor = { nCh, nVs ->
                                        onClearExplainer()
                                        activePanel = CardInlinePanel.NONE
                                        onJumpToVerse(nCh, nVs)
                                    },
                                    onCollapse = {
                                        onClearExplainer()
                                        activePanel = CardInlinePanel.NONE
                                    },
                                    modifier = Modifier.padding(top = 14.dp)
                                )
                            }

                            // Inline Reflection Composer (No separate window!)
                            AnimatedVisibility(
                                visible = activePanel == CardInlinePanel.REFLECT,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                InlineReflectionPanel(
                                    chapter = shloka.chapter,
                                    verse = shloka.verse,
                                    onSave = { noteText, moodTag ->
                                        onSaveReflection(shloka, noteText, moodTag)
                                        activePanel = CardInlinePanel.NONE
                                    },
                                    onCancel = { activePanel = CardInlinePanel.NONE },
                                    modifier = Modifier.padding(top = 14.dp)
                                )
                            }

                            if (showSwipeHint && activePanel == CardInlinePanel.NONE) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = mutedIvory,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Swipe up to scroll the Geeta",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = mutedIvory
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
