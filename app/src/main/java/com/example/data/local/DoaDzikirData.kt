package com.example.data.local

data class DoaItem(
    val id: String,
    val title: String,
    val category: String, // "Harian", "Shalat", "Pagi", "Petang"
    val arab: String,
    val latin: String,
    val translation: String,
    val source: String = "",
    val count: Int = 1
)

object DoaDzikirData {

    val ALL_DOA: List<DoaItem> = listOf(
        // Doa Sehari-hari
        DoaItem(
            id = "d1",
            title = "Doa Bangun Tidur",
            category = "Harian",
            arab = "اَلْحَمْدُ لِلّٰهِ الَّذِيْ أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُوْرُ",
            latin = "Alhamdulillāhil-lażī aḥyānā ba'da mā amātanā wa ilaihin-nusyūr.",
            translation = "Segala puji bagi Allah yang telah menghidupkan kami setelah mematikan kami (tidur), dan hanya kepada-Nya kami akan dibangkitkan.",
            source = "HR. Bukhari & Muslim"
        ),
        DoaItem(
            id = "d2",
            title = "Doa Sebelum Tidur",
            category = "Harian",
            arab = "بِاسْمِكَ اللّٰهُمَّ أَحْيَا وَأَمُوْتُ",
            latin = "Bismikallāhumma aḥyā wa amūt.",
            translation = "Dengan nama-Mu ya Allah, aku hidup dan aku mati.",
            source = "HR. Bukhari"
        ),
        DoaItem(
            id = "d3",
            title = "Doa Masuk Masjid",
            category = "Harian",
            arab = "اَللّٰهُمَّ افْتَحْ لِيْ أَبْوَابَ رَحْمَتِكَ",
            latin = "Allāhummaftaḥ lī abwāba raḥmatik.",
            translation = "Ya Allah, bukakanlah untukku pintu-pintu rahmat-Mu.",
            source = "HR. Muslim"
        ),
        DoaItem(
            id = "d4",
            title = "Doa Keluar Masjid",
            category = "Harian",
            arab = "اَللّٰهُمَّ إِنِّيْ أَسْأَلُكَ مِنْ فَضْلِكَ",
            latin = "Allāhumma innī as'aluka min faḍlik.",
            translation = "Ya Allah, sesungguhnya aku memohon sebagian dari karunia-Mu.",
            source = "HR. Muslim"
        ),
        DoaItem(
            id = "d5",
            title = "Doa Kedua Orang Tua",
            category = "Harian",
            arab = "رَبِّ اغْفِرْ لِيْ وَلِوَالِدَيَّ وَارْحَمْهُمَا كَمَا رَبَّيَانِيْ صَغِيْرًا",
            latin = "Rabbigfir lī wa liwālidayya warḥamhumā kamā rabbayānī ṣagīrā.",
            translation = "Wahai Tuhanku, ampunilah aku dan kedua orang tuaku, dan sayangilah keduanya sebagaimana mereka menyayangiku di waktu kecil.",
            source = "QS. Al-Isra': 24"
        ),
        DoaItem(
            id = "d6",
            title = "Doa Sapu Jagad (Kebaikan Dunia Akhirat)",
            category = "Harian",
            arab = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latin = "Rabbanā ātinā fid-dunyā ḥasanatan wa fil-ākhirati ḥasanatan wa qinā 'ażāban-nār.",
            translation = "Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, dan lindungilah kami dari azab neraka.",
            source = "QS. Al-Baqarah: 201"
        ),
        DoaItem(
            id = "d7",
            title = "Doa Keluar Rumah",
            category = "Harian",
            arab = "بِسْمِ اللّٰهِ تَوَكَّلْتُ عَلَى اللّٰهِ لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللّٰهِ",
            latin = "Bismillāhi tawakkaltu 'alallāh, lā ḥawla wa lā quwwata illā billāh.",
            translation = "Dengan nama Allah, aku bertawakal kepada Allah. Tiada daya dan kekuatan kecuali dengan pertolongan Allah.",
            source = "HR. Abu Daud & Tirmidzi"
        ),

        // Dzikir Setelah Shalat Fardhu
        DoaItem(
            id = "s1",
            title = "Istighfar Setelah Shalat",
            category = "Shalat",
            arab = "أَسْتَغْفِرُ اللّٰهَ الْعَظِيْمَ (٣×)",
            latin = "Astaghfirullāhal-'aẓīm. (3x)",
            translation = "Aku memohon ampun kepada Allah Yang Maha Agung.",
            source = "HR. Muslim",
            count = 3
        ),
        DoaItem(
            id = "s2",
            title = "Doa Keselamatan (Antas-Salam)",
            category = "Shalat",
            arab = "اَللّٰهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
            latin = "Allāhumma antas-salām, wa minkas-salām, tabārakta yā żal-jalāli wal-ikrām.",
            translation = "Ya Allah, Engkaulah Yang Memberi Keselamatan, dan dari-Mulah keselamatan. Mahaberkah Engkau, wahai Dzat Pemilik Keagungan dan Kemuliaan.",
            source = "HR. Muslim"
        ),
        DoaItem(
            id = "s3",
            title = "Tasbih, Tahmid, Takbir",
            category = "Shalat",
            arab = "سُبْحَانَ اللّٰهِ (٣٣×) ، اَلْحَمْدُ لِلّٰهِ (٣٣×) ، اَللّٰهُ أَكْبَرُ (٣٣×)",
            latin = "Subḥānallāh (33x), Al-ḥamdulillāh (33x), Allāhu Akbar (33x)",
            translation = "Maha Suci Allah (33x), Segala puji bagi Allah (33x), Allah Maha Besar (33x)",
            source = "HR. Muslim",
            count = 33
        ),

        // Dzikir Pagi
        DoaItem(
            id = "p1",
            title = "Sayyidul Istighfar",
            category = "Pagi",
            arab = "اَللّٰهُمَّ أَنْتَ رَبِّيْ لَا إِلٰهَ إِلَّا أَنْتَ، خَلَقْتَنِيْ وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوْذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوْءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوْءُ بِذَنْبِيْ فَاغْفِرْ لِيْ فَإِنَّهُ لَا يَغْفِرُ الذُّنُوْبَ إِلَّا أَنْتَ",
            latin = "Allāhumma anta rabbī lā ilāha illā anta, khalaqtanī wa anā 'abduka, wa anā 'alā 'ahdika wa wa'dika mastaṭa'tu, a'ūżu bika min syarri mā ṣana'tu, abū'u laka bini'matika 'alayya, wa abū'u biżanbī fagfir lī fa'innahū lā yagfiruz-żunūba illā ant.",
            translation = "Ya Allah, Engkaulah Tuhanku, tiada tuhan selain Engkau. Engkau yang menciptakan aku dan aku adalah hamba-Mu. Aku memegang teguh perjanjian dan janji-Mu semampuku. Aku berlindung kepada-Mu dari keburukan yang kuperbuat. Aku mengakui nikmat-Mu atasku dan dosaku, maka ampunilah aku...",
            source = "HR. Bukhari",
            count = 1
        ),
        DoaItem(
            id = "p2",
            title = "Doa Pagi Hari (Asbahna)",
            category = "Pagi",
            arab = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلّٰهِ وَالْحَمْدُ لِلّٰهِ، لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيْكَ لَهُ",
            latin = "Aṣbaḥnā wa aṣbaḥal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā syarīka lah.",
            translation = "Kami telah memasuki pagi hari dan kerajaan hanya milik Allah. Segala puji bagi Allah, tiada tuhan selain Allah Yang Maha Esa, tiada sekutu bagi-Nya.",
            source = "HR. Muslim"
        ),

        // Dzikir Petang
        DoaItem(
            id = "m1",
            title = "Doa Petang Hari (Amsayna)",
            category = "Petang",
            arab = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلّٰهِ وَالْحَمْدُ لِلّٰهِ، لَا إِلٰهَ إِلَّا اللّٰهُ وَحْدَهُ لَا شَرِيْكَ لَهُ",
            latin = "Amsainā wa amsal-mulku lillāh, wal-ḥamdu lillāh, lā ilāha illallāhu waḥdahū lā syarīka lah.",
            translation = "Kami telah memasuki waktu petang dan kerajaan hanya milik Allah. Segala puji bagi Allah, tiada sesembahan yang berhak disembah selain Allah semata, tidak ada sekutu bagi-Nya.",
            source = "HR. Muslim"
        ),
        DoaItem(
            id = "m2",
            title = "Perlindungan Dari Marabahaya (Bismillahilladzi)",
            category = "Petang",
            arab = "بِسْمِ اللّٰهِ الَّذِيْ لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيْعُ الْعَلِيْمُ (٣×)",
            latin = "Bismillāhil-lażī lā yaḍurru ma'asmihī syai'un fil-arḍi wa lā fis-samā'i wa huwas-samī'ul-'alīm. (3x)",
            translation = "Dengan nama Allah yang bersama nama-Nya tidak ada sesuatu pun di bumi maupun di langit yang dapat mendatangkan bahaya. Dia-lah Yang Maha Mendengar lagi Maha Mengetahui.",
            source = "HR. Abu Daud & Tirmidzi",
            count = 3
        )
    )
}
