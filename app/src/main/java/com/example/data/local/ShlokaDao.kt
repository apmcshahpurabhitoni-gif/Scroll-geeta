package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShlokaDao {
    @Query("SELECT * FROM shlokas WHERE chapter = :chapter AND verse = :verse LIMIT 1")
    suspend fun getShloka(chapter: Int, verse: Int): ShlokaEntity?

    @Query("SELECT * FROM shlokas WHERE chapter = :chapter ORDER BY verse ASC")
    fun observeShlokasByChapter(chapter: Int): Flow<List<ShlokaEntity>>

    @Query("SELECT * FROM shlokas WHERE chapter = :chapter ORDER BY importanceScore DESC, verse ASC")
    fun observeShlokasByChapterRanked(chapter: Int): Flow<List<ShlokaEntity>>

    @Query(
        """
        SELECT * FROM shlokas
        WHERE sanskrit LIKE '%' || :query || '%'
           OR transliteration LIKE '%' || :query || '%'
           OR hindi LIKE '%' || :query || '%'
           OR english LIKE '%' || :query || '%'
           OR explainersJson LIKE '%' || :query || '%'
           OR speakerContext LIKE '%' || :query || '%'
           OR contextSummary LIKE '%' || :query || '%'
        ORDER BY importanceScore DESC, chapter ASC, verse ASC
        LIMIT 80
        """
    )
    suspend fun searchShlokas(query: String): List<ShlokaEntity>

    @Query("SELECT COUNT(*) FROM shlokas")
    suspend fun countCached(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShloka(shloka: ShlokaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(shlokas: List<ShlokaEntity>)
}
