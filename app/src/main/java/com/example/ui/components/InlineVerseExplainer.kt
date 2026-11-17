package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Shloka
import com.example.model.VerseContextBundle
import com.example.model.VerseImportanceTier

private val LuminousGold = Color(0xFFFFD180)
private val WarmIvory = Color(0xFFFAF3E6)
private val MutedIvory = Color(0xFFCBBFA9)
private val GlassCardBg = Color(0x9912172B)
private val GlassSubCardBg = Color(0xCC0D1222)
private val GlassBorder = Color(0x45FFD180)

@Composable
fun InlineVerseExplainer(
    shloka: Shloka,
    contextBundle: VerseContextBundle?,
    isContextLoading: Boolean,
    onJumpToNeighbor: (chapter: Int, verse: Int) -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedScholarIndex by remember(shloka.id) { mutableIntStateOf(0) }

    val tierAccent = when (shloka.importanceTier) {
        VerseImportanceTier.CORE_ESSENCE -> LuminousGold
        VerseImportanceTier.KEY_INSIGHT -> Color(0xFF9EC4FF)
        VerseImportanceTier.SUPPORTING_CONTEXT -> Color(0xFF9BE0B0)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = GlassCardBg,
        border = BorderStroke(1.dp, GlassBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CONTEXT & COMMENTARIES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = LuminousGold
                    )
                    Text(
                        text = "BG ${shloka.chapter}.${shloka.verse} • Surrounding Flow & Classical Scholars",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedIvory
                    )
                }
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier.testTag("close_explainer_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Collapse explainer",
                        tint = WarmIvory
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Importance Rank & Context Dependency Summary
            Surface(
                color = tierAccent.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, tierAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (shloka.requiresAdjacentContext) Icons.Rounded.Link else Icons.Rounded.Star,
                                contentDescription = null,
                                tint = tierAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${shloka.importanceTier.shortBadge} • Rank ${shloka.importanceScore}/100",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = tierAccent
                            )
                        }
                        if (shloka.requiresAdjacentContext) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tierAccent.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "Needs Prev/Next",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tierAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    if (shloka.contextSummary.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = shloka.contextSummary,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                            color = WarmIvory
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Surrounding Previous / Next Verses Flow (Non-AI scriptural context)
            Surface(
                color = GlassSubCardBg,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Surrounding Verse Flow",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = LuminousGold
                        )
                        if (isContextLoading) {
                            CircularProgressIndicator(
                                color = LuminousGold,
                                modifier = Modifier.size(15.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val prev = contextBundle?.previousShloka
                    if (prev != null) {
                        InlineNeighborCard(
                            tag = "PREVIOUS • BG ${prev.chapter}.${prev.verse}",
                            speaker = prev.speakerContext,
                            englishSnippet = prev.english,
                            hindiSnippet = prev.hindi,
                            onClick = { onJumpToNeighbor(prev.chapter, prev.verse) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else if (!isContextLoading && shloka.chapter == 1 && shloka.verse == 1) {
                        Text(
                            text = "This is the opening verse of the Bhagavad Gita.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedIvory
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    val next = contextBundle?.nextShloka
                    if (next != null) {
                        InlineNeighborCard(
                            tag = "NEXT • BG ${next.chapter}.${next.verse}",
                            speaker = next.speakerContext,
                            englishSnippet = next.english,
                            hindiSnippet = next.hindi,
                            onClick = { onJumpToNeighbor(next.chapter, next.verse) }
                        )
                    } else if (isContextLoading) {
                        Text(
                            text = "Loading adjacent verses for complete dialogue flow...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedIvory
                        )
                    } else if (shloka.chapter == 18 && shloka.verse == 78) {
                        Text(
                            text = "This is the concluding benediction of the Bhagavad Gita.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedIvory
                        )
                    }
                }
            }

            if (shloka.explainers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.14f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.HistoryEdu,
                        contentDescription = null,
                        tint = LuminousGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Classical Explainers (${shloka.explainers.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = WarmIvory
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                val safeIndex = selectedScholarIndex.coerceIn(0, shloka.explainers.lastIndex)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    itemsIndexed(shloka.explainers) { idx, perspective ->
                        val selected = idx == safeIndex
                        FilterChip(
                            selected = selected,
                            onClick = { selectedScholarIndex = idx },
                            label = {
                                Text(
                                    text = "${perspective.scholarName} (${perspective.languageLabel})",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = WarmIvory,
                                selectedContainerColor = LuminousGold,
                                selectedLabelColor = Color(0xFF1A1205)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = Color.White.copy(alpha = 0.22f)
                            ),
                            modifier = Modifier.testTag("scholar_chip_$idx")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                val currentPerspective = shloka.explainers[safeIndex]
                Surface(
                    color = GlassSubCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${currentPerspective.scholarName} • ${currentPerspective.tradition}",
                            style = MaterialTheme.typography.labelLarge,
                            color = LuminousGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentPerspective.content,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = WarmIvory
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InlineReflectionPanel(
    chapter: Int,
    verse: Int,
    onSave: (noteText: String, moodTag: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moodOptions = listOf("Peace", "Clarity", "Devotion", "Gratitude", "Strength")
    var noteText by remember(chapter, verse) { mutableStateOf("") }
    var selectedMood by remember(chapter, verse) { mutableStateOf("Peace") }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = GlassCardBg,
        border = BorderStroke(1.dp, GlassBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERSONAL REFLECTION • BG $chapter.$verse",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = LuminousGold
                )
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close reflection",
                        tint = WarmIvory
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(moodOptions) { mood ->
                    val selected = mood == selectedMood
                    FilterChip(
                        selected = selected,
                        onClick = { selectedMood = mood },
                        label = { Text(mood, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = WarmIvory,
                            selectedContainerColor = LuminousGold,
                            selectedLabelColor = Color(0xFF1A1205)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = Color.White.copy(alpha = 0.22f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("What insight does this shloka awaken in you?", color = MutedIvory) },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = WarmIvory,
                    unfocusedTextColor = WarmIvory,
                    focusedBorderColor = LuminousGold,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                    focusedContainerColor = GlassSubCardBg,
                    unfocusedContainerColor = GlassSubCardBg,
                    cursorColor = LuminousGold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inline_reflection_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Text("Cancel", color = WarmIvory)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            onSave(noteText.trim(), selectedMood)
                        }
                    },
                    enabled = noteText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuminousGold,
                        contentColor = Color(0xFF1A1205)
                    ),
                    modifier = Modifier.testTag("save_reflection_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Note")
                }
            }
        }
    }
}

@Composable
private fun InlineNeighborCard(
    tag: String,
    speaker: String,
    englishSnippet: String,
    hindiSnippet: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$tag • $speaker",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = LuminousGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = englishSnippet,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmIvory.copy(alpha = 0.92f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (hindiSnippet.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hindiSnippet,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedIvory,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = "Open $tag",
                tint = LuminousGold,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
