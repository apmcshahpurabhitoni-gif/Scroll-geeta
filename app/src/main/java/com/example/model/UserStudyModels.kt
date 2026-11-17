package com.example.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

val MOOD_TAGS = listOf("Clarity", "Peace", "Duty", "Devotion", "Gratitude")

@Keep
@IgnoreExtraProperties
data class VerseBookmark(
    @DocumentId val id: String = "",
    val userId: String = "",
    val verseId: String = "",
    val chapter: Long = 1L,
    val verse: Long = 1L,
    val chapterTitle: String = "",
    val sanskritPreview: String = "",
    val hindiPreview: String = "",
    val englishPreview: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class VerseReflection(
    @DocumentId val id: String = "",
    val userId: String = "",
    val verseId: String = "",
    val chapter: Long = 1L,
    val verse: Long = 1L,
    val noteText: String = "",
    val moodTag: String = "Clarity",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ReadingProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val lastChapter: Long = 1L,
    val lastVerse: Long = 1L,
    val currentStreak: Long = 0L,
    val longestStreak: Long = 0L,
    val totalVersesRead: Long = 0L,
    val lastReadDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ReadVerseRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val verseId: String = "",
    val chapter: Long = 1L,
    val verse: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null
)
