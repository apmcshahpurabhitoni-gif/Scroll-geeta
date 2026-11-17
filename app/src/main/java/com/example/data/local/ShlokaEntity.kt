package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shlokas",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["chapterNumber"],
            childColumns = ["chapter"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chapter"]),
        Index(value = ["chapter", "importanceScore"])
    ]
)
data class ShlokaEntity(
    @PrimaryKey val verseId: String,
    val chapter: Int,
    val verse: Int,
    val speakerContext: String,
    val sanskrit: String,
    val transliteration: String,
    val hindi: String,
    val english: String,
    val explainersJson: String,
    val importanceScore: Int,
    val importanceTier: String,
    val requiresAdjacentContext: Boolean,
    val contextSummary: String,
    val updatedEpochMs: Long
)
