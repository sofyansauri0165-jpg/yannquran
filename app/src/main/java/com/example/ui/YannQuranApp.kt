package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AudioMiniPlayer
import com.example.ui.components.FontSettingsBottomSheet
import com.example.ui.components.QuranTopBar
import com.example.ui.components.TafsirBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    QURAN("Al-Qur'an", Icons.Filled.AutoStories, Icons.Outlined.AutoStories, "tab_quran"),
    PRAYER("Jadwal Salat", Icons.Filled.Schedule, Icons.Outlined.Schedule, "tab_prayer"),
    QIBLA("Kiblat", Icons.Filled.Explore, Icons.Outlined.Explore, "tab_qibla"),
    YASIN("Yasin & Tahlil", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "tab_yasin"),
    DOA("Doa & Dzikir", Icons.Filled.Spa, Icons.Outlined.Spa, "tab_doa")
}

sealed interface CurrentScreen {
    data class MainTabs(val activeTab: AppTab = AppTab.QURAN) : CurrentScreen
    data class SurahDetail(val surahNumber: Int, val targetAyah: Int = 1) : CurrentScreen
    data object Bookmarks : CurrentScreen
}

@Composable
fun YannQuranApp(
    viewModel: QuranViewModel,
    modifier: Modifier = Modifier
) {
    var screenState by remember { mutableStateOf<CurrentScreen>(CurrentScreen.MainTabs()) }
    var showFontSettings by remember { mutableStateOf(false) }
    var forceDesktopMode by remember { mutableStateOf(false) }
    var desktopSelectedSurah by remember { mutableIntStateOf(1) }

    val surahs by viewModel.surahs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val lastRead by viewModel.lastRead.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val tafsirState by viewModel.tafsirState.collectAsStateWithLifecycle()
    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val showLatin by viewModel.showLatin.collectAsStateWithLifecycle()
    val showTranslation by viewModel.showTranslation.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val audioState by viewModel.audioPlaybackState.collectAsStateWithLifecycle()
    val prayerSchedule by viewModel.prayerSchedule.collectAsStateWithLifecycle()
    val selectedCityIndex by viewModel.selectedCityIndex.collectAsStateWithLifecycle()
    val qiblaInfo by viewModel.qiblaInfo.collectAsStateWithLifecycle()
    val tasbihCount by viewModel.tasbihCount.collectAsStateWithLifecycle()
    val tasbihTarget by viewModel.tasbihTarget.collectAsStateWithLifecycle()
    val useGpsLocation by viewModel.useGpsLocation.collectAsStateWithLifecycle()
    val isGpsLoading by viewModel.isGpsLoading.collectAsStateWithLifecycle()
    val gpsLocationName by viewModel.gpsLocationName.collectAsStateWithLifecycle()
    val isPrayerAudioEnabled by viewModel.isPrayerAudioEnabled.collectAsStateWithLifecycle()
    val prayerAudioVolume by viewModel.prayerAudioVolume.collectAsStateWithLifecycle()
    val reminderType by viewModel.reminderType.collectAsStateWithLifecycle()
    val prayerAudioState by viewModel.prayerAudioState.collectAsStateWithLifecycle()

    // Ensure initial surah detail is loaded for desktop dual pane
    LaunchedEffect(Unit) {
        viewModel.loadSurahDetail(desktopSelectedSurah)
    }

    // Handle Back Press
    BackHandler(enabled = screenState !is CurrentScreen.MainTabs) {
        screenState = CurrentScreen.MainTabs()
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 840.dp
        val isDesktopMode = isWideScreen || forceDesktopMode

        if (isDesktopMode) {
            // ==================== DESKTOP MODE (SIDE NAVRAIL & DUAL PANE) ====================
            val currentTab = when (val s = screenState) {
                is CurrentScreen.MainTabs -> s.activeTab
                else -> AppTab.QURAN
            }

            Row(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Desktop Side Navigation Rail
                NavigationRail(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight(),
                    containerColor = ForestDark,
                    contentColor = WarmWhite,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                                    .border(1.5.dp, GoldAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = "Yann Quran",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Yann Quran",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }
                    }
                ) {
                    AppTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = {
                                screenState = CurrentScreen.MainTabs(tab)
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = ForestDark,
                                selectedTextColor = GoldAccent,
                                indicatorColor = GoldAccent,
                                unselectedIconColor = WarmWhite.copy(alpha = 0.65f),
                                unselectedTextColor = WarmWhite.copy(alpha = 0.65f)
                            ),
                            modifier = Modifier.testTag("nav_rail_${tab.testTag}")
                        )
                    }
                }

                VerticalDivider(color = GoldAccent.copy(alpha = 0.3f), thickness = 1.dp)

                // Main Desktop Workspace
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Top Bar with Desktop Indicator
                    QuranTopBar(
                        title = "Yann Quran",
                        subtitle = when (currentTab) {
                            AppTab.QURAN -> "Mode Desktop • Dual-Pane Reading & Audio"
                            AppTab.PRAYER -> "Jadwal Salat & Waktu Adzan Otomatis"
                            AppTab.QIBLA -> "Penunjuk Arah Kiblat ke Ka'bah"
                            AppTab.YASIN -> "Bacaan Yasin, Tahlil & Doa Arwah"
                            AppTab.DOA -> "Kumpulan Doa, Dzikir & Tasbih Digital"
                        },
                        isDesktopMode = true,
                        onToggleDesktopMode = { forceDesktopMode = !forceDesktopMode },
                        onBookmarkClick = { screenState = CurrentScreen.Bookmarks }
                    )

                    // Desktop Dual-Pane Content Area
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (currentTab) {
                            AppTab.QURAN -> {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    // Left Pane: Surah List (40% width)
                                    Box(modifier = Modifier.weight(0.40f).fillMaxHeight()) {
                                        HomeScreen(
                                            surahs = surahs,
                                            searchQuery = searchQuery,
                                            selectedFilter = selectedFilter,
                                            lastRead = lastRead,
                                            selectedSurahNomor = desktopSelectedSurah,
                                            isDesktopDualPane = true,
                                            onSearchChange = { viewModel.onSearchQueryChanged(it) },
                                            onFilterSelect = { viewModel.onFilterSelected(it) },
                                            onSurahClick = { surahNumber ->
                                                desktopSelectedSurah = surahNumber
                                                viewModel.loadSurahDetail(surahNumber)
                                            },
                                            onContinueReading = { surahNumber, _ ->
                                                desktopSelectedSurah = surahNumber
                                                viewModel.loadSurahDetail(surahNumber)
                                            }
                                        )
                                    }

                                    VerticalDivider(color = GoldAccent.copy(alpha = 0.25f), thickness = 1.dp)

                                    // Right Pane: Active Surah Reader (60% width)
                                    Box(modifier = Modifier.weight(0.60f).fillMaxHeight()) {
                                        SurahDetailScreen(
                                            detailState = detailState,
                                            audioPlaybackState = audioState,
                                            arabicFontSize = arabicFontSize,
                                            showLatin = showLatin,
                                            showTranslation = showTranslation,
                                            isAyahBookmarked = { s, a -> viewModel.isAyahBookmarked(s, a) },
                                            onBackClick = { /* Stays in dual-pane */ },
                                            onPlayAyah = { ayah ->
                                                val viewing = viewModel.currentViewingSurah.value
                                                if (viewing != null) {
                                                    viewModel.playAyahAudio(viewing.nomor, viewing.namaLatin, ayah)
                                                }
                                            },
                                            onPlayFullSurah = { surahDetail ->
                                                viewModel.playFullSurahAudio(surahDetail)
                                            },
                                            onToggleBookmark = { ayah ->
                                                val viewing = viewModel.currentViewingSurah.value
                                                if (viewing != null) {
                                                    viewModel.toggleBookmark(viewing.nomor, viewing.namaLatin, ayah)
                                                }
                                            },
                                            onSetLastRead = { ayahNum ->
                                                val viewing = viewModel.currentViewingSurah.value
                                                if (viewing != null) {
                                                    viewModel.setLastRead(viewing.nomor, viewing.namaLatin, ayahNum, viewing.jumlahAyat)
                                                }
                                            },
                                            onOpenTafsir = { viewModel.loadTafsir(desktopSelectedSurah) },
                                            onOpenFontSettings = { showFontSettings = true },
                                            onNavigateSurah = { nextNomor ->
                                                desktopSelectedSurah = nextNomor
                                                viewModel.loadSurahDetail(nextNomor)
                                            }
                                        )
                                    }
                                }
                            }

                            AppTab.PRAYER -> {
                                PrayerTimesScreen(
                                    schedule = prayerSchedule,
                                    selectedCityIndex = selectedCityIndex,
                                    useGpsLocation = useGpsLocation,
                                    isGpsLoading = isGpsLoading,
                                    gpsLocationName = gpsLocationName,
                                    isPrayerAudioEnabled = isPrayerAudioEnabled,
                                    prayerAudioVolume = prayerAudioVolume,
                                    reminderType = reminderType,
                                    prayerAudioState = prayerAudioState,
                                    isPrayerAudioActive = { viewModel.isPrayerAudioActive(it) },
                                    onSelectCity = { viewModel.selectCity(it) },
                                    onRequestGpsLocation = { viewModel.requestGpsLocation() },
                                    onToggleUseGps = { viewModel.setUseGpsLocation(it) },
                                    onToggleMasterAudio = { viewModel.togglePrayerAudioMaster(it) },
                                    onSetAudioVolume = { viewModel.setPrayerAudioVolume(it) },
                                    onSetPrayerAudioActive = { name, active -> viewModel.setPrayerAudioActive(name, active) },
                                    onTestAudio = { viewModel.testPrayerAudio(it) },
                                    onStopAudio = { viewModel.stopPrayerAudio() }
                                )
                            }

                            AppTab.QIBLA -> {
                                QiblaScreen(
                                    qiblaInfo = qiblaInfo,
                                    compassManager = viewModel.compassManager
                                )
                            }

                            AppTab.YASIN -> {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                        YasinReadingTab(onOpenFullYasin = {
                                            desktopSelectedSurah = 36
                                            viewModel.loadSurahDetail(36)
                                            screenState = CurrentScreen.MainTabs(AppTab.QURAN)
                                        })
                                    }
                                    VerticalDivider(color = GoldAccent.copy(alpha = 0.25f), thickness = 1.dp)
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                        TahlilReadingTab()
                                    }
                                }
                            }

                            AppTab.DOA -> {
                                DoaDzikirScreen(
                                    tasbihCount = tasbihCount,
                                    tasbihTarget = tasbihTarget,
                                    onIncrementTasbih = { viewModel.incrementTasbih() },
                                    onResetTasbih = { viewModel.resetTasbih() },
                                    onSetTasbihTarget = { viewModel.setTasbihTarget(it) }
                                )
                            }
                        }
                    }

                    // Desktop Docked Audio Bar at the bottom
                    AudioMiniPlayer(
                        state = audioState,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onStop = { viewModel.stopAudio() }
                    )
                }
            }
        } else {
            // ==================== MOBILE MODE (FLOATING NAVBAR & FAB) ====================
            Scaffold(
                floatingActionButton = {
                    if (screenState is CurrentScreen.MainTabs && (screenState as CurrentScreen.MainTabs).activeTab == AppTab.QURAN) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                if (lastRead != null) {
                                    viewModel.loadSurahDetail(lastRead!!.surahNumber)
                                    screenState = CurrentScreen.SurahDetail(lastRead!!.surahNumber, lastRead!!.ayahNumber)
                                } else {
                                    viewModel.loadSurahDetail(1)
                                    screenState = CurrentScreen.SurahDetail(1, 1)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Lanjut Tilawah",
                                    tint = ForestDark
                                )
                            },
                            text = {
                                Text(
                                    text = if (lastRead != null) "Lanjut: ${lastRead!!.surahName}" else "Mulai Tilawah",
                                    color = ForestDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            },
                            containerColor = GoldAccent,
                            contentColor = ForestDark,
                            shape = RoundedCornerShape(22.dp),
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .testTag("floating_quick_read_fab")
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    if (screenState is CurrentScreen.MainTabs) {
                        val currentTab = (screenState as CurrentScreen.MainTabs).activeTab
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        ) {
                            // Floating Audio Mini Player
                            AudioMiniPlayer(
                                state = audioState,
                                onTogglePlayPause = { viewModel.togglePlayPause() },
                                onStop = { viewModel.stopAudio() }
                            )

                            // Modern Floating Navbar with rounded corners and gold border
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(26.dp),
                                color = ForestDark,
                                shadowElevation = 10.dp,
                                border = BorderStroke(1.2.dp, GoldAccent.copy(alpha = 0.5f))
                            ) {
                                NavigationBar(
                                    containerColor = Color.Transparent,
                                    tonalElevation = 0.dp,
                                    windowInsets = WindowInsets(0.dp),
                                    modifier = Modifier.testTag("main_bottom_nav")
                                ) {
                                    AppTab.entries.forEach { tab ->
                                        val isSelected = currentTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                screenState = CurrentScreen.MainTabs(tab)
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = tab.title,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = ForestDark,
                                                selectedTextColor = GoldAccent,
                                                indicatorColor = GoldAccent,
                                                unselectedIconColor = WarmWhite.copy(alpha = 0.65f),
                                                unselectedTextColor = WarmWhite.copy(alpha = 0.65f)
                                            ),
                                            modifier = Modifier.testTag(tab.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    } else if (screenState is CurrentScreen.SurahDetail) {
                        AudioMiniPlayer(
                            state = audioState,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onStop = { viewModel.stopAudio() },
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (val current = screenState) {
                        is CurrentScreen.MainTabs -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                QuranTopBar(
                                    title = "Yann Quran",
                                    subtitle = when (current.activeTab) {
                                        AppTab.QURAN -> "Your Daily Companion with the Holy Quran"
                                        AppTab.PRAYER -> "Jadwal Salat & Waktu Adzan Akurat"
                                        AppTab.QIBLA -> "Penunjuk Arah Kiblat ke Ka'bah"
                                        AppTab.YASIN -> "Bacaan Yasin, Tahlil & Doa Arwah"
                                        AppTab.DOA -> "Kumpulan Doa, Dzikir & Tasbih Digital"
                                    },
                                    isDesktopMode = false,
                                    onToggleDesktopMode = { forceDesktopMode = !forceDesktopMode },
                                    onBookmarkClick = {
                                        screenState = CurrentScreen.Bookmarks
                                    }
                                )

                                when (current.activeTab) {
                                    AppTab.QURAN -> {
                                        HomeScreen(
                                            surahs = surahs,
                                            searchQuery = searchQuery,
                                            selectedFilter = selectedFilter,
                                            lastRead = lastRead,
                                            onSearchChange = { viewModel.onSearchQueryChanged(it) },
                                            onFilterSelect = { viewModel.onFilterSelected(it) },
                                            onSurahClick = { surahNumber ->
                                                viewModel.loadSurahDetail(surahNumber)
                                                screenState = CurrentScreen.SurahDetail(surahNumber)
                                            },
                                            onContinueReading = { surahNumber, ayahNumber ->
                                                viewModel.loadSurahDetail(surahNumber)
                                                screenState = CurrentScreen.SurahDetail(surahNumber, ayahNumber)
                                            }
                                        )
                                    }
                                    AppTab.PRAYER -> {
                                        PrayerTimesScreen(
                                            schedule = prayerSchedule,
                                            selectedCityIndex = selectedCityIndex,
                                            useGpsLocation = useGpsLocation,
                                            isGpsLoading = isGpsLoading,
                                            gpsLocationName = gpsLocationName,
                                            isPrayerAudioEnabled = isPrayerAudioEnabled,
                                            prayerAudioVolume = prayerAudioVolume,
                                            reminderType = reminderType,
                                            prayerAudioState = prayerAudioState,
                                            isPrayerAudioActive = { viewModel.isPrayerAudioActive(it) },
                                            onSelectCity = { viewModel.selectCity(it) },
                                            onRequestGpsLocation = { viewModel.requestGpsLocation() },
                                            onToggleUseGps = { viewModel.setUseGpsLocation(it) },
                                            onToggleMasterAudio = { viewModel.togglePrayerAudioMaster(it) },
                                            onSetAudioVolume = { viewModel.setPrayerAudioVolume(it) },
                                            onSetPrayerAudioActive = { name, active -> viewModel.setPrayerAudioActive(name, active) },
                                            onTestAudio = { viewModel.testPrayerAudio(it) },
                                            onStopAudio = { viewModel.stopPrayerAudio() }
                                        )
                                    }
                                    AppTab.QIBLA -> {
                                        QiblaScreen(
                                            qiblaInfo = qiblaInfo,
                                            compassManager = viewModel.compassManager
                                        )
                                    }
                                    AppTab.YASIN -> {
                                        YasinTahlilScreen(
                                            onOpenFullYasin = {
                                                viewModel.loadSurahDetail(36)
                                                screenState = CurrentScreen.SurahDetail(36)
                                            }
                                        )
                                    }
                                    AppTab.DOA -> {
                                        DoaDzikirScreen(
                                            tasbihCount = tasbihCount,
                                            tasbihTarget = tasbihTarget,
                                            onIncrementTasbih = { viewModel.incrementTasbih() },
                                            onResetTasbih = { viewModel.resetTasbih() },
                                            onSetTasbihTarget = { viewModel.setTasbihTarget(it) }
                                        )
                                    }
                                }
                            }
                        }
                        is CurrentScreen.SurahDetail -> {
                            SurahDetailScreen(
                                detailState = detailState,
                                audioPlaybackState = audioState,
                                arabicFontSize = arabicFontSize,
                                showLatin = showLatin,
                                showTranslation = showTranslation,
                                isAyahBookmarked = { s, a -> viewModel.isAyahBookmarked(s, a) },
                                onBackClick = { screenState = CurrentScreen.MainTabs() },
                                onPlayAyah = { ayah ->
                                    val viewing = viewModel.currentViewingSurah.value
                                    if (viewing != null) {
                                        viewModel.playAyahAudio(viewing.nomor, viewing.namaLatin, ayah)
                                    }
                                },
                                onPlayFullSurah = { surahDetail ->
                                    viewModel.playFullSurahAudio(surahDetail)
                                },
                                onToggleBookmark = { ayah ->
                                    val viewing = viewModel.currentViewingSurah.value
                                    if (viewing != null) {
                                        viewModel.toggleBookmark(viewing.nomor, viewing.namaLatin, ayah)
                                    }
                                },
                                onSetLastRead = { ayahNum ->
                                    val viewing = viewModel.currentViewingSurah.value
                                    if (viewing != null) {
                                        viewModel.setLastRead(viewing.nomor, viewing.namaLatin, ayahNum, viewing.jumlahAyat)
                                    }
                                },
                                onOpenTafsir = {
                                    viewModel.loadTafsir(current.surahNumber)
                                },
                                onOpenFontSettings = {
                                    showFontSettings = true
                                },
                                onNavigateSurah = { nextNomor ->
                                    viewModel.loadSurahDetail(nextNomor)
                                    screenState = CurrentScreen.SurahDetail(nextNomor)
                                }
                            )
                        }
                        is CurrentScreen.Bookmarks -> {
                            BookmarkScreen(
                                bookmarks = bookmarks,
                                onBackClick = { screenState = CurrentScreen.MainTabs() },
                                onBookmarkClick = { sNum, aNum ->
                                    viewModel.loadSurahDetail(sNum)
                                    screenState = CurrentScreen.SurahDetail(sNum, aNum)
                                },
                                onDeleteBookmark = { sNum, aNum ->
                                    viewModel.toggleBookmark(
                                        sNum,
                                        "",
                                        com.example.data.model.Ayat(aNum, "", "", "")
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Shared Modals
        if (tafsirState !is TafsirUiState.Idle) {
            TafsirBottomSheet(
                tafsirState = tafsirState,
                onDismiss = { viewModel.closeTafsir() }
            )
        }

        if (showFontSettings) {
            FontSettingsBottomSheet(
                currentFontSize = arabicFontSize,
                showLatin = showLatin,
                showTranslation = showTranslation,
                selectedReciter = selectedReciter,
                onFontSizeChange = { viewModel.updateFontSize(it) },
                onToggleLatin = { viewModel.toggleLatin() },
                onToggleTranslation = { viewModel.toggleTranslation() },
                onSelectReciter = { viewModel.selectReciter(it) },
                onDismiss = { showFontSettings = false }
            )
        }
    }
}
