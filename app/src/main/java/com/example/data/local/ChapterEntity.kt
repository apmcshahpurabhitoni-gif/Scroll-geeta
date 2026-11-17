package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val chapterNumber: Int,
    val sanskritName: String,
    val transliteration: String,
    val englishTitle: String,
    val meaning: String,
    val summary: String,
    val versesCount: Int,
    val sceneName: String
)

data class ChapterWithShlokas(
    @Embedded val chapter: ChapterEntity,
    @Relation(
        parentColumn = "chapterNumber",
        entityColumn = "chapter"
    )
    val shlokas: List<ShlokaEntity>
)
