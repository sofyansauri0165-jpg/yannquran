package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DoaDzikirData
import com.example.data.local.DoaItem
import com.example.ui.theme.*

@Composable
fun DoaDzikirScreen(
    tasbihCount: Int,
    tasbihTarget: Int,
    onIncrementTasbih: () -> Unit,
    onResetTasbih: () -> Unit,
    onSetTasbihTarget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Harian") } // "Harian", "Shalat", "Pagi", "Petang", "Tasbih"

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("doa_dzikir_screen")
    ) {
        // Category Scrollable / Flow Row
        ScrollableTabRow(
            selectedTabIndex = when (selectedCategory) {
                "Harian" -> 0
                "Shalat" -> 1
                "Pagi" -> 2
                "Petang" -> 3
                else -> 4
            },
            containerColor = ForestDark,
            contentColor = WarmWhite,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                val tabIdx = when (selectedCategory) {
                    "Harian" -> 0
                    "Shalat" -> 1
                    "Pagi" -> 2
                    "Petang" -> 3
                    else -> 4
                }
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                    color = GoldAccent
                )
            }
        ) {
            listOf("Harian" to "Doa Harian", "Shalat" to "Dzikir Shalat", "Pagi" to "Dzikir Pagi", "Petang" to "Dzikir Petang", "Tasbih" to "Tasbih Digital").forEach { (key, label) ->
                val isSelected = selectedCategory == key
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategory = key },
                    text = {
                        Text(
                            label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GoldAccent else WarmWhite.copy(alpha = 0.75f)
                        )
                    }
                )
            }
        }

        if (selectedCategory == "Tasbih") {
            TasbihCounterTab(
                count = tasbihCount,
                target = tasbihTarget,
                onIncrement = onIncrementTasbih,
                onReset = onResetTasbih,
                onSetTarget = onSetTasbihTarget
            )
        } else {
            val items = remember(selectedCategory) {
                DoaDzikirData.ALL_DOA.filter { it.category == selectedCategory }
            }
            DoaListTab(items = items)
        }
    }
}

@Composable
fun DoaListTab(items: List<DoaItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items, key = { it.id }) { doa ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = doa.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (doa.count > 1) {
                            Surface(
                                color = GoldAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${doa.count}x",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = doa.arab,
                        fontSize = 20.sp,
                        textAlign = TextAlign.End,
                        lineHeight = 36.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = doa.latin,
                        fontSize = 12.sp,
                        color = EmeraldPrimary,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = doa.translation,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 19.sp
                    )

                    if (doa.source.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doa.source,
                            fontSize = 11.sp,
                            color = GoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TasbihCounterTab(
    count: Int,
    target: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSetTarget: (Int) -> Unit
) {
    val progress = (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "tasbih_progress")

    var selectedDzikir by remember { mutableStateOf("Subḥānallāh") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            // Target Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(33, 99, 100).forEach { tgt ->
                    FilterChip(
                        selected = target == tgt,
                        onClick = { onSetTarget(tgt) },
                        label = { Text("Target $tgt", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = WarmWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Current Dzikir Phrase
            Surface(
                color = ForestDark,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val arabText = when (selectedDzikir) {
                        "Subḥānallāh" -> "سُبْحَانَ اللَّهِ"
                        "Al-ḥamdulillāh" -> "الْحَمْدُ لِلَّهِ"
                        "Allāhu Akbar" -> "اللَّهُ أَكْبَرُ"
                        "Astaghfirullāh" -> "أَسْتَغْفِرُ اللَّهَ"
                        else -> "لَا إِلٰهَ إِلَّا اللَّهُ"
                    }
                    Text(
                        text = arabText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedDzikir,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = WarmWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dzikir selector chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Subḥānallāh", "Al-ḥamdulillāh", "Allāhu Akbar").forEach { dz ->
                    TextButton(
                        onClick = { selectedDzikir = dz },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (selectedDzikir == dz) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(dz, fontSize = 11.sp, fontWeight = if (selectedDzikir == dz) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        // Giant Interactive Counter Button
        Box(
            modifier = Modifier
                .size(230.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(EmeraldPrimary, ForestDark)
                    )
                )
                .border(4.dp, GoldAccent, CircleShape)
                .clickable { onIncrement() }
                .testTag("tasbih_tap_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$count",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmWhite
                )
                Text(
                    text = "DARI $target",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = GoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "SENTUH UNTUK MENGHITUNG",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Reset and Vibration Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Vibration, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Getaran Haptic Aktif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            OutlinedButton(
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Hitungan", fontSize = 12.sp)
            }
        }
    }
}
