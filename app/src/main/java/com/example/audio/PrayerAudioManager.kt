package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PrayerAudioType(
    val label: String,
    val url: String,
    val description: String
) {
    SUBUH(
        "Adzan Subuh",
        "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan_subuh.mp3",
        "Lafadz As-shalatu khairum minan naum"
    ),
    FARDHU(
        "Adzan Fardhu",
        "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan.mp3",
        "Untuk Dzuhur, Ashar, Maghrib & Isya"
    ),
    IMSAK(
        "Peringatan Imsak",
        "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/imsak.mp3",
        "Peringatan masuk batas imsak"
    ),
    TERBIT(
        "Waktu Terbit (Syuruq)",
        "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/ayam.mp3",
        "Suara ayam berkokok penanda matahari terbit"
    )
}

data class PrayerAudioState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentType: PrayerAudioType? = null,
    val currentPrayerName: String? = null,
    val errorMessage: String? = null
)

class PrayerAudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _audioState = MutableStateFlow(PrayerAudioState())
    val audioState: StateFlow<PrayerAudioState> = _audioState.asStateFlow()

    private var currentVolume: Float = 0.85f

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        mediaPlayer?.let {
            try {
                it.setVolume(currentVolume, currentVolume)
            } catch (_: Exception) {}
        }
    }

    fun playAudio(
        type: PrayerAudioType,
        prayerName: String,
        onMurottalMustStop: () -> Unit = {},
        onFinished: () -> Unit = {}
    ) {
        // Ensure any ongoing murottal or adzan stops
        onMurottalMustStop()
        stopAudio()

        _audioState.value = PrayerAudioState(
            isLoading = true,
            currentType = type,
            currentPrayerName = prayerName
        )

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(type.url)
                setVolume(currentVolume, currentVolume)
                setOnPreparedListener { mp ->
                    mp.start()
                    _audioState.value = PrayerAudioState(
                        isPlaying = true,
                        isLoading = false,
                        currentType = type,
                        currentPrayerName = prayerName
                    )
                }
                setOnCompletionListener {
                    _audioState.value = PrayerAudioState()
                    releaseMediaPlayer()
                    onFinished()
                }
                setOnErrorListener { _, what, extra ->
                    _audioState.value = PrayerAudioState(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "Tidak dapat memutar audio ($what, $extra). Periksa koneksi internet."
                    )
                    releaseMediaPlayer()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _audioState.value = PrayerAudioState(
                isPlaying = false,
                isLoading = false,
                errorMessage = e.localizedMessage ?: "Gagal memutar audio pengingat salat"
            )
            releaseMediaPlayer()
        }
    }

    fun stopAudio() {
        releaseMediaPlayer()
        _audioState.value = PrayerAudioState()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
                it.reset()
                it.release()
            } catch (_: Exception) {}
        }
        mediaPlayer = null
    }

    companion object {
        fun getAudioTypeForPrayer(prayerName: String): PrayerAudioType {
            return when (prayerName.trim().lowercase()) {
                "subuh" -> PrayerAudioType.SUBUH
                "imsak" -> PrayerAudioType.IMSAK
                "terbit" -> PrayerAudioType.TERBIT
                else -> PrayerAudioType.FARDHU // Dzuhur, Ashar, Maghrib, Isya
            }
        }
    }
}
