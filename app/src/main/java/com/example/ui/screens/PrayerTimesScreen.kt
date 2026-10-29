package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PrayerAudioState
import com.example.audio.PrayerAudioType
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.PrayerSchedule
import com.example.ui.theme.*

@Composable
fun PrayerTimesScreen(
    schedule: PrayerSchedule?,
    selectedCityIndex: Int,
    useGpsLocation: Boolean,
    isGpsLoading: Boolean,
    gpsLocationName: String,
    isPrayerAudioEnabled: Boolean,
    prayerAudioVolume: Float,
    reminderType: String,
    prayerAudioState: PrayerAudioState,
    isPrayerAudioActive: (String) -> Boolean,
    onSelectCity: (Int) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onToggleUseGps: (Boolean) -> Unit,
    onToggleMasterAudio: (Boolean) -> Unit,
    onSetAudioVolume: (Float) -> Unit,
    onSetPrayerAudioActive: (String, Boolean) -> Unit,
    onTestAudio: (PrayerAudioType) -> Unit,
    onStopAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    var showAudioSettingsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onRequestGpsLocation()
        }
    }

    if (showCityDialog) {
        CitySelectionDialog(
            selectedIndex = selectedCityIndex,
            onSelect = {
                onSelectCity(it)
                showCityDialog = false
            },
            onDismiss = { showCityDialog = false }
        )
    }

    if (showAudioSettingsDialog) {
        PrayerAudioSettingsDialog(
            isAudioEnabled = isPrayerAudioEnabled,
            volume = prayerAudioVolume,
            reminderType = reminderType,
            isPrayerAudioActive = isPrayerAudioActive,
            onToggleMaster = onToggleMasterAudio,
            onVolumeChange = onSetAudioVolume,
            onTogglePrayer = onSetPrayerAudioActive,
            onTestAudio = onTestAudio,
            onStopAudio = onStopAudio,
            audioState = prayerAudioState,
            onDismiss = { showAudioSettingsDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_times_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Countdown Hero Card
        item {
            schedule?.let { s ->
                NextPrayerCountdownCard(
                    schedule = s,
                    useGps = useGpsLocation,
                    isGpsLoading = isGpsLoading,
                    gpsName = gpsLocationName,
                    onOpenCityPicker = { showCityDialog = true },
                    onRequestGps = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onOpenAudioSettings = { showAudioSettingsDialog = true }
                )
            }
        }

        // Active Audio Playback Bar if Adzan/Audio is running
        item {
            AnimatedVisibility(visible = prayerAudioState.isPlaying || prayerAudioState.isLoading) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = ForestDark,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (prayerAudioState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = GoldAccent,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (prayerAudioState.isLoading) "Memuat audio..." else "Memutar: ${prayerAudioState.currentType?.label ?: "Pengingat"}",
                                    color = WarmWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = prayerAudioState.currentType?.description ?: "Waktu salat tiba",
                                    color = GoldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = onStopAudio,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Hentikan", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Section Title & GPS Toolbar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Jadwal Shalat Hari Ini",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Quick button to open Audio Settings
                    FilledTonalButton(
                        onClick = { showAudioSettingsDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = EmeraldPrimary.copy(alpha = 0.15f),
                            contentColor = EmeraldPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pengaturan Suara", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // GPS & City Mode Switcher Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !useGpsLocation,
                        onClick = { showCityDialog = true },
                        label = { Text("Pilih Kota Manual", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = WarmWhite
                        )
                    )

                    FilterChip(
                        selected = useGpsLocation,
                        onClick = {
                            if (!useGpsLocation) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        label = {
                            Text(
                                if (isGpsLoading) "Mendeteksi..." else if (useGpsLocation) "GPS Aktif" else "Gunakan GPS",
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            if (isGpsLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = GoldAccent)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = ForestDark
                        )
                    )
                }
            }
        }

        // Prayer Schedule Cards List
        schedule?.let { s ->
            val prayerRows = listOf(
                PrayerRowData("Imsak", s.imsak, Icons.Default.NightsStay, false, PrayerAudioType.IMSAK),
                PrayerRowData("Subuh", s.subuh, Icons.Default.WbTwilight, s.nextPrayerName.startsWith("Subuh"), PrayerAudioType.SUBUH),
                PrayerRowData("Terbit", s.terbit, Icons.Default.WbSunny, false, PrayerAudioType.TERBIT),
                PrayerRowData("Dhuha", s.dhuha, Icons.Default.LightMode, false, null),
                PrayerRowData("Dzuhur", s.dzuhur, Icons.Default.WbSunny, s.nextPrayerName == "Dzuhur", PrayerAudioType.FARDHU),
                PrayerRowData("Ashar", s.ashar, Icons.Default.WbTwilight, s.nextPrayerName == "Ashar", PrayerAudioType.FARDHU),
                PrayerRowData("Maghrib", s.maghrib, Icons.Default.NightsStay, s.nextPrayerName == "Maghrib", PrayerAudioType.FARDHU),
                PrayerRowData("Isya", s.isya, Icons.Default.Bedtime, s.nextPrayerName == "Isya", PrayerAudioType.FARDHU)
            )

            items(prayerRows) { row ->
                PrayerItemCard(
                    row = row,
                    isAudioActive = isPrayerAudioActive(row.name),
                    onToggleAudio = { onSetPrayerAudioActive(row.name, it) },
                    onTestAudio = {
                        row.audioType?.let { onTestAudio(it) }
                    }
                )
            }
        }

        // Audio Test Hub Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Uji Nyata Audio Jadwal Salat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tekan tombol di bawah untuk menguji setiap file audio pengingat:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PrayerAudioType.entries.forEach { type ->
                        OutlinedButton(
                            onClick = { onTestAudio(type) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(type.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = when (type) {
                                        PrayerAudioType.SUBUH -> "adzan_subuh.mp3"
                                        PrayerAudioType.FARDHU -> "adzan.mp3"
                                        PrayerAudioType.IMSAK -> "imsak.mp3"
                                        PrayerAudioType.TERBIT -> "ayam.mp3"
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Realistic Device Constraints Information Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Catatan Sistem & Akurasi Waktu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Audio pengingat diputar otomatis satu kali tepat saat jadwal tiba tanpa pengulangan ganda.\n• Ketika aplikasi aktif di layar, audio terdengar langsung. Saat layar mati/aplikasi diminimalkan, perangkat Android menggunakan alarm sistem & notifikasi agar daya baterai tetap hemat.\n• Hisab astronomi dihitung sesuai standar Kemenag RI (+2 menit ihtiyat).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

data class PrayerRowData(
    val name: String,
    val time: String,
    val icon: ImageVector,
    val isNext: Boolean,
    val audioType: PrayerAudioType?
)

@Composable
fun NextPrayerCountdownCard(
    schedule: PrayerSchedule,
    useGps: Boolean,
    isGpsLoading: Boolean,
    gpsName: String,
    onOpenCityPicker: () -> Unit,
    onRequestGps: () -> Unit,
    onOpenAudioSettings: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ForestDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ForestDark, EmeraldPrimary)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Pill with GPS status
                    Surface(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable {
                            if (useGps) onRequestGps() else onOpenCityPicker()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (useGps) Icons.Default.MyLocation else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (useGps) (if (isGpsLoading) "Mencari GPS..." else gpsName) else schedule.cityName,
                                color = WarmWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenAudioSettings,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                            .size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Pengaturan Audio Salat",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "MENUJU SALAT BERIKUTNYA",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = schedule.nextPrayerName,
                            color = WarmWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val hours = schedule.countdownMinutes / 60
                        val mins = schedule.countdownMinutes % 60
                        val cdText = if (hours > 0) "$hours jam $mins menit lagi" else "$mins menit lagi"
                        Text(
                            text = "- $cdText",
                            color = GoldLight,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = schedule.nextPrayerTime,
                        color = GoldAccent,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = schedule.dateFormatted,
                        color = WarmWhite.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )

                    Surface(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Audio Adzan Aktif",
                            color = GoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerItemCard(
    row: PrayerRowData,
    isAudioActive: Boolean,
    onToggleAudio: (Boolean) -> Unit,
    onTestAudio: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = if (row.isNext) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (row.isNext) GoldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (row.isNext) GoldAccent else EmeraldPrimary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = row.icon,
                            contentDescription = null,
                            tint = if (row.isNext) ForestDark else EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = row.name,
                        fontSize = 15.sp,
                        fontWeight = if (row.isNext) FontWeight.Bold else FontWeight.Medium,
                        color = if (row.isNext) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (row.isNext) "Waktu Berikutnya" else (row.audioType?.label ?: "Waktu Sunnah"),
                        fontSize = 11.sp,
                        color = if (row.isNext) GoldDark else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = row.time,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (row.isNext) GoldDark else MaterialTheme.colorScheme.onSurface
                )

                if (row.audioType != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onTestAudio,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Uji Suara ${row.name}",
                            tint = if (isAudioActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerAudioSettingsDialog(
    isAudioEnabled: Boolean,
    volume: Float,
    reminderType: String,
    isPrayerAudioActive: (String) -> Boolean,
    onToggleMaster: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onTogglePrayer: (String, Boolean) -> Unit,
    onTestAudio: (PrayerAudioType) -> Unit,
    onStopAudio: () -> Unit,
    audioState: PrayerAudioState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pengaturan Audio Salat", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Master Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bunyikan Audio Salat", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Switch(
                        checked = isAudioEnabled,
                        onCheckedChange = onToggleMaster,
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent, checkedTrackColor = EmeraldPrimary)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Volume Slider
                Text(
                    text = "Volume Suara: ${(volume * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = EmeraldPrimary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Aktifkan per Waktu Salat:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(6.dp))

                listOf("Imsak", "Subuh", "Terbit", "Dzuhur", "Ashar", "Maghrib", "Isya").forEach { prayer ->
                    val active = isPrayerAudioActive(prayer)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePrayer(prayer, !active) }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = prayer,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Checkbox(
                            checked = active,
                            onCheckedChange = { onTogglePrayer(prayer, it) },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan Pengaturan", color = WarmWhite)
            }
        }
    )
}

@Composable
fun CitySelectionDialog(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pilih Kota / Wilayah",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp)) {
                items(PrayerCalculator.MAJOR_CITIES.indices.toList()) { index ->
                    val city = PrayerCalculator.MAJOR_CITIES[index]
                    val isSelected = selectedIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(index) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = city.name,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = city.province,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = EmeraldPrimary)
            }
        }
    )
}
