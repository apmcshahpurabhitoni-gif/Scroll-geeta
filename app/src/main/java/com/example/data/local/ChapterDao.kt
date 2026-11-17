package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Transaction
    @Query("SELECT * FROM chapters ORDER BY chapterNumber ASC")
    fun observeChaptersWithShlokas(): Flow<List<ChapterWithShlokas>>

    @Query("SELECT * FROM chapters ORDER BY chapterNumber ASC")
    fun observeAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun countChapters(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(chapters: List<ChapterEntity>)
}
