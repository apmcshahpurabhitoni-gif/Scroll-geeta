package com.example.model

import androidx.annotation.DrawableRes
import com.example.R
import com.squareup.moshi.JsonClass

enum class KrishnaScene(
    @DrawableRes val drawableRes: Int,
    val sceneTitle: String,
    val situationTag: String
) {
    KURUKSHETRA(
        drawableRes = R.drawable.img_krishna_kurukshetra,
        sceneTitle = "Kurukshetra Chariot Counsel",
        situationTag = "On the sacred battlefield of Kurukshetra"
    ),
    KARMA(
        drawableRes = R.drawable.img_krishna_karma,
        sceneTitle = "Selfless Action & Dharma",
        situationTag = "The luminous path of Karma & Dharma"
    ),
    DHYANA(
        drawableRes = R.drawable.img_krishna_dhyana,
        sceneTitle = "Stillness & Dhyana Meditation",
        situationTag = "Quiet mastery of mind beside lotus waters"
    ),
    VISHWAROOPA(
        drawableRes = R.drawable.img_krishna_vishwaroopa,
        sceneTitle = "Cosmic Vishwaroopa & Bhakti",
        situationTag = "Divine cosmic radiance & loving devotion"
    )
}

enum class VerseImportanceTier(
    val level: Int,
    val label: String,
    val shortBadge: String,
    val description: String
) {
    CORE_ESSENCE(
        level = 3,
        label = "Core Essence",
        shortBadge = "★★★ Essential",
        description = "Foundational shloka standing on its own as a pillar of Gita philosophy."
    ),
    KEY_INSIGHT(
        level = 2,
        label = "Key Insight",
        shortBadge = "★★ Key",
        description = "Important teaching or pivotal dialogue turn in the chapter."
    ),
    SUPPORTING_CONTEXT(
        level = 1,
        label = "Supporting Context",
        shortBadge = "★ Context Link",
        description = "Narrative or continuing clause; best understood with previous and next verses."
    );

    companion object {
        fun fromName(raw: String?): VerseImportanceTier {
            return entries.firstOrNull { it.name == raw } ?: KEY_INSIGHT
        }
    }
}

data class VerseRanking(
    val score: Int,
    val tier: VerseImportanceTier,
    val requiresAdjacentContext: Boolean,
    val contextSummary: String
)

data class ChapterSummary(
    val chapterNumber: Int,
    val sanskritName: String,
    val transliteration: String,
    val englishTitle: String,
    val meaning: String,
    val summary: String,
    val versesCount: Int,
    val scene: KrishnaScene
)

@JsonClass(generateAdapter = true)
data class ExplainerPerspective(
    val scholarName: String,
    val tradition: String,
    val languageLabel: String,
    val content: String
)

data class Shloka(
    val id: String,
    val chapter: Int,
    val verse: Int,
    val speakerContext: String,
    val sanskrit: String,
    val transliteration: String,
    val hindi: String,
    val english: String,
    val explainers: List<ExplainerPerspective>,
    val scene: KrishnaScene,
    val importanceScore: Int = 75,
    val importanceTier: VerseImportanceTier = VerseImportanceTier.KEY_INSIGHT,
    val requiresAdjacentContext: Boolean = false,
    val contextSummary: String = ""
)

data class VerseContextBundle(
    val target: Shloka,
    val previousShloka: Shloka? = null,
    val nextShloka: Shloka? = null
)

data class ChapterHierarchy(
    val chapter: ChapterSummary,
    val cachedShlokas: Map<Int, Shloka>
)

data class DailyVerseInfo(
    val chapter: Int,
    val verse: Int,
    val themeTitle: String,
    val reflectionPrompt: String
)

fun verseKey(chapter: Int, verse: Int): String = "BG${chapter}_${verse}"
