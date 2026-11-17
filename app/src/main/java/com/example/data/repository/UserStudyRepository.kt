package com.example.data.repository

import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.example.model.MOOD_TAGS
import com.example.model.ReadVerseRecord
import com.example.model.ReadingProgress
import com.example.model.Shloka
import com.example.model.VerseBookmark
import com.example.model.VerseReflection
import com.example.model.verseKey
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserStudyRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    private fun requireAuthorizedUserId(expectedUserId: String): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
        check(uid == expectedUserId) { "Signed-in user changed; refusing cross-user access." }
        return uid
    }

    fun observeBookmarks(userId: String): Flow<List<VerseBookmark>> =
        db.collection("users").document(userId).collection("bookmarks")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    VerseBookmark::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt?.seconds ?: 0L }
            }

    fun observeReflections(userId: String): Flow<List<VerseReflection>> =
        db.collection("users").document(userId).collection("reflections")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    VerseReflection::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.updatedAt?.seconds ?: it.createdAt?.seconds ?: 0L }
            }

    fun observeProgress(userId: String): Flow<ReadingProgress?> =
        db.collection("users").document(userId).collection("progress")
            .document("summary")
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        ReadingProgress::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }

    fun observeReadVerses(userId: String): Flow<List<ReadVerseRecord>> =
        db.collection("users").document(userId).collection("read_verses")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    ReadVerseRecord::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }

    suspend fun toggleBookmark(
        userId: String,
        shloka: Shloka,
        chapterTitle: String,
        currentlyBookmarked: Boolean
    ): Result<Unit> {
        val verseId = verseKey(shloka.chapter, shloka.verse)
        val path = "users/$userId/bookmarks/$verseId"
        return try {
            val uid = requireAuthorizedUserId(userId)
            val docRef = db.collection("users").document(uid).collection("bookmarks").document(verseId)
            if (currentlyBookmarked) {
                docRef.delete().await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "verseId" to verseId,
                    "chapter" to shloka.chapter.toLong(),
                    "verse" to shloka.verse.toLong(),
                    "chapterTitle" to chapterTitle.trim().ifBlank { "Chapter ${shloka.chapter}" }.take(120),
                    "sanskritPreview" to shloka.sanskrit.trim().ifBlank { "श्लोक" }.take(600),
                    "hindiPreview" to shloka.hindi.trim().ifBlank { "हिन्दी अर्थ" }.take(600),
                    "englishPreview" to shloka.english.trim().ifBlank { "English translation" }.take(600),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(
                e,
                if (currentlyBookmarked) OperationType.DELETE else OperationType.CREATE,
                path
            )
            Result.failure(e)
        }
    }

    suspend fun saveReflection(
        userId: String,
        shloka: Shloka,
        noteText: String,
        moodTag: String,
        existingReflectionId: String? = null
    ): Result<Unit> {
        val trimmedNote = noteText.trim()
        if (trimmedNote.isEmpty() || trimmedNote.length > 2000) {
            return Result.failure(IllegalArgumentException("Reflection must be between 1 and 2000 characters."))
        }
        val safeMood = if (moodTag in MOOD_TAGS) moodTag else "Clarity"
        val verseId = verseKey(shloka.chapter, shloka.verse)
        val colRef = db.collection("users").document(userId).collection("reflections")
        val docRef = if (!existingReflectionId.isNullOrBlank()) {
            colRef.document(existingReflectionId)
        } else {
            colRef.document()
        }
        val path = docRef.path
        val isUpdate = !existingReflectionId.isNullOrBlank()
        return try {
            val uid = requireAuthorizedUserId(userId)
            if (isUpdate) {
                docRef.update(
                    mapOf(
                        "noteText" to trimmedNote,
                        "moodTag" to safeMood,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "verseId" to verseId,
                        "chapter" to shloka.chapter.toLong(),
                        "verse" to shloka.verse.toLong(),
                        "noteText" to trimmedNote,
                        "moodTag" to safeMood,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(
                e,
                if (isUpdate) OperationType.UPDATE else OperationType.CREATE,
                path
            )
            Result.failure(e)
        }
    }

    suspend fun deleteReflection(userId: String, reflectionId: String): Result<Unit> {
        val path = "users/$userId/reflections/$reflectionId"
        return try {
            val uid = requireAuthorizedUserId(userId)
            require(reflectionId.isNotBlank()) { "Reflection ID cannot be blank" }
            db.collection("users").document(uid).collection("reflections")
                .document(reflectionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun recordVerseVisited(userId: String, chapter: Int, verse: Int): Result<Unit> {
        val path = "users/$userId/progress/summary"
        return try {
            val uid = requireAuthorizedUserId(userId)
            val docRef = db.collection("users").document(uid).collection("progress").document("summary")
            val ch = chapter.coerceIn(1, 18).toLong()
            val vs = verse.coerceIn(1, 80).toLong()
            db.runTransaction { tx ->
                val snap = tx.get(docRef)
                if (!snap.exists()) {
                    tx.set(
                        docRef,
                        mapOf(
                            "userId" to uid,
                            "lastChapter" to ch,
                            "lastVerse" to vs,
                            "currentStreak" to 0L,
                            "longestStreak" to 0L,
                            "totalVersesRead" to 0L,
                            "lastReadDate" to "",
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    tx.update(
                        docRef,
                        mapOf(
                            "lastChapter" to ch,
                            "lastVerse" to vs,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun markVerseRead(userId: String, chapter: Int, verse: Int): Result<Unit> {
        val verseId = verseKey(chapter, verse)
        val path = "users/$userId/read_verses/$verseId"
        return try {
            val uid = requireAuthorizedUserId(userId)
            val readRef = db.collection("users").document(uid).collection("read_verses").document(verseId)
            val progressRef = db.collection("users").document(uid).collection("progress").document("summary")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayCal = Calendar.getInstance()
            val todayStr = dateFormat.format(todayCal.time)
            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterdayStr = dateFormat.format(yesterdayCal.time)
            val ch = chapter.coerceIn(1, 18).toLong()
            val vs = verse.coerceIn(1, 80).toLong()

            db.runTransaction { tx ->
                val readSnap = tx.get(readRef)
                val progressSnap = tx.get(progressRef)

                val alreadyRead = readSnap.exists()
                if (!alreadyRead) {
                    tx.set(
                        readRef,
                        mapOf(
                            "userId" to uid,
                            "verseId" to verseId,
                            "chapter" to ch,
                            "verse" to vs,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                if (!progressSnap.exists()) {
                    tx.set(
                        progressRef,
                        mapOf(
                            "userId" to uid,
                            "lastChapter" to ch,
                            "lastVerse" to vs,
                            "currentStreak" to 1L,
                            "longestStreak" to 1L,
                            "totalVersesRead" to if (alreadyRead) 0L else 1L,
                            "lastReadDate" to todayStr,
                            "createdAt" to FieldValue.serverTimestamp(),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                } else {
                    val prevDateStr = progressSnap.getString("lastReadDate").orEmpty()
                    val prevStreak = progressSnap.getLong("currentStreak") ?: 0L
                    val prevLongest = progressSnap.getLong("longestStreak") ?: 0L
                    val prevTotal = progressSnap.getLong("totalVersesRead") ?: 0L

                    val newStreak = when (prevDateStr) {
                        todayStr -> prevStreak.coerceAtLeast(1L)
                        yesterdayStr -> prevStreak + 1L
                        else -> 1L
                    }
                    val newLongest = maxOf(prevLongest, newStreak)
                    val newTotal = (if (alreadyRead) prevTotal else prevTotal + 1L).coerceAtMost(701L)

                    tx.update(
                        progressRef,
                        mapOf(
                            "lastChapter" to ch,
                            "lastVerse" to vs,
                            "currentStreak" to newStreak,
                            "longestStreak" to newLongest,
                            "totalVersesRead" to newTotal,
                            "lastReadDate" to todayStr,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    )
                }
            }.await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }
}
