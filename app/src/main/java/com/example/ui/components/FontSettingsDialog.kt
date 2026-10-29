package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reciter
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestDark
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.WarmWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSettingsBottomSheet(
    currentFontSize: Float,
    showLatin: Boolean,
    showTranslation: Boolean,
    selectedReciter: String,
    onFontSizeChange: (Float) -> Unit,
    onToggleLatin: () -> Unit,
    onToggleTranslation: () -> Unit,
    onSelectReciter: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = GoldAccent) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatSize,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Pengaturan Tampilan & Audio",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Preview Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = ForestDark,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = currentFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        color = GoldAccent,
                        textAlign = TextAlign.Center,
                        lineHeight = (currentFontSize * 1.6).sp
                    )
                    if (showLatin) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bismillāhir-raḥmānir-raḥīm",
                            fontSize = 12.sp,
                            color = WarmWhite.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                    if (showTranslation) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dengan nama Allah Yang Maha Pengasih, Maha Penyayang.",
                            fontSize = 12.sp,
                            color = WarmWhite.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Size Controls
            Text(
                text = "Ukuran Tulisan Arab: ${currentFontSize.toInt()} sp",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Slider(
                value = currentFontSize,
                onValueChange = onFontSizeChange,
                valueRange = 20f..38f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = GoldAccent,
                    activeTrackColor = EmeraldPrimary
                ),
                modifier = Modifier.testTag("font_size_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(22f to "Kecil", 26f to "Sedang", 32f to "Besar", 38f to "Ekstra").forEach { (size, label) ->
                    FilterChip(
                        selected = currentFontSize == size,
                        onClick = { onFontSizeChange(size) },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Switches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleLatin() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Teks Transliterasi Latin", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Membantu pelafalan tajwid", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = showLatin,
                    onCheckedChange = { onToggleLatin() },
                    colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldPrimary)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleTranslation() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Terjemahan Kemenag RI", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Bahasa Indonesia resmi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = showTranslation,
                    onCheckedChange = { onToggleTranslation() },
                    colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldPrimary)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Reciter Selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(imageVector = Icons.Default.Headphones, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pilihan Qari Audio Murottal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Reciter.ALL.forEach { reciter ->
                val isSelected = selectedReciter == reciter.key
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelectReciter(reciter.key) },
                    color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(reciter.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(reciter.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Terapkan Pengaturan", color = WarmWhite)
            }
        }
    }
}
