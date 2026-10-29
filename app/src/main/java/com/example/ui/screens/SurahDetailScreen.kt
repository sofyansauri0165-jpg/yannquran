package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.model.Ayat
import com.example.data.model.SurahDetail
import com.example.ui.DetailUiState
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    detailState: DetailUiState,
    audioPlaybackState: AudioPlaybackState,
    arabicFontSize: Float,
    showLatin: Boolean,
    showTranslation: Boolean,
    isAyahBookmarked: (Int, Int) -> Boolean,
    onBackClick: () -> Unit,
    onPlayAyah: (Ayat) -> Unit,
    onPlayFullSurah: (SurahDetail) -> Unit,
    onToggleBookmark: (Ayat) -> Unit,
    onSetLastRead: (Int) -> Unit,
    onOpenTafsir: () -> Unit,
    onOpenFontSettings: () -> Unit,
    onNavigateSurah: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (detailState is DetailUiState.Success) {
                        Column {
                            Text(
                                text = "Surat ${detailState.detail.namaLatin}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmWhite
                            )
                            Text(
                                text = "${detailState.detail.arti} • ${detailState.detail.jumlahAyat} Ayat",
                                fontSize = 11.sp,
                                color = GoldLight
                            )
                        }
                    } else {
                        Text("Membaca Al-Qur'an", fontSize = 17.sp, color = WarmWhite)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = WarmWhite
                        )
                    }
                },
                actions = {
                    if (detailState is DetailUiState.Success) {
                        // Play full surah
                        IconButton(
                            onClick = { onPlayFullSurah(detailState.detail) },
                            modifier = Modifier.testTag("play_full_surah_button")
                        ) {
                            Icon(
                                imageVector = if (audioPlaybackState.isPlaying && audioPlaybackState.currentSurahNumber == detailState.detail.nomor && audioPlaybackState.currentAyahNumber == null) {
                                    Icons.Default.PauseCircle
                                } else {
                                    Icons.Default.PlayCircle
                                },
                                contentDescription = "Putar Murottal Surat Lengkap",
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Open Tafsir
                    IconButton(onClick = onOpenTafsir) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Tafsir Kemenag",
                            tint = WarmWhite
                        )
                    }

                    // Font settings
                    IconButton(onClick = onOpenFontSettings) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Ukuran Tulisan & Opsi",
                            tint = WarmWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestDark,
                    titleContentColor = WarmWhite
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (detailState) {
                is DetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = EmeraldPrimary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Menyiapkan ayat suci...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = detailState.message,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Kembali", color = WarmWhite)
                        }
                    }
                }
                is DetailUiState.Success -> {
                    val surah = detailState.detail
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("ayat_list_column"),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        // Surah Header Banner
                        item {
                            SurahMetadataHeader(surah = surah)
                        }

                        // Basmalah (Surah 1 and 9 handled specially)
                        if (surah.nomor != 1 && surah.nomor != 9) {
                            item {
                                BasmalahBanner()
                            }
                        }

                        // Verses List
                        items(surah.ayat, key = { it.nomorAyat }) { ayah ->
                            val isCurrentlyPlaying = audioPlaybackState.isPlaying &&
                                audioPlaybackState.currentSurahNumber == surah.nomor &&
                                audioPlaybackState.currentAyahNumber == ayah.nomorAyat

                            val isSaved = isAyahBookmarked(surah.nomor, ayah.nomorAyat)

                            AyahCard(
                                surahNumber = surah.nomor,
                                surahName = surah.namaLatin,
                                ayah = ayah,
                                arabicFontSize = arabicFontSize,
                                showLatin = showLatin,
                                showTranslation = showTranslation,
                                isPlaying = isCurrentlyPlaying,
                                isBookmarked = isSaved,
                                onPlayAyah = { onPlayAyah(ayah) },
                                onToggleBookmark = { onToggleBookmark(ayah) },
                                onSetLastRead = { onSetLastRead(ayah.nomorAyat) },
                                onCopyAyah = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        "QS ${surah.namaLatin}: ${ayah.nomorAyat}",
                                        "${ayah.teksArab}\n\n\"${ayah.teksIndonesia}\"\n(QS. ${surah.namaLatin}: ${ayah.nomorAyat})"
                                    )
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Ayat disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Bottom Navigation Between Surahs
                        item {
                            SurahBottomNavigationRow(
                                currentNomor = surah.nomor,
                                onNavigateSurah = { targetNomor ->
                                    coroutineScope.launch {
                                        listState.scrollToItem(0)
                                    }
                                    onNavigateSurah(targetNomor)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahMetadataHeader(surah: SurahDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ForestDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(EmeraldPrimary, ForestDark),
                        radius = 800f
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = surah.namaLatin,
                    color = WarmWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = surah.arti,
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(
                    modifier = Modifier.width(140.dp),
                    color = GoldAccent.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = surah.tempatTurun.uppercase(),
                        color = WarmWhite.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = "•", color = GoldAccent)
                    Text(
                        text = "${surah.jumlahAyat} AYAT",
                        color = WarmWhite.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun BasmalahBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = ForestDark,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                fontSize = 22.sp,
                color = GoldAccent,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AyahCard(
    surahNumber: Int,
    surahName: String,
    ayah: Ayat,
    arabicFontSize: Float,
    showLatin: Boolean,
    showTranslation: Boolean,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlayAyah: () -> Unit,
    onToggleBookmark: () -> Unit,
    onSetLastRead: () -> Unit,
    onCopyAyah: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isPlaying) 2.dp else 1.dp,
                color = if (isPlaying) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("ayah_card_${ayah.nomorAyat}"),
        color = if (isPlaying) ActiveAyahBg else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isPlaying) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Verse Toolbar
            Surface(
                color = ForestDark.copy(alpha = 0.06f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Verse Number
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(EmeraldPrimary, CircleShape)
                            .border(1.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ayah.nomorAyat}",
                            color = WarmWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Action Icons Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Play Verse Audio
                        IconButton(
                            onClick = onPlayAyah,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Putar Audio Ayat",
                                tint = if (isPlaying) GoldDark else EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Simpan Bookmark",
                                tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Set Last Read
                        IconButton(
                            onClick = {
                                onSetLastRead()
                                Toast.makeText(context, "Ditandai terakhir dibaca: Ayat ${ayah.nomorAyat}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Tandai Terakhir Dibaca",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Copy
                        IconButton(
                            onClick = onCopyAyah,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin Ayat",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Text (Right-aligned, big & elegant)
            Text(
                text = "${ayah.teksArab} ۝",
                fontSize = arabicFontSize.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                lineHeight = (arabicFontSize * 1.8).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Latin Transliteration
            if (showLatin) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = ayah.teksLatin,
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Indonesian Translation
            if (showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.teksIndonesia,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
fun SurahBottomNavigationRow(
    currentNomor: Int,
    onNavigateSurah: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentNomor > 1) {
            OutlinedButton(
                onClick = { onNavigateSurah(currentNomor - 1) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Surat Sebelumnya")
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        if (currentNomor < 114) {
            Button(
                onClick = { onNavigateSurah(currentNomor + 1) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Surat Selanjutnya", color = WarmWhite)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
