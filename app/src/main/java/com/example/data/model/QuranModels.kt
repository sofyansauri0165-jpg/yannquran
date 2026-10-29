package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SurahListResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: List<SurahSummary> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SurahSummary(
    @Json(name = "nomor") val nomor: Int,
    @Json(name = "nama") val nama: String,
    @Json(name = "namaLatin") val namaLatin: String,
    @Json(name = "jumlahAyat") val jumlahAyat: Int,
    @Json(name = "tempatTurun") val tempatTurun: String,
    @Json(name = "arti") val arti: String,
    @Json(name = "deskripsi") val deskripsi: String = "",
    @Json(name = "audioFull") val audioFull: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SurahDetailResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: SurahDetail? = null
)

@JsonClass(generateAdapter = true)
data class SurahDetail(
    @Json(name = "nomor") val nomor: Int,
    @Json(name = "nama") val nama: String,
    @Json(name = "namaLatin") val namaLatin: String,
    @Json(name = "jumlahAyat") val jumlahAyat: Int,
    @Json(name = "tempatTurun") val tempatTurun: String,
    @Json(name = "arti") val arti: String,
    @Json(name = "deskripsi") val deskripsi: String = "",
    @Json(name = "audioFull") val audioFull: Map<String, String>? = null,
    @Json(name = "ayat") val ayat: List<Ayat> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Ayat(
    @Json(name = "nomorAyat") val nomorAyat: Int,
    @Json(name = "teksArab") val teksArab: String,
    @Json(name = "teksLatin") val teksLatin: String,
    @Json(name = "teksIndonesia") val teksIndonesia: String,
    @Json(name = "audio") val audio: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class TafsirResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: TafsirDetail? = null
)

@JsonClass(generateAdapter = true)
data class TafsirDetail(
    @Json(name = "nomor") val nomor: Int,
    @Json(name = "nama") val nama: String,
    @Json(name = "namaLatin") val namaLatin: String,
    @Json(name = "jumlahAyat") val jumlahAyat: Int,
    @Json(name = "tempatTurun") val tempatTurun: String,
    @Json(name = "arti") val arti: String,
    @Json(name = "deskripsi") val deskripsi: String = "",
    @Json(name = "tafsir") val tafsir: List<TafsirItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TafsirItem(
    @Json(name = "ayat") val ayat: Int,
    @Json(name = "teks") val teks: String
)

data class Bookmark(
    val id: String = "",
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val textArab: String,
    val textIndo: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LastRead(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val totalAyat: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class Reciter(
    val key: String,
    val name: String,
    val description: String
) {
    companion object {
        val ALL = listOf(
            Reciter("05", "Misyari Rasyid Al-Afasi", "Merdu & Tartil"),
            Reciter("01", "Abdullah Al-Juhany", "Imam Masjidil Haram"),
            Reciter("02", "Abdul Muhsin Al-Qasim", "Imam Masjid Nabawi"),
            Reciter("03", "Abdurrahman As-Sudais", "Khas Masjidil Haram"),
            Reciter("04", "Ibrahim Al-Dossari", "Qari Saudi Arabia")
        )
    }
}
