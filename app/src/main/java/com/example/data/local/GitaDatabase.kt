package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ChapterEntity::class, ShlokaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GitaDatabase : RoomDatabase() {
    abstract fun chapterDao(): ChapterDao
    abstract fun shlokaDao(): ShlokaDao

    companion object {
        @Volatile
        private var INSTANCE: GitaDatabase? = null

        fun getInstance(context: Context): GitaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GitaDatabase::class.java,
                    "geeta_scroll_cache.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
