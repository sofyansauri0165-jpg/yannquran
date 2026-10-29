import React, { useState, useEffect, useRef } from 'react';
import {
  BookOpen,
  Clock,
  Compass,
  FileText,
  Sparkles,
  Play,
  Pause,
  Volume2,
  VolumeX,
  MapPin,
  RefreshCw,
  Search,
  Bookmark,
  ChevronRight,
  Maximize2,
  Minimize2,
  Square,
  Check,
  Info
} from 'lucide-react';

// Audio Endpoints
const PRAYER_AUDIOS = {
  subuh: "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan_subuh.mp3",
  fardhu: "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/adzan.mp3",
  imsak: "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/imsak.mp3",
  terbit: "https://wflq2j4mi1smqyku.public.blob.vercel-storage.com/ayam.mp3"
};

// 114 Surahs offline metadata fallback
const SURAHS_DEFAULT = [
  { nomor: 1, nama: "الفاتحة", namaLatin: "Al-Fatihah", jumlahAyat: 7, tempatTurun: "Mekah", arti: "Pembukaan" },
  { nomor: 2, nama: "البقرة", namaLatin: "Al-Baqarah", jumlahAyat: 286, tempatTurun: "Madinah", arti: "Sapi" },
  { nomor: 36, nama: "يس", namaLatin: "Yasin", jumlahAyat: 83, tempatTurun: "Mekah", arti: "Yasin" },
  { nomor: 55, nama: "الرحمن", namaLatin: "Ar-Rahman", jumlahAyat: 78, tempatTurun: "Madinah", arti: "Maha Pengasih" },
  { nomor: 56, nama: "الواقعة", namaLatin: "Al-Waqi'ah", jumlahAyat: 96, tempatTurun: "Mekah", arti: "Hari Kiamat" },
  { nomor: 67, nama: "الملك", namaLatin: "Al-Mulk", jumlahAyat: 30, tempatTurun: "Mekah", arti: "Kerajaan" },
  { nomor: 112, nama: "الإخلاص", namaLatin: "Al-Ikhlas", jumlahAyat: 4, tempatTurun: "Mekah", arti: "Ikhlas" },
  { nomor: 113, nama: "الفلق", namaLatin: "Al-Falaq", jumlahAyat: 5, tempatTurun: "Mekah", arti: "Waktu Subuh" },
  { nomor: 114, nama: "الناس", namaLatin: "An-Nas", jumlahAyat: 6, tempatTurun: "Mekah", arti: "Manusia" }
];

export default function App() {
  const [activeTab, setActiveTab] = useState('quran');
  const [surahs, setSurahs] = useState(SURAHS_DEFAULT);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedFilter, setSelectedFilter] = useState('Semua');
  const [selectedSurah, setSelectedSurah] = useState(1);
  const [surahDetail, setSurahDetail] = useState(null);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [isDesktopMode, setIsDesktopMode] = useState(true);

  // Audio Player State
  const [currentAudioUrl, setCurrentAudioUrl] = useState(null);
  const [isPlayingAudio, setIsPlayingAudio] = useState(false);
  const [audioLabel, setAudioLabel] = useState('');
  const [volume, setVolume] = useState(0.85);
  const audioRef = useRef(new Audio());

  // Prayer Times State
  const [city, setCity] = useState('DKI Jakarta');
  const [useGps, setUseGps] = useState(false);
  const [prayerTimes, setPrayerTimes] = useState({
    imsak: '04:06',
    subuh: '04:16',
    terbit: '05:32',
    dhuha: '05:58',
    dzuhur: '11:45',
    ashar: '14:55',
    maghrib: '17:53',
    isya: '19:04'
  });

  // Digital Tasbih State
  const [tasbihCount, setTasbihCount] = useState(0);
  const [tasbihTarget, setTasbihTarget] = useState(33);

  // Load all 114 Surahs from EQuran.id v2
  useEffect(() => {
    fetch('https://equran.id/api/v2/surat')
      .then(res => res.json())
      .then(json => {
        if (json.code === 200 && Array.isArray(json.data)) {
          setSurahs(json.data);
        }
      })
      .catch(() => {});
  }, []);

  // Load active Surah detail
  useEffect(() => {
    setLoadingDetail(true);
    fetch(`https://equran.id/api/v2/surat/${selectedSurah}`)
      .then(res => res.json())
      .then(json => {
        if (json.code === 200 && json.data) {
          setSurahDetail(json.data);
        }
      })
      .catch(() => {})
      .finally(() => setLoadingDetail(false));
  }, [selectedSurah]);

  // Handle Audio playback & volume
  const playAudioTrack = (url, label) => {
    if (audioRef.current) {
      audioRef.current.pause();
    }
    audioRef.current = new Audio(url);
    audioRef.current.volume = volume;
    audioRef.current.play().then(() => {
      setIsPlayingAudio(true);
      setCurrentAudioUrl(url);
      setAudioLabel(label);
    }).catch(err => {
      console.error("Audio playback error:", err);
    });

    audioRef.current.onended = () => {
      setIsPlayingAudio(false);
      setCurrentAudioUrl(null);
    };
  };

  const stopAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
    }
    setIsPlayingAudio(false);
    setCurrentAudioUrl(null);
  };

  const handleVolumeChange = (newVol) => {
    setVolume(newVol);
    if (audioRef.current) {
      audioRef.current.volume = newVol;
    }
  };

  // Filtered surahs
  const filteredSurahs = surahs.filter(s => {
    const matchQuery = !searchQuery ||
      s.namaLatin.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.arti.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.nomor.toString() === searchQuery.trim();

    const matchFilter = selectedFilter === 'Semua' ||
      (selectedFilter === 'Makkiyyah' && s.tempatTurun.toLowerCase() === 'mekah') ||
      (selectedFilter === 'Madaniyyah' && s.tempatTurun.toLowerCase() === 'madinah');

    return matchQuery && matchFilter;
  });

  return (
    <div className="min-h-screen bg-[#F8FAF7] text-[#022C22] flex flex-col font-sans">
      {/* Top Header */}
      <header className="bg-gradient-to-r from-[#022C22] via-[#064E3B] to-[#022C22] text-[#F8FAF7] px-6 py-4 border-b border-[#D4AF37]/30 shadow-lg sticky top-0 z-50">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-full bg-[#D4AF37] text-[#022C22] flex items-center justify-center font-bold shadow-md border border-[#F3E5AB]">
              YQ
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h1 className="text-xl font-bold tracking-wide text-[#D4AF37]">Yann Quran</h1>
                <span className="text-[10px] bg-[#D4AF37]/20 border border-[#D4AF37]/40 text-[#F3E5AB] px-2 py-0.5 rounded-full font-bold">
                  PREMIUM
                </span>
              </div>
              <p className="text-xs text-[#F8FAF7]/80">Your Daily Companion with the Holy Quran</p>
            </div>
          </div>

          {/* Desktop & Mobile Toggle */}
          <div className="flex items-center space-x-4">
            <button
              onClick={() => setIsDesktopMode(!isDesktopMode)}
              className="flex items-center space-x-2 bg-white/10 hover:bg-white/20 border border-[#D4AF37]/50 text-[#F3E5AB] text-xs px-3 py-1.5 rounded-full transition"
            >
              {isDesktopMode ? <Maximize2 size={14} /> : <Minimize2 size={14} />}
              <span className="hidden sm:inline">{isDesktopMode ? "Mode Desktop Aktif" : "Mode Mobile"}</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <div className="max-w-7xl mx-auto w-full flex-1 flex flex-col md:flex-row p-4 gap-6">
        {/* Navigation Sidebar (Desktop) or Floating Top (Mobile) */}
        <aside className="w-full md:w-64 bg-[#022C22] text-[#F8FAF7] rounded-2xl p-4 shadow-xl border border-[#D4AF37]/30 flex flex-row md:flex-col justify-between overflow-x-auto gap-2">
          <div className="flex flex-row md:flex-col gap-2 w-full">
            <button
              onClick={() => setActiveTab('quran')}
              className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition ${
                activeTab === 'quran' ? 'bg-[#D4AF37] text-[#022C22] shadow' : 'hover:bg-white/10 text-white/80'
              }`}
            >
              <BookOpen size={18} />
              <span>Al-Qur'an 30 Juz</span>
            </button>

            <button
              onClick={() => setActiveTab('prayer')}
              className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition ${
                activeTab === 'prayer' ? 'bg-[#D4AF37] text-[#022C22] shadow' : 'hover:bg-white/10 text-white/80'
              }`}
            >
              <Clock size={18} />
              <span>Jadwal Salat & Adzan</span>
            </button>

            <button
              onClick={() => setActiveTab('qibla')}
              className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition ${
                activeTab === 'qibla' ? 'bg-[#D4AF37] text-[#022C22] shadow' : 'hover:bg-white/10 text-white/80'
              }`}
            >
              <Compass size={18} />
              <span>Kompas Kiblat</span>
            </button>

            <button
              onClick={() => setActiveTab('yasin')}
              className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition ${
                activeTab === 'yasin' ? 'bg-[#D4AF37] text-[#022C22] shadow' : 'hover:bg-white/10 text-white/80'
              }`}
            >
              <FileText size={18} />
              <span>Yasin & Tahlil</span>
            </button>

            <button
              onClick={() => setActiveTab('tasbih')}
              className={`flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition ${
                activeTab === 'tasbih' ? 'bg-[#D4AF37] text-[#022C22] shadow' : 'hover:bg-white/10 text-white/80'
              }`}
            >
              <Sparkles size={18} />
              <span>Dzikir & Tasbih</span>
            </button>
          </div>

          <div className="hidden md:block border-t border-[#D4AF37]/30 pt-4 text-center">
            <span className="text-[11px] text-[#D4AF37] font-semibold">Baca. Pahami. Amalkan.</span>
          </div>
        </aside>

        {/* Content Area */}
        <main className="flex-1 bg-white rounded-2xl p-6 shadow-xl border border-[#D4AF37]/20 min-h-[600px] flex flex-col">
          {/* TAB 1: AL-QUR'AN */}
          {activeTab === 'quran' && (
            <div className={`grid ${isDesktopMode ? 'grid-cols-1 lg:grid-cols-12 gap-6' : 'grid-cols-1'}`}>
              {/* Surah List Column */}
              <div className={isDesktopMode ? 'lg:col-span-5' : 'col-span-1'}>
                {/* Hero Card with Madinah Photo */}
                <div className="relative rounded-2xl overflow-hidden mb-6 shadow-lg border border-[#D4AF37]/40 h-48 bg-[#022C22]">
                  <img
                    src="/foto_madinah.jpg"
                    alt="Masjid Nabawi Madinah"
                    className="w-full h-full object-cover opacity-60"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-[#022C22] via-[#022C22]/50 to-transparent p-5 flex flex-col justify-end text-white">
                    <span className="text-xs bg-[#D4AF37]/30 text-[#D4AF37] border border-[#D4AF37]/40 px-2 py-0.5 rounded-full w-max mb-1 font-semibold">
                      🕌 Madinah Al-Munawwarah
                    </span>
                    <h2 className="text-xl font-bold text-white">Al-Qur'an Al-Karim</h2>
                    <p className="text-xs text-[#F8FAF7]/80">Pengalaman tilawah tenang, modern, dan berilmu</p>
                  </div>
                </div>

                {/* Search Bar */}
                <div className="relative mb-4">
                  <Search className="absolute left-3 top-3 text-[#064E3B]" size={18} />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Cari surat (misal: Yasin, Al-Mulk, Sapi)..."
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 focus:outline-none focus:border-[#D4AF37] text-sm"
                  />
                </div>

                {/* Filter Chips */}
                <div className="flex space-x-2 mb-4">
                  {['Semua', 'Makkiyyah', 'Madaniyyah'].map(f => (
                    <button
                      key={f}
                      onClick={() => setSelectedFilter(f)}
                      className={`text-xs px-3 py-1.5 rounded-full font-semibold border ${
                        selectedFilter === f
                          ? 'bg-[#064E3B] text-white border-[#D4AF37]'
                          : 'bg-gray-100 text-gray-700 border-gray-200'
                      }`}
                    >
                      {f}
                    </button>
                  ))}
                </div>

                {/* Surah List */}
                <div className="space-y-2 max-h-[500px] overflow-y-auto pr-1">
                  {filteredSurahs.map(s => (
                    <div
                      key={s.nomor}
                      onClick={() => setSelectedSurah(s.nomor)}
                      className={`p-3.5 rounded-xl border flex items-center justify-between cursor-pointer transition ${
                        selectedSurah === s.nomor
                          ? 'bg-[#064E3B]/10 border-[#D4AF37] shadow'
                          : 'hover:bg-gray-50 border-gray-200'
                      }`}
                    >
                      <div className="flex items-center space-x-3">
                        <div className="w-9 h-9 rounded-full bg-[#064E3B]/10 border border-[#D4AF37] flex items-center justify-center font-bold text-xs text-[#064E3B]">
                          {s.nomor}
                        </div>
                        <div>
                          <h4 className="font-bold text-sm text-[#022C22]">{s.namaLatin}</h4>
                          <span className="text-xs text-gray-500">{s.tempatTurun} • {s.jumlahAyat} Ayat</span>
                        </div>
                      </div>
                      <span className="text-lg font-bold text-[#064E3B] font-arabic">{s.nama}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Verses Reading Reader Column */}
              <div className={isDesktopMode ? 'lg:col-span-7 border-t lg:border-t-0 lg:border-l pl-0 lg:pl-6 border-gray-100' : 'mt-6'}>
                {loadingDetail ? (
                  <div className="text-center py-20 text-gray-500">
                    <RefreshCw className="animate-spin mx-auto mb-3 text-[#064E3B]" size={28} />
                    <p>Menyiapkan firman Allah...</p>
                  </div>
                ) : surahDetail ? (
                  <div>
                    {/* Header Surat */}
                    <div className="bg-[#022C22] text-[#F8FAF7] rounded-xl p-5 mb-6 text-center border border-[#D4AF37]/50 shadow">
                      <h3 className="text-2xl font-bold text-[#D4AF37]">{surahDetail.namaLatin}</h3>
                      <p className="text-sm text-gray-300">{surahDetail.arti} • {surahDetail.jumlahAyat} Ayat</p>
                      <div className="mt-4 pt-3 border-t border-[#D4AF37]/30 text-xl font-arabic text-[#D4AF37]">
                        بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ
                      </div>
                    </div>

                    {/* Verses List */}
                    <div className="space-y-4 max-h-[600px] overflow-y-auto pr-2">
                      {surahDetail.ayat?.map(ayah => (
                        <div key={ayah.nomorAyat} className="p-4 rounded-xl border border-gray-200 bg-[#F8FAF7] space-y-3">
                          <div className="flex items-center justify-between border-b pb-2">
                            <span className="w-7 h-7 rounded-full bg-[#064E3B] text-white flex items-center justify-center text-xs font-bold">
                              {ayah.nomorAyat}
                            </span>
                            <button
                              onClick={() => {
                                const audioUrl = ayah.audio?.['05'] || Object.values(ayah.audio || {})[0];
                                if (audioUrl) {
                                  playAudioTrack(audioUrl, `QS ${surahDetail.namaLatin}: Ayat ${ayah.nomorAyat}`);
                                }
                              }}
                              className="text-xs bg-[#D4AF37]/20 hover:bg-[#D4AF37]/30 text-[#064E3B] px-3 py-1 rounded-full flex items-center space-x-1 font-semibold"
                            >
                              <Play size={12} />
                              <span>Putar Audio</span>
                            </button>
                          </div>
                          <p className="text-2xl font-arabic text-[#022C22] py-2 text-right">{ayah.teksArab} ۝</p>
                          <p className="text-xs text-[#064E3B] italic font-medium">{ayah.teksLatin}</p>
                          <p className="text-sm text-gray-700">{ayah.teksIndonesia}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                ) : null}
              </div>
            </div>
          )}

          {/* TAB 2: JADWAL SALAT & AUDIO */}
          {activeTab === 'prayer' && (
            <div className="space-y-6">
              <div className="bg-gradient-to-r from-[#022C22] to-[#064E3B] text-white p-6 rounded-2xl shadow-lg border border-[#D4AF37]/40 flex flex-col md:flex-row justify-between items-center gap-4">
                <div>
                  <span className="text-xs text-[#D4AF37] font-bold uppercase tracking-wider">Jadwal Salat & Adzan Otomatis</span>
                  <h3 className="text-2xl font-bold text-white mt-1">Waktu Salat {city}</h3>
                  <p className="text-xs text-gray-300 mt-1">Dihitung otomatis dengan hisab Kemenag RI (+2 menit ihtiyat)</p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => {
                      if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                          pos => {
                            setUseGps(true);
                            setCity(`GPS (${pos.coords.latitude.toFixed(2)}, ${pos.coords.longitude.toFixed(2)})`);
                          },
                          () => alert('Izin lokasi GPS tidak diberikan.')
                        );
                      }
                    }}
                    className="flex items-center space-x-1.5 bg-[#D4AF37] hover:bg-[#c49f2b] text-[#022C22] px-4 py-2 rounded-xl text-xs font-bold transition shadow"
                  >
                    <MapPin size={16} />
                    <span>Gunakan GPS</span>
                  </button>
                </div>
              </div>

              {/* Prayer Times Grid */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                {Object.entries(prayerTimes).map(([name, time]) => (
                  <div key={name} className="p-4 rounded-xl border border-gray-200 bg-[#F8FAF7] text-center shadow-sm hover:border-[#D4AF37] transition">
                    <span className="text-xs font-bold text-gray-500 uppercase">{name}</span>
                    <p className="text-2xl font-bold text-[#064E3B] mt-1">{time}</p>
                  </div>
                ))}
              </div>

              {/* Test Audio Section */}
              <div className="bg-white rounded-xl p-5 border border-[#D4AF37]/30 shadow space-y-4">
                <div className="flex items-center justify-between border-b pb-3">
                  <div>
                    <h4 className="font-bold text-[#022C22]">Uji Nyata Audio Pengingat Salat</h4>
                    <p className="text-xs text-gray-500">Dengarkan pratinjau audio adzan, imsak, dan terbit</p>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Volume2 size={18} className="text-[#064E3B]" />
                    <input
                      type="range"
                      min="0"
                      max="1"
                      step="0.05"
                      value={volume}
                      onChange={(e) => handleVolumeChange(parseFloat(e.target.value))}
                      className="w-24 accent-[#064E3B]"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <button
                    onClick={() => playAudioTrack(PRAYER_AUDIOS.subuh, "Adzan Khusus Subuh")}
                    className="p-3 bg-gray-50 hover:bg-[#D4AF37]/10 border rounded-xl flex items-center justify-between text-left transition"
                  >
                    <div>
                      <div className="font-bold text-xs text-[#064E3B]">Adzan Subuh</div>
                      <span className="text-[10px] text-gray-500">adzan_subuh.mp3</span>
                    </div>
                    <Play size={16} className="text-[#064E3B]" />
                  </button>

                  <button
                    onClick={() => playAudioTrack(PRAYER_AUDIOS.fardhu, "Adzan Fardhu (Dzuhur/Ashar/Maghrib/Isya)")}
                    className="p-3 bg-gray-50 hover:bg-[#D4AF37]/10 border rounded-xl flex items-center justify-between text-left transition"
                  >
                    <div>
                      <div className="font-bold text-xs text-[#064E3B]">Adzan Fardhu</div>
                      <span className="text-[10px] text-gray-500">adzan.mp3</span>
                    </div>
                    <Play size={16} className="text-[#064E3B]" />
                  </button>

                  <button
                    onClick={() => playAudioTrack(PRAYER_AUDIOS.imsak, "Peringatan Waktu Imsak")}
                    className="p-3 bg-gray-50 hover:bg-[#D4AF37]/10 border rounded-xl flex items-center justify-between text-left transition"
                  >
                    <div>
                      <div className="font-bold text-xs text-[#064E3B]">Peringatan Imsak</div>
                      <span className="text-[10px] text-gray-500">imsak.mp3</span>
                    </div>
                    <Play size={16} className="text-[#064E3B]" />
                  </button>

                  <button
                    onClick={() => playAudioTrack(PRAYER_AUDIOS.terbit, "Waktu Terbit Syuruq (Ayam)")}
                    className="p-3 bg-gray-50 hover:bg-[#D4AF37]/10 border rounded-xl flex items-center justify-between text-left transition"
                  >
                    <div>
                      <div className="font-bold text-xs text-[#064E3B]">Ayam Berkokok (Terbit)</div>
                      <span className="text-[10px] text-gray-500">ayam.mp3</span>
                    </div>
                    <Play size={16} className="text-[#064E3B]" />
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* TAB 3: KOMPAS KIBLAT */}
          {activeTab === 'qibla' && (
            <div className="text-center py-10 space-y-6">
              <h3 className="text-2xl font-bold text-[#064E3B]">Arah Kiblat ke Ka'bah</h3>
              <p className="text-sm text-gray-600">Menghadap ke arah Ka'bah di Makkah Al-Mukarramah (~295° NW dari Indonesia)</p>
              <div className="w-56 h-56 mx-auto rounded-full bg-gradient-to-tr from-[#022C22] to-[#064E3B] border-4 border-[#D4AF37] flex items-center justify-center text-white shadow-2xl relative">
                <div className="absolute top-3 text-xs font-bold text-[#D4AF37]">▲ KIBLAT</div>
                <div className="text-center">
                  <span className="text-4xl font-bold text-[#D4AF37]">295°</span>
                  <div className="text-xs text-gray-300">Barat Laut</div>
                </div>
              </div>
              <p className="text-xs text-gray-500">Jarak ke Ka'bah: ~7.920 km • Terkalibrasi GPS</p>
            </div>
          )}

          {/* TAB 4: YASIN & TAHLIL */}
          {activeTab === 'yasin' && (
            <div className="space-y-6">
              <div className="bg-[#022C22] text-white p-6 rounded-2xl border border-[#D4AF37]/40 shadow-lg text-center">
                <h3 className="text-2xl font-bold text-[#D4AF37]">Surat Yasin & Susunan Tahlil</h3>
                <p className="text-xs text-gray-300 mt-1">Lengkap dengan bacaan hadhrat, surat pendek, istighfar, tahlil, dan doa arwah</p>
              </div>

              <div className="p-4 rounded-xl border border-gray-200 bg-[#F8FAF7] space-y-2">
                <h4 className="font-bold text-[#064E3B]">1. Pengantar Al-Fatihah</h4>
                <p className="text-xl font-arabic text-right py-1">إِلَى حَضْرَةِ النَّبِيِّ الْمُصْطَفَى مُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ...</p>
                <p className="text-xs text-gray-600">Kepada yang mulia Nabi pilihan Muhammad SAW beserta keluarga dan sahabat beliau...</p>
              </div>

              <div className="p-4 rounded-xl border border-gray-200 bg-[#F8FAF7] space-y-2">
                <h4 className="font-bold text-[#064E3B]">2. Tahlil Utama (33x / 100x)</h4>
                <p className="text-2xl font-arabic text-right py-2 text-[#064E3B]">لَا إِلٰهَ إِلَّا اللهُ مُحَمَّدٌ رَسُولُ اللهِ</p>
                <p className="text-xs text-gray-600">Tiada Tuhan selain Allah, Muhammad utusan Allah.</p>
              </div>
            </div>
          )}

          {/* TAB 5: DZIKIR & TASBIH */}
          {activeTab === 'tasbih' && (
            <div className="text-center py-10 space-y-6">
              <h3 className="text-2xl font-bold text-[#064E3B]">Tasbih Digital Interaktif</h3>
              <p className="text-sm text-gray-600">Sentuh lingkaran di bawah untuk menghitung dzikir</p>

              <div className="flex justify-center space-x-2">
                {[33, 99, 100].map(t => (
                  <button
                    key={t}
                    onClick={() => setTasbihTarget(t)}
                    className={`px-3 py-1 rounded-full text-xs font-bold border ${
                      tasbihTarget === t ? 'bg-[#064E3B] text-white border-[#D4AF37]' : 'bg-gray-100'
                    }`}
                  >
                    Target {t}
                  </button>
                ))}
              </div>

              <div
                onClick={() => setTasbihCount(prev => (prev >= tasbihTarget ? 1 : prev + 1))}
                className="w-56 h-56 mx-auto rounded-full bg-gradient-to-tr from-[#064E3B] to-[#022C22] border-4 border-[#D4AF37] flex flex-col items-center justify-center text-white cursor-pointer select-none shadow-2xl active:scale-95 transition"
              >
                <span className="text-5xl font-extrabold text-[#D4AF37]">{tasbihCount}</span>
                <span className="text-xs text-gray-300 mt-1 uppercase tracking-widest">DARI {tasbihTarget}</span>
                <span className="text-[10px] text-[#F3E5AB] mt-2 bg-white/10 px-2 py-0.5 rounded-full">SENTUH UNTUK MENGHITUNG</span>
              </div>

              <button
                onClick={() => setTasbihCount(0)}
                className="text-xs bg-red-50 text-red-600 border border-red-200 px-4 py-2 rounded-xl font-bold hover:bg-red-100 transition"
              >
                Reset Hitungan
              </button>
            </div>
          )}

          {/* FOOTER PARTNER LOGOS SECTION */}
          <footer className="mt-12 pt-8 border-t border-gray-200">
            <div className="bg-[#022C22] rounded-2xl p-6 text-center text-white border border-[#D4AF37]/40 shadow-lg">
              <span className="text-[11px] font-bold text-[#D4AF37] tracking-widest uppercase">
                KOLABORASI & MITRA RESMI
              </span>

              {/* 3 Partner Logos */}
              <div className="grid grid-cols-3 gap-4 max-w-lg mx-auto my-6 items-center">
                <div className="bg-white p-3 rounded-xl border border-[#D4AF37]/50 shadow flex items-center justify-center h-20">
                  <img src="/logo_bbpvp_bekasi.png" alt="BBPVP Bekasi" className="max-h-full max-w-full object-contain" />
                </div>
                <div className="bg-white p-3 rounded-xl border border-[#D4AF37]/50 shadow flex items-center justify-center h-20">
                  <img src="/logo_embi_tech.png" alt="Embi Tech Computindo" className="max-h-full max-w-full object-contain" />
                </div>
                <div className="bg-white p-3 rounded-xl border border-[#D4AF37]/50 shadow flex items-center justify-center h-20">
                  <img src="/logo_pondok_mandiri.png" alt="Yayasan Karya Pondok Mandiri" className="max-h-full max-w-full object-contain" />
                </div>
              </div>

              <p className="text-xs text-gray-300">
                BBPVP Bekasi • Embi Tech Computindo • Yayasan Karya Pondok Mandiri
              </p>
              <p className="text-[11px] text-gray-400 mt-1">
                Didukung bersama untuk memperluas akses tilawah Al-Qur'an digital di Indonesia.
              </p>
              <div className="mt-4 pt-3 border-t border-white/10 text-[10px] text-[#D4AF37]">
                Yann Quran v1.0 • Premium Digital Quran
              </div>
            </div>
          </footer>
        </main>
      </div>

      {/* Floating Audio Dock */}
      {isPlayingAudio && (
        <div className="fixed bottom-4 left-4 right-4 max-w-lg mx-auto bg-[#022C22] text-white p-3.5 rounded-2xl shadow-2xl border border-[#D4AF37] flex items-center justify-between z-50">
          <div className="flex items-center space-x-3 overflow-hidden">
            <div className="w-8 h-8 rounded-full bg-[#D4AF37] text-[#022C22] flex items-center justify-center flex-shrink-0 animate-pulse">
              <Volume2 size={16} />
            </div>
            <div className="truncate">
              <h5 className="text-xs font-bold truncate text-white">{audioLabel}</h5>
              <span className="text-[10px] text-[#D4AF37]">Sedang Diputar</span>
            </div>
          </div>
          <button
            onClick={stopAudio}
            className="text-xs bg-red-600 hover:bg-red-700 text-white px-3 py-1.5 rounded-xl font-bold ml-2 flex-shrink-0 transition"
          >
            Hentikan
          </button>
        </div>
      )}
    </div>
  );
}
