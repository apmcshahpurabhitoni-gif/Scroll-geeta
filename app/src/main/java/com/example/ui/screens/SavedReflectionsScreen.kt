package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.Shloka
import com.example.model.VerseBookmark
import com.example.model.VerseReflection
import com.example.model.KrishnaScene
import com.example.ui.components.ReflectionEditorDialog
import com.example.ui.viewmodel.UiState

@Composable
fun SavedReflectionsScreen(
    bookmarksState: UiState<List<VerseBookmark>>,
    reflectionsState: UiState<List<VerseReflection>>,
    onOpenVerse: (chapter: Int, verse: Int) -> Unit,
    onRemoveBookmark: (VerseBookmark) -> Unit,
    onUpdateReflection: (VerseReflection, String, String) -> Unit,
    onDeleteReflection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var editingReflection by remember { mutableStateOf<VerseReflection?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Saved & Reflections",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    modifier = Modifier.testTag("tab_bookmarks")
                ) {
                    Text("Bookmarks")
                }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    modifier = Modifier.testTag("tab_reflections")
                ) {
                    Text("Reflections")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Search bookmarked verses..."
                        else "Search reflection notes..."
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saved_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                when (bookmarksState) {
                    UiState.Loading -> LoadingPlaceholder()
                    is UiState.Error -> ErrorPlaceholder(bookmarksState.message)
                    is UiState.Success -> {
                        val query = searchQuery.trim().lowercase()
                        val filtered = bookmarksState.data.filter { bm ->
                            query.isEmpty() ||
                                bm.chapterTitle.lowercase().contains(query) ||
                                bm.englishPreview.lowercase().contains(query) ||
                                bm.hindiPreview.lowercase().contains(query) ||
                                "${bm.chapter}.${bm.verse}".contains(query)
                        }
                        if (filtered.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Rounded.Bookmarks,
                                title = "No Bookmarked Shlokas Yet",
                                subtitle = "Tap the bookmark icon on any shloka while scrolling to keep it close."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filtered, key = { it.verseId }) { bookmark ->
                                    BookmarkCard(
                                        bookmark = bookmark,
                                        onClick = {
                                            onOpenVerse(bookmark.chapter.toInt(), bookmark.verse.toInt())
                                        },
                                        onRemove = { onRemoveBookmark(bookmark) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                when (reflectionsState) {
                    UiState.Loading -> LoadingPlaceholder()
                    is UiState.Error -> ErrorPlaceholder(reflectionsState.message)
                    is UiState.Success -> {
                        val query = searchQuery.trim().lowercase()
                        val filtered = reflectionsState.data.filter { note ->
                            query.isEmpty() ||
                                note.noteText.lowercase().contains(query) ||
                                note.moodTag.lowercase().contains(query) ||
                                "${note.chapter}.${note.verse}".contains(query)
                        }
                        if (filtered.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Rounded.EditNote,
                                title = "No Personal Reflections Yet",
                                subtitle = "Tap the note icon on any shloka to capture insights and intentions."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filtered, key = { it.id }) { reflection ->
                                    ReflectionCard(
                                        reflection = reflection,
                                        onOpenVerse = {
                                            onOpenVerse(reflection.chapter.toInt(), reflection.verse.toInt())
                                        },
                                        onEdit = { editingReflection = reflection },
                                        onDelete = { onDeleteReflection(reflection.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editingReflection?.let { target ->
        ReflectionEditorDialog(
            chapter = target.chapter.toInt(),
            verse = target.verse.toInt(),
            initialText = target.noteText,
            initialMood = target.moodTag,
            isEditing = true,
            onSave = { updatedText, updatedMood ->
                onUpdateReflection(target, updatedText, updatedMood)
                editingReflection = null
            },
            onDismiss = { editingReflection = null }
        )
    }
}

@Composable
private fun BookmarkCard(
    bookmark: VerseBookmark,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("bookmark_card_${bookmark.verseId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BG ${bookmark.chapter}.${bookmark.verse} • ${bookmark.chapterTitle}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkRemove,
                        contentDescription = "Remove bookmark"
                    )
                }
            }
            Text(
                text = bookmark.sanskritPreview,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = bookmark.englishPreview,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ReflectionCard(
    reflection: VerseReflection,
    onOpenVerse: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenVerse)
            .testTag("reflection_card_${reflection.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "BG ${reflection.chapter}.${reflection.verse}",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = reflection.moodTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit reflection"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Delete reflection"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reflection.noteText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorPlaceholder(message: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
