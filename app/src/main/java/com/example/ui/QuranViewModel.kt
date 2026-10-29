package com.example.ui

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlaybackState
import com.example.audio.MurottalAudioPlayer
import com.example.audio.PrayerAudioManager
import com.example.audio.PrayerAudioState
import com.example.audio.PrayerAudioType
import com.example.data.api.ApiClient
import com.example.data.local.DoaDzikirData
import com.example.data.local.PreferencesManager
import com.example.data.local.QuranBundledData
import com.example.data.local.YasinTahlilData
import com.example.data.model.Ayat
import com.example.data.model.Bookmark
import com.example.data.model.LastRead
import com.example.data.model.SurahDetail
import com.example.data.model.SurahSummary
import com.example.data.model.TafsirDetail
import com.example.data.prayer.CityLocation
import com.example.data.prayer.LocationHelper
import com.example.data.prayer.PrayerCalculator
import com.example.data.prayer.PrayerSchedule
import com.example.data.prayer.QiblaInfo
import com.example.sensor.QiblaCompassManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val detail: SurahDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

sealed interface TafsirUiState {
    data object Idle : TafsirUiState
    data object Loading : TafsirUiState
    data class Success(val tafsir: TafsirDetail) : TafsirUiState
    data class Error(val message: String) : TafsirUiState
}

class QuranViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    val audioPlayer = MurottalAudioPlayer(application)
    val prayerAudioManager = PrayerAudioManager(application)
    val compassManager = QiblaCompassManager(application)
    val locationHelper = LocationHelper(application)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = application.getSystemService(Application.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Application.VIBRATOR_SERVICE) as? Vibrator
    }

    // Surah List & Filter
    private val _surahs = MutableStateFlow<List<SurahSummary>>(QuranBundledData.SURAHS)
    val surahs: StateFlow<List<SurahSummary>> = _surahs.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("Semua")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // Surah Detail
    private val _detailState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    private val _currentViewingSurah = MutableStateFlow<SurahSummary?>(null)
    val currentViewingSurah: StateFlow<SurahSummary?> = _currentViewingSurah.asStateFlow()

    // Tafsir
    private val _tafsirState = MutableStateFlow<TafsirUiState>(TafsirUiState.Idle)
    val tafsirState: StateFlow<TafsirUiState> = _tafsirState.asStateFlow()

    // Bookmarks & Last Read
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(prefs.getBookmarks())
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _lastRead = MutableStateFlow<LastRead?>(prefs.getLastRead())
    val lastRead: StateFlow<LastRead?> = _lastRead.asStateFlow()

    // Preferences & Customization
    private val _arabicFontSize = MutableStateFlow(prefs.arabicFontSize)
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _showLatin = MutableStateFlow(prefs.showLatin)
    val showLatin: StateFlow<Boolean> = _showLatin.asStateFlow()

    private val _showTranslation = MutableStateFlow(prefs.showTranslation)
    val showTranslation: StateFlow<Boolean> = _showTranslation.asStateFlow()

    private val _selectedReciter = MutableStateFlow(prefs.selectedReciter)
    val selectedReciter: StateFlow<String> = _selectedReciter.asStateFlow()

    // Prayer & Qibla
    private val _selectedCityIndex = MutableStateFlow(prefs.selectedCityIndex.coerceIn(0, PrayerCalculator.MAJOR_CITIES.lastIndex))
    val selectedCityIndex: StateFlow<Int> = _selectedCityIndex.asStateFlow()

    private val _useGpsLocation = MutableStateFlow(prefs.useGpsLocation)
    val useGpsLocation: StateFlow<Boolean> = _useGpsLocation.asStateFlow()

    private val _isGpsLoading = MutableStateFlow(false)
    val isGpsLoading: StateFlow<Boolean> = _isGpsLoading.asStateFlow()

    private val _gpsLocationName = MutableStateFlow(prefs.gpsLocationName)
    val gpsLocationName: StateFlow<String> = _gpsLocationName.asStateFlow()

    private val _prayerSchedule = MutableStateFlow<PrayerSchedule?>(null)
    val prayerSchedule: StateFlow<PrayerSchedule?> = _prayerSchedule.asStateFlow()

    private val _qiblaInfo = MutableStateFlow<QiblaInfo?>(null)
    val qiblaInfo: StateFlow<QiblaInfo?> = _qiblaInfo.asStateFlow()

    // Prayer Audio Settings
    private val _isPrayerAudioEnabled = MutableStateFlow(prefs.isPrayerAudioEnabled)
    val isPrayerAudioEnabled: StateFlow<Boolean> = _isPrayerAudioEnabled.asStateFlow()

    private val _prayerAudioVolume = MutableStateFlow(prefs.prayerAudioVolume)
    val prayerAudioVolume: StateFlow<Float> = _prayerAudioVolume.asStateFlow()

    private val _reminderType = MutableStateFlow(prefs.reminderType)
    val reminderType: StateFlow<String> = _reminderType.asStateFlow()

    val prayerAudioState: StateFlow<PrayerAudioState> = prayerAudioManager.audioState

    // Digital Tasbih
    private val _tasbihCount = MutableStateFlow(prefs.tasbihCount)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(prefs.tasbihTarget)
    val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

    // Murottal Audio Playback
    val audioPlaybackState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    init {
        prayerAudioManager.setVolume(prefs.prayerAudioVolume)
        fetchRemoteSurahList()
        updatePrayerAndQibla()
        startPeriodicPrayerChecker()
    }

    private fun fetchRemoteSurahList() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getSurahList()
                }
                if (response.code == 200 && response.data.isNotEmpty()) {
                    _surahs.value = response.data
                }
            } catch (_: Exception) {}
        }
    }

    // Periodic Prayer Time Scheduler & Deduplication Check
    private fun startPeriodicPrayerChecker() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    checkAndTriggerScheduledPrayer()
                } catch (_: Exception) {}
                delay(12_000) // check every 12 seconds
            }
        }
    }

    private fun checkAndTriggerScheduledPrayer() {
        if (!_isPrayerAudioEnabled.value) return

        val schedule = _prayerSchedule.value ?: return
        val now = Calendar.getInstance()
        val currentHHmm = String.format(Locale.US, "%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val prayerEntries = listOf(
            "Imsak" to schedule.imsak,
            "Subuh" to schedule.subuh,
            "Terbit" to schedule.terbit,
            "Dzuhur" to schedule.dzuhur,
            "Ashar" to schedule.ashar,
            "Maghrib" to schedule.maghrib,
            "Isya" to schedule.isya
        )

        for ((name, scheduledTime) in prayerEntries) {
            if (currentHHmm == scheduledTime) {
                // Check if this specific prayer's audio toggle is on
                if (!prefs.isPrayerAudioActive(name)) continue

                // Deduplication check: Has this prayer already triggered today?
                if (!prefs.isPrayerTriggeredToday(todayKey, name)) {
                    // Mark as triggered immediately to avoid race conditions
                    prefs.markPrayerTriggeredToday(todayKey, name)

                    // Determine correct audio type
                    val audioType = PrayerAudioManager.getAudioTypeForPrayer(name)

                    // Trigger audio playback
                    prayerAudioManager.playAudio(
                        type = audioType,
                        prayerName = name,
                        onMurottalMustStop = {
                            audioPlayer.stop()
                        }
                    )
                    break
                }
            }
        }
    }

    // GPS Location Request
    fun requestGpsLocation() {
        _isGpsLoading.value = true
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    locationHelper.getCurrentLocation()
                }
                if (result != null) {
                    prefs.useGpsLocation = true
                    prefs.gpsLatitude = result.latitude
                    prefs.gpsLongitude = result.longitude
                    prefs.gpsLocationName = result.locationName
                    _useGpsLocation.value = true
                    _gpsLocationName.value = result.locationName
                    updatePrayerAndQibla()
                }
            } catch (_: Exception) {
            } finally {
                _isGpsLoading.value = false
            }
        }
    }

    fun setUseGpsLocation(useGps: Boolean) {
        prefs.useGpsLocation = useGps
        _useGpsLocation.value = useGps
        if (useGps) {
            requestGpsLocation()
        } else {
            updatePrayerAndQibla()
        }
    }

    // Audio Test Actions
    fun testPrayerAudio(type: PrayerAudioType) {
        prayerAudioManager.playAudio(
            type = type,
            prayerName = type.label,
            onMurottalMustStop = { audioPlayer.stop() }
        )
    }

    fun stopPrayerAudio() {
        prayerAudioManager.stopAudio()
    }

    fun setPrayerAudioVolume(volume: Float) {
        prefs.prayerAudioVolume = volume
        _prayerAudioVolume.value = volume
        prayerAudioManager.setVolume(volume)
    }

    fun togglePrayerAudioMaster(enabled: Boolean) {
        prefs.isPrayerAudioEnabled = enabled
        _isPrayerAudioEnabled.value = enabled
        if (!enabled) {
            prayerAudioManager.stopAudio()
        }
    }

    fun setPrayerAudioActive(prayerName: String, active: Boolean) {
        prefs.setPrayerAudioActive(prayerName, active)
    }

    fun isPrayerAudioActive(prayerName: String): Boolean {
        return prefs.isPrayerAudioActive(prayerName)
    }

    fun setReminderType(type: String) {
        prefs.reminderType = type
        _reminderType.value = type
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun loadSurahDetail(surahNumber: Int) {
        val summary = _surahs.value.find { it.nomor == surahNumber }
            ?: QuranBundledData.SURAHS.find { it.nomor == surahNumber }
        _currentViewingSurah.value = summary
        _detailState.value = DetailUiState.Loading

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getSurahDetail(surahNumber)
                }
                if (response.code == 200 && response.data != null) {
                    _detailState.value = DetailUiState.Success(response.data)
                } else {
                    handleOfflineDetailFallback(surahNumber, summary)
                }
            } catch (_: Exception) {
                handleOfflineDetailFallback(surahNumber, summary)
            }
        }
    }

    private fun handleOfflineDetailFallback(surahNumber: Int, summary: SurahSummary?) {
        if (surahNumber == 36) {
            val yasinDetail = SurahDetail(
                nomor = 36,
                nama = "يس",
                namaLatin = "Yasin",
                jumlahAyat = 83,
                tempatTurun = "Mekah",
                arti = "Yasin",
                ayat = YasinTahlilData.YASIN_SAMPLE_AYAT
            )
            _detailState.value = DetailUiState.Success(yasinDetail)
        } else {
            _detailState.value = DetailUiState.Error(
                "Tidak dapat memuat ayat surat ${summary?.namaLatin ?: surahNumber}. Silakan periksa koneksi internet Anda."
            )
        }
    }

    fun loadTafsir(surahNumber: Int) {
        _tafsirState.value = TafsirUiState.Loading
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getTafsir(surahNumber)
                }
                if (response.code == 200 && response.data != null) {
                    _tafsirState.value = TafsirUiState.Success(response.data)
                } else {
                    _tafsirState.value = TafsirUiState.Error("Tafsir surat tidak dapat ditampilkan.")
                }
            } catch (e: Exception) {
                _tafsirState.value = TafsirUiState.Error("Gagal memuat tafsir: ${e.localizedMessage}")
            }
        }
    }

    fun closeTafsir() {
        _tafsirState.value = TafsirUiState.Idle
    }

    fun toggleBookmark(surahNumber: Int, surahName: String, ayah: Ayat) {
        val isSaved = prefs.isBookmarked(surahNumber, ayah.nomorAyat)
        if (isSaved) {
            prefs.removeBookmark(surahNumber, ayah.nomorAyat)
        } else {
            prefs.addBookmark(
                Bookmark(
                    id = "${surahNumber}_${ayah.nomorAyat}",
                    surahNumber = surahNumber,
                    surahName = surahName,
                    ayahNumber = ayah.nomorAyat,
                    textArab = ayah.teksArab,
                    textIndo = ayah.teksIndonesia
                )
            )
        }
        _bookmarks.value = prefs.getBookmarks()
    }

    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return _bookmarks.value.any { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
    }

    fun setLastRead(surahNumber: Int, surahName: String, ayahNumber: Int, totalAyat: Int = 0) {
        prefs.saveLastRead(surahNumber, surahName, ayahNumber, totalAyat)
        _lastRead.value = prefs.getLastRead()
    }

    // Murottal Controls
    fun playAyahAudio(surahNumber: Int, surahName: String, ayah: Ayat) {
        prayerAudioManager.stopAudio() // Stop any prayer audio first
        val reciter = _selectedReciter.value
        val audioUrl = ayah.audio?.get(reciter) ?: ayah.audio?.values?.firstOrNull()
        if (audioUrl.isNullOrEmpty()) return

        audioPlayer.playUrl(
            url = audioUrl,
            surahNumber = surahNumber,
            surahName = surahName,
            ayahNumber = ayah.nomorAyat
        )
    }

    fun playFullSurahAudio(surah: SurahDetail) {
        prayerAudioManager.stopAudio() // Stop any prayer audio first
        val reciter = _selectedReciter.value
        val audioUrl = surah.audioFull?.get(reciter) ?: surah.audioFull?.values?.firstOrNull()
        if (audioUrl.isNullOrEmpty()) return

        audioPlayer.playUrl(
            url = audioUrl,
            surahNumber = surah.nomor,
            surahName = surah.namaLatin,
            ayahNumber = null
        )
    }

    fun togglePlayPause() {
        if (audioPlaybackState.value.isPlaying) {
            audioPlayer.pause()
        } else {
            audioPlayer.resume()
        }
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    fun updateFontSize(newSize: Float) {
        prefs.arabicFontSize = newSize
        _arabicFontSize.value = newSize
    }

    fun toggleLatin() {
        val newVal = !_showLatin.value
        prefs.showLatin = newVal
        _showLatin.value = newVal
    }

    fun toggleTranslation() {
        val newVal = !_showTranslation.value
        prefs.showTranslation = newVal
        _showTranslation.value = newVal
    }

    fun selectReciter(qariKey: String) {
        prefs.selectedReciter = qariKey
        _selectedReciter.value = qariKey
    }

    fun selectCity(index: Int) {
        prefs.useGpsLocation = false
        _useGpsLocation.value = false
        prefs.selectedCityIndex = index
        _selectedCityIndex.value = index
        updatePrayerAndQibla()
    }

    fun updatePrayerAndQibla() {
        val city = if (_useGpsLocation.value) {
            val tzOffset = TimeZone.getDefault().rawOffset / 3600000.0
            CityLocation(
                name = _gpsLocationName.value,
                province = "GPS Otomatis",
                latitude = prefs.gpsLatitude,
                longitude = prefs.gpsLongitude,
                timezoneOffsetHours = tzOffset
            )
        } else {
            PrayerCalculator.MAJOR_CITIES[_selectedCityIndex.value]
        }

        val schedule = PrayerCalculator.calculatePrayerSchedule(city)
        val qibla = PrayerCalculator.calculateQibla(city.latitude, city.longitude, city.name)
        _prayerSchedule.value = schedule
        _qiblaInfo.value = qibla
    }

    fun incrementTasbih() {
        var next = _tasbihCount.value + 1
        if (next > _tasbihTarget.value) {
            next = 1
        }
        _tasbihCount.value = next
        prefs.tasbihCount = next

        try {
            if (next == _tasbihTarget.value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(100)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, 120))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {}
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
        prefs.tasbihCount = 0
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
        prefs.tasbihTarget = target
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
        prayerAudioManager.stopAudio()
        compassManager.stopListening()
    }
}
