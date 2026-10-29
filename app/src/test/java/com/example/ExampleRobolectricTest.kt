package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.PrayerAudioManager
import com.example.audio.PrayerAudioType
import com.example.data.local.PreferencesManager
import com.example.data.local.QuranBundledData
import com.example.data.prayer.PrayerCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.HttpURLConnection
import java.net.URL

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Yann Quran", appName)
  }

  @Test
  fun `verify 114 surahs bundled`() {
    assertEquals(114, QuranBundledData.SURAHS.size)
    assertEquals("Al-Fatihah", QuranBundledData.SURAHS[0].namaLatin)
    assertEquals("An-Nas", QuranBundledData.SURAHS[113].namaLatin)
  }

  @Test
  fun `verify qibla calculation`() {
    val qibla = PrayerCalculator.calculateQibla(-6.2088, 106.8456, "Jakarta")
    assertTrue(qibla.qiblaBearing in 290f..300f)
    assertTrue(qibla.distanceKm > 7000.0)
  }

  @Test
  fun `verify prayer audio mapping`() {
    assertEquals(PrayerAudioType.SUBUH, PrayerAudioManager.getAudioTypeForPrayer("Subuh"))
    assertEquals(PrayerAudioType.IMSAK, PrayerAudioManager.getAudioTypeForPrayer("Imsak"))
    assertEquals(PrayerAudioType.TERBIT, PrayerAudioManager.getAudioTypeForPrayer("Terbit"))
    assertEquals(PrayerAudioType.FARDHU, PrayerAudioManager.getAudioTypeForPrayer("Dzuhur"))
    assertEquals(PrayerAudioType.FARDHU, PrayerAudioManager.getAudioTypeForPrayer("Ashar"))
    assertEquals(PrayerAudioType.FARDHU, PrayerAudioManager.getAudioTypeForPrayer("Maghrib"))
    assertEquals(PrayerAudioType.FARDHU, PrayerAudioManager.getAudioTypeForPrayer("Isya"))

    assertEquals("https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan_subuh.mp3", PrayerAudioType.SUBUH.url)
    assertEquals("https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan.mp3", PrayerAudioType.FARDHU.url)
    assertEquals("https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/imsak.mp3", PrayerAudioType.IMSAK.url)
    assertEquals("https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/ayam.mp3", PrayerAudioType.TERBIT.url)
  }

  @Test
  fun `verify deduplication prevents double playback`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = PreferencesManager(context)
    val testDate = "2026-10-29"

    assertFalse(prefs.isPrayerTriggeredToday(testDate, "Subuh"))
    prefs.markPrayerTriggeredToday(testDate, "Subuh")
    assertTrue(prefs.isPrayerTriggeredToday(testDate, "Subuh"))

    // Ensure next prayer isn't marked
    assertFalse(prefs.isPrayerTriggeredToday(testDate, "Dzuhur"))

    // Ensure next day resets
    assertFalse(prefs.isPrayerTriggeredToday("2026-10-30", "Subuh"))
  }
}
