package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.GitaDatabase
import com.example.data.remote.SlokApiResponse
import com.example.data.remote.VedicScripturesApi
import com.example.data.repository.GitaScriptureRepository
import com.example.data.seed.GitaCatalogSeed
import com.example.model.VerseImportanceTier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GitaHierarchyAndRankingTest {

    private lateinit var db: GitaDatabase
    private lateinit var repository: GitaScriptureRepository

    private val fakeApi = object : VedicScripturesApi {
        override suspend fun getSlok(chapter: Int, verse: Int): SlokApiResponse {
            return SlokApiResponse(
                id = "BG${chapter}_$verse",
                chapter = chapter,
                verse = verse,
                slok = "Test Sanskrit $chapter.$verse",
                transliteration = "test transliteration $chapter.$verse"
            )
        }
    }

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, GitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = GitaScriptureRepository(
            chapterDao = db.chapterDao(),
            shlokaDao = db.shlokaDao(),
            api = fakeApi
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seed populates 18 chapter hierarchy with relational shlokas`() = runTest {
        repository.ensureSeedCached()
        val hierarchy = repository.observeChapterHierarchy().first()
        assertEquals(18, hierarchy.size)
        assertEquals(1, hierarchy.first().chapter.chapterNumber)
        assertEquals(18, hierarchy.last().chapter.chapterNumber)
        assertTrue(hierarchy[1].cachedShlokas.containsKey(47))
        assertEquals(100, hierarchy[1].cachedShlokas[47]?.importanceScore)
    }

    @Test
    fun `ranking distinguishes standalone core verses from context-dependent links`() {
        val coreVerse = GitaCatalogSeed.verseRankingFor(2, 47)
        assertEquals(VerseImportanceTier.CORE_ESSENCE, coreVerse.tier)
        assertEquals(100, coreVerse.score)

        val pairedVerse = GitaCatalogSeed.verseRankingFor(4, 8)
        assertEquals(VerseImportanceTier.CORE_ESSENCE, pairedVerse.tier)
        assertTrue(pairedVerse.requiresAdjacentContext)

        val narrativeLink = GitaCatalogSeed.verseRankingFor(1, 5)
        assertEquals(VerseImportanceTier.SUPPORTING_CONTEXT, narrativeLink.tier)
        assertTrue(narrativeLink.requiresAdjacentContext)
    }

    @Test
    fun `context bundle loads previous and next shlokas across boundaries`() = runTest {
        val firstBundle = repository.getVerseContextBundle(1, 1)
        assertNull(firstBundle.previousShloka)
        assertNotNull(firstBundle.nextShloka)
        assertEquals(1, firstBundle.nextShloka?.chapter)
        assertEquals(2, firstBundle.nextShloka?.verse)

        val crossChapterBundle = repository.getVerseContextBundle(2, 1)
        assertEquals(1, crossChapterBundle.previousShloka?.chapter)
        assertEquals(47, crossChapterBundle.previousShloka?.verse)
        assertEquals(2, crossChapterBundle.nextShloka?.chapter)
        assertEquals(2, crossChapterBundle.nextShloka?.verse)

        val lastBundle = repository.getVerseContextBundle(18, 78)
        assertEquals(18, lastBundle.previousShloka?.chapter)
        assertEquals(77, lastBundle.previousShloka?.verse)
        assertNull(lastBundle.nextShloka)
    }
}
