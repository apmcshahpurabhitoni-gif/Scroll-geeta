package com.example.data.repository

import com.example.data.local.ChapterDao
import com.example.data.local.ChapterEntity
import com.example.data.local.ShlokaDao
import com.example.data.local.ShlokaEntity
import com.example.data.remote.SlokApiResponse
import com.example.data.remote.VedicScripturesApi
import com.example.data.seed.GitaCatalogSeed
import com.example.model.ChapterHierarchy
import com.example.model.ChapterSummary
import com.example.model.DailyVerseInfo
import com.example.model.ExplainerPerspective
import com.example.model.KrishnaScene
import com.example.model.Shloka
import com.example.model.VerseContextBundle
import com.example.model.VerseImportanceTier
import com.example.model.verseKey
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Calendar

data class GitaSearchResult(
    val query: String = "",
    val directVerseTarget: Pair<Int, Int>? = null,
    val directVersePreview: Shloka? = null,
    val matchingChapters: List<ChapterSummary> = emptyList(),
    val matchingShlokas: List<Shloka> = emptyList()
)

class GitaScriptureRepository(
    private val chapterDao: ChapterDao,
    private val shlokaDao: ShlokaDao,
    private val api: VedicScripturesApi
) {
    val chapters: List<ChapterSummary> = GitaCatalogSeed.chapters
    val totalVerses: Int = chapters.sumOf { it.versesCount }

    private val seedMutex = Mutex()

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val explainersAdapter = moshi.adapter<List<ExplainerPerspective>>(
        Types.newParameterizedType(List::class.java, ExplainerPerspective::class.java)
    )

    fun getChapter(chapter: Int): ChapterSummary {
        return chapters.firstOrNull { it.chapterNumber == chapter } ?: chapters.first()
    }

    fun chapterVerseToIndex(chapter: Int, verse: Int): Int {
        var index = 0
        for (ch in chapters) {
            if (ch.chapterNumber < chapter) {
                index += ch.versesCount
            } else if (ch.chapterNumber == chapter) {
                val clampedVerse = verse.coerceIn(1, ch.versesCount)
                return (index + clampedVerse - 1).coerceIn(0, totalVerses - 1)
            }
        }
        return 0
    }

    fun indexToChapterVerse(pageIndex: Int): Pair<Int, Int> {
        val clamped = pageIndex.coerceIn(0, totalVerses - 1)
        var remaining = clamped
        for (ch in chapters) {
            if (remaining < ch.versesCount) {
                return ch.chapterNumber to (remaining + 1)
            }
            remaining -= ch.versesCount
        }
        return 1 to 1
    }

    fun getPreviousCoordinates(chapter: Int, verse: Int): Pair<Int, Int>? {
        val index = chapterVerseToIndex(chapter, verse)
        if (index <= 0) return null
        return indexToChapterVerse(index - 1)
    }

    fun getNextCoordinates(chapter: Int, verse: Int): Pair<Int, Int>? {
        val index = chapterVerseToIndex(chapter, verse)
        if (index >= totalVerses - 1) return null
        return indexToChapterVerse(index + 1)
    }

    fun getDailyVerse(): DailyVerseInfo {
        val list = GitaCatalogSeed.dailyVerses
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val idx = (dayOfYear % list.size).coerceIn(0, list.lastIndex)
        return list[idx]
    }

    private suspend fun ensureChaptersSeededInternal() {
        if (chapterDao.countChapters() < chapters.size) {
            chapterDao.upsertAll(chapters.map { it.toEntity() })
        }
    }

    suspend fun ensureSeedCached() = withContext(Dispatchers.IO) {
        seedMutex.withLock {
            ensureChaptersSeededInternal()
            val seedEntities = GitaCatalogSeed.allSeedShlokas.map { it.toEntity() }
            if (shlokaDao.countCached() < seedEntities.size) {
                shlokaDao.upsertAll(seedEntities)
            }
        }
    }

    fun observeChapterHierarchy(): Flow<List<ChapterHierarchy>> {
        return chapterDao.observeChaptersWithShlokas().map { relations ->
            if (relations.isEmpty()) {
                chapters.map { ChapterHierarchy(it, emptyMap()) }
            } else {
                relations.map { relation ->
                    ChapterHierarchy(
                        chapter = relation.chapter.toDomain(),
                        cachedShlokas = relation.shlokas.associate { it.verse to it.toDomain() }
                    )
                }
            }
        }
    }

    fun observeChapterShlokas(chapter: Int): Flow<Map<Int, Shloka>> {
        return shlokaDao.observeShlokasByChapter(chapter).map { entities ->
            entities.associate { it.verse to it.toDomain() }
        }
    }

    suspend fun prefetchChapterRange(chapter: Int, startVerse: Int, endVerse: Int) {
        withContext(Dispatchers.IO) {
            ensureChaptersSeededInternal()
            val chSummary = getChapter(chapter)
            val from = startVerse.coerceIn(1, chSummary.versesCount)
            val to = endVerse.coerceIn(from, chSummary.versesCount)
            coroutineScope {
                (from..to).map { verse ->
                    async {
                        if (shlokaDao.getShloka(chapter, verse) == null) {
                            runCatching { getShloka(chapter, verse) }
                        }
                    }
                }.awaitAll()
            }
        }
    }

    suspend fun prefetchTopRankedForChapter(chapter: Int) {
        withContext(Dispatchers.IO) {
            ensureChaptersSeededInternal()
            val chSummary = getChapter(chapter)
            val priorityVerses = (1..chSummary.versesCount)
                .filter { v -> GitaCatalogSeed.verseRankingFor(chapter, v).score >= 85 }
                .take(12)
            coroutineScope {
                priorityVerses.map { verse ->
                    async {
                        if (shlokaDao.getShloka(chapter, verse) == null) {
                            runCatching { getShloka(chapter, verse) }
                        }
                    }
                }.awaitAll()
            }
        }
    }

    suspend fun getVerseContextBundle(chapter: Int, verse: Int): VerseContextBundle =
        withContext(Dispatchers.IO) {
            ensureSeedCached()
            val prevCoord = getPreviousCoordinates(chapter, verse)
            val nextCoord = getNextCoordinates(chapter, verse)

            coroutineScope {
                val targetDeferred = async {
                    getShloka(chapter, verse).getOrNull() ?: fallbackStubShloka(chapter, verse)
                }
                val prevDeferred = async {
                    prevCoord?.let { (pCh, pVs) -> getShloka(pCh, pVs).getOrNull() }
                }
                val nextDeferred = async {
                    nextCoord?.let { (nCh, nVs) -> getShloka(nCh, nVs).getOrNull() }
                }

                VerseContextBundle(
                    target = targetDeferred.await(),
                    previousShloka = prevDeferred.await(),
                    nextShloka = nextDeferred.await()
                )
            }
        }

    suspend fun searchGita(rawQuery: String): GitaSearchResult = withContext(Dispatchers.IO) {
        val trimmed = rawQuery.trim()
        if (trimmed.isEmpty()) {
            return@withContext GitaSearchResult()
        }

        ensureSeedCached()

        val directTarget = parseDirectVerseReference(trimmed)
        val directPreview = directTarget?.let { (ch, vs) ->
            getShloka(ch, vs).getOrNull()
        }

        val matchingChapters = findMatchingChapters(trimmed)

        val keywordResults = shlokaDao.searchShlokas(trimmed).map { it.toDomain() }
            .ifEmpty {
                val tokens = trimmed.lowercase().split(Regex("\\s+")).filter { it.length >= 2 }
                if (tokens.size > 1) {
                    shlokaDao.searchShlokas(tokens.first())
                        .map { it.toDomain() }
                        .filter { shloka ->
                            val hay = "${shloka.sanskrit} ${shloka.transliteration} ${shloka.hindi} ${shloka.english} ${shloka.speakerContext} ${shloka.contextSummary}".lowercase()
                            tokens.all { hay.contains(it) }
                        }
                } else {
                    emptyList()
                }
            }

        GitaSearchResult(
            query = trimmed,
            directVerseTarget = directTarget,
            directVersePreview = directPreview,
            matchingChapters = matchingChapters,
            matchingShlokas = keywordResults
        )
    }

    private fun parseDirectVerseReference(query: String): Pair<Int, Int>? {
        val cleaned = query.trim().lowercase()
        val twoNumberRegex = Regex(
            "(?:bg\\s*|chapter\\s*|ch\\.?\\s*|adhyay\\s*)?(\\d{1,2})\\s*(?:[.:_\\-/\\s]|verse\\s*|shloka\\s*|v\\.?\\s*)+(\\d{1,2})"
        )
        val match = twoNumberRegex.matchEntire(cleaned) ?: return null
        val ch = match.groupValues[1].toIntOrNull() ?: return null
        val vs = match.groupValues[2].toIntOrNull() ?: return null
        if (ch !in 1..18) return null
        val maxVerses = getChapter(ch).versesCount
        if (vs !in 1..maxVerses) return null
        return ch to vs
    }

    private fun findMatchingChapters(query: String): List<ChapterSummary> {
        val lower = query.trim().lowercase()
        val chapterNumberMatch = Regex("(?:chapter|ch\\.?|adhyay)?\\s*(\\d{1,2})")
            .matchEntire(lower)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()

        return chapters.filter { ch ->
            (chapterNumberMatch != null && ch.chapterNumber == chapterNumberMatch) ||
                ch.sanskritName.lowercase().contains(lower) ||
                ch.transliteration.lowercase().contains(lower) ||
                ch.englishTitle.lowercase().contains(lower) ||
                ch.meaning.lowercase().contains(lower) ||
                ch.summary.lowercase().contains(lower)
        }
    }

    suspend fun getShloka(chapter: Int, verse: Int): Result<Shloka> = withContext(Dispatchers.IO) {
        ensureChaptersSeededInternal()
        val seed = GitaCatalogSeed.getSeedShloka(chapter, verse)
        val cached = shlokaDao.getShloka(chapter, verse)?.toDomain()
        if (cached != null) {
            return@withContext Result.success(cached)
        }

        try {
            val remote = api.getSlok(chapter, verse)
            val mapped = remote.toDomain(seed)
            shlokaDao.upsertShloka(mapped.toEntity())
            Result.success(mapped)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (seed != null) {
                shlokaDao.upsertShloka(seed.toEntity())
                Result.success(seed)
            } else {
                Result.failure(e)
            }
        }
    }

    private fun fallbackStubShloka(chapter: Int, verse: Int): Shloka {
        val ranking = GitaCatalogSeed.verseRankingFor(chapter, verse)
        return Shloka(
            id = verseKey(chapter, verse),
            chapter = chapter,
            verse = verse,
            speakerContext = GitaCatalogSeed.speakerForVerse(chapter, verse),
            sanskrit = "श्लोक $chapter.$verse",
            transliteration = "",
            hindi = "",
            english = "Tap to load full translation for BG $chapter.$verse.",
            explainers = emptyList(),
            scene = GitaCatalogSeed.sceneForVerse(chapter, verse),
            importanceScore = ranking.score,
            importanceTier = ranking.tier,
            requiresAdjacentContext = ranking.requiresAdjacentContext,
            contextSummary = ranking.contextSummary
        )
    }

    private fun SlokApiResponse.toDomain(seedFallback: Shloka?): Shloka {
        val sanskritText = slok?.trim().takeUnless { it.isNullOrBlank() }
            ?: seedFallback?.sanskrit
            ?: "श्लोक उपलब्ध नहीं है।"
        val translitText = transliteration?.trim().takeUnless { it.isNullOrBlank() }
            ?: seedFallback?.transliteration
            ?: ""
        val hindiText = listOfNotNull(
            tej?.hindiTranslation,
            rams?.hindiTranslation,
            chinmay?.hindiCommentary,
            seedFallback?.hindi
        ).firstOrNull { it.isNotBlank() }?.trim()
            ?: "हिन्दी अनुवाद लोड नहीं हो सका।"
        val englishText = listOfNotNull(
            siva?.englishTranslation,
            purohit?.englishTranslation,
            adi?.englishTranslation,
            gambir?.englishTranslation,
            seedFallback?.english
        ).firstOrNull { it.isNotBlank() }?.trim()
            ?: "English translation unavailable."

        val explainers = buildList {
            siva?.englishCommentary?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Swami Sivananda", "Divine Life Society", "English", it.trim()))
            }
            chinmay?.hindiCommentary?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Swami Chinmayananda", "Chinmaya Mission", "हिन्दी", it.trim()))
            }
            rams?.hindiCommentary?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Swami Ramsukhdas", "Gita Press Sadhak Sanjeevani", "हिन्दी", it.trim()))
            }
            tej?.hindiTranslation?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Swami Tejomayananda", "Hindi Purport", "हिन्दी", it.trim()))
            }
            purohit?.englishTranslation?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Shri Purohit Swami", "Literary Translation", "English", it.trim()))
            }
            adi?.englishTranslation?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Adi Shankaracharya", "Advaita Tradition", "English", it.trim()))
            }
            gambir?.englishTranslation?.takeIf { it.isNotBlank() }?.let {
                add(ExplainerPerspective("Swami Gambirananda", "Ramakrishna Order", "English", it.trim()))
            }
        }.ifEmpty {
            seedFallback?.explainers ?: listOf(
                ExplainerPerspective(
                    scholarName = "Scriptural Summary",
                    tradition = "Gita Scroll",
                    languageLabel = "English",
                    content = englishText
                )
            )
        }

        val ranking = GitaCatalogSeed.verseRankingFor(chapter, verse)

        return Shloka(
            id = verseKey(chapter, verse),
            chapter = chapter,
            verse = verse,
            speakerContext = GitaCatalogSeed.speakerForVerse(chapter, verse),
            sanskrit = sanskritText,
            transliteration = translitText,
            hindi = hindiText,
            english = englishText,
            explainers = explainers,
            scene = GitaCatalogSeed.sceneForVerse(chapter, verse),
            importanceScore = ranking.score,
            importanceTier = ranking.tier,
            requiresAdjacentContext = ranking.requiresAdjacentContext,
            contextSummary = ranking.contextSummary
        )
    }

    private fun ChapterSummary.toEntity(): ChapterEntity = ChapterEntity(
        chapterNumber = chapterNumber,
        sanskritName = sanskritName,
        transliteration = transliteration,
        englishTitle = englishTitle,
        meaning = meaning,
        summary = summary,
        versesCount = versesCount,
        sceneName = scene.name
    )

    private fun ChapterEntity.toDomain(): ChapterSummary = ChapterSummary(
        chapterNumber = chapterNumber,
        sanskritName = sanskritName,
        transliteration = transliteration,
        englishTitle = englishTitle,
        meaning = meaning,
        summary = summary,
        versesCount = versesCount,
        scene = KrishnaScene.entries.firstOrNull { it.name == sceneName } ?: KrishnaScene.KURUKSHETRA
    )

    private fun Shloka.toEntity(): ShlokaEntity {
        val json = explainersAdapter.toJson(explainers)
        return ShlokaEntity(
            verseId = id,
            chapter = chapter,
            verse = verse,
            speakerContext = speakerContext,
            sanskrit = sanskrit,
            transliteration = transliteration,
            hindi = hindi,
            english = english,
            explainersJson = json,
            importanceScore = importanceScore,
            importanceTier = importanceTier.name,
            requiresAdjacentContext = requiresAdjacentContext,
            contextSummary = contextSummary,
            updatedEpochMs = System.currentTimeMillis()
        )
    }

    private fun ShlokaEntity.toDomain(): Shloka {
        val parsedExplainers = try {
            explainersAdapter.fromJson(explainersJson).orEmpty()
        } catch (_: Exception) {
            emptyList()
        }
        return Shloka(
            id = verseId,
            chapter = chapter,
            verse = verse,
            speakerContext = speakerContext,
            sanskrit = sanskrit,
            transliteration = transliteration,
            hindi = hindi,
            english = english,
            explainers = parsedExplainers,
            scene = GitaCatalogSeed.sceneForVerse(chapter, verse),
            importanceScore = importanceScore,
            importanceTier = VerseImportanceTier.fromName(importanceTier),
            requiresAdjacentContext = requiresAdjacentContext,
            contextSummary = contextSummary
        )
    }
}
