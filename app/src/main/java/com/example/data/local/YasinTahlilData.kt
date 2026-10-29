package com.example.data.local

import com.example.data.model.Ayat

data class TahlilItem(
    val id: Int,
    val title: String,
    val arab: String,
    val latin: String,
    val translation: String,
    val count: String = "1x"
)

object YasinTahlilData {

    val TAHLIL_READINGS: List<TahlilItem> = listOf(
        TahlilItem(
            id = 1,
            title = "Pengantar Al-Fatihah (Hadhrat)",
            arab = "إِلَى حَضْرَةِ النَّبِيِّ الْمُصْطَفَى مُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ وَآلِهِ وَأَصْحَابِهِ وَأَزْوَاجِهِ وَذُرِّيَّاتِهِ، لَهُمُ الْفَاتِحَةُ...",
            latin = "Ilaa hadhratin-nabiyyil-musthafaa Muhammadin shallallahu 'alaihi wa sallama wa aalihii wa ashhaabihii wa azwaajihii wa dzurriyyatihii, lahumul-Faatihah...",
            translation = "Kepada yang mulia Nabi pilihan Muhammad SAW, segenap keluarga, sahabat, istri, dan keturunan beliau, (bacaan) Al-Fatihah kami haturkan...",
            count = "1x"
        ),
        TahlilItem(
            id = 2,
            title = "Surat Al-Ikhlas",
            arab = "قُلْ هُوَ اللَّهُ أَحَدٌ ۞ اللَّهُ الصَّمَدُ ۞ لَمْ يَلِدْ وَلَمْ يُولَدْ ۞ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            latin = "Qul huwallāhu aḥad. Allāhuṣ-ṣamad. Lam yalid wa lam yūlad. Wa lam yakul lahū kufuwan aḥad.",
            translation = "Katakanlah (Muhammad), 'Dialah Allah, Yang Maha Esa. Allah tempat meminta segala sesuatu. (Allah) tidak beranak dan tidak pula diperanakkan. Dan tidak ada sesuatu yang setara dengan Dia.'",
            count = "3x"
        ),
        TahlilItem(
            id = 3,
            title = "Tahlil & Takbir Antar Surat",
            arab = "لَا إِلٰهَ إِلَّا اللهُ وَاللهُ أَكْبَرُ، وَلِلّٰهِ الْحَمْدُ",
            latin = "Laa ilaaha illallaahu wallaahu akbar, wa lillaahil-hamd.",
            translation = "Tiada Tuhan selain Allah, Allah Maha Besar, dan segala puji hanya bagi Allah.",
            count = "1x"
        ),
        TahlilItem(
            id = 4,
            title = "Surat Al-Falaq",
            arab = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۞ مِن شَرِّ مَا خَلَقَ ۞ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۞ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۞ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
            latin = "Qul a'ūżu birabbil-falaq. Min syarri mā khalaq. Wa min syarri gāsiqin iżā waqab. Wa min syarrin-naffāṡāti fil-'uqad. Wa min syarri ḥāsidin iżā ḥasad.",
            translation = "Katakanlah, 'Aku berlindung kepada Tuhan yang menguasai subuh, dari kejahatan makhluk yang Dia ciptakan, dan dari kejahatan malam apabila telah gelap gulita, dan dari kejahatan wanita-wanita penyihir yang meniup pada buhul-buhulnya, dan dari kejahatan orang yang dengki apabila dia dengki.'",
            count = "1x"
        ),
        TahlilItem(
            id = 5,
            title = "Surat An-Nas",
            arab = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۞ مَلِكِ النَّاسِ ۞ إِلَٰهِ النَّاسِ ۞ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۞ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۞ مِنَ الْجِنَّةِ وَالنَّاسِ",
            latin = "Qul a'ūżu birabbin-nās. Malikin-nās. Ilāhin-nās. Min syarril-waswāsil-khannās. Allażī yuwaswisu fī ṣudūrin-nās. Minal-jinnati wan-nās.",
            translation = "Katakanlah, 'Aku berlindung kepada Tuhannya manusia, Raja manusia, Sembahan manusia, dari kejahatan bisikan setan yang bersembunyi, yang membisikkan (kejahatan) ke dalam dada manusia, dari golongan jin dan manusia.'",
            count = "1x"
        ),
        TahlilItem(
            id = 6,
            title = "Awal Surat Al-Baqarah (1-5)",
            arab = "الم ۞ ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ ۞ الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ ۞ وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ ۞ أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ",
            latin = "Alif-Lām-Mīm. Zālikal-kitābu lā raiba fīh, hudal lil-muttaqīn. Allażīna yu'minūna bil-gaibi wa yuqīmūnaṣ-ṣalāta wa mimmā razaqnāhum yunfiqūn. Wallażīna yu'minūna bimā unzila ilaika wa mā unzila min qablik, wa bil-ākhirati hum yūqinūn. Ulā'ika 'alā hudam mir rabbihim wa ulā'ika humul-mufliḥūn.",
            translation = "Alif Lam Mim. Kitab (Al-Qur'an) ini tidak ada keraguan padanya; petunjuk bagi mereka yang bertakwa...",
            count = "1x"
        ),
        TahlilItem(
            id = 7,
            title = "Ayat Kursi (Al-Baqarah: 255)",
            arab = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            latin = "Allāhu lā ilāha illā huwal-ḥayyul-qayyūm, lā ta'khużuhū sinatuw wa lā na'ūm, lahū mā fis-samāwāti wa mā fil-arḍ, man żallażī yasyfa'u 'indahū illā bi'iżnih, ya'lamu mā baina aidīhim wa mā khalfahum, wa lā yuḥīṭūna bisyai'im min 'ilmihī illā bimā syā', wasi'a kursiyyuhus-samāwāti wal-arḍ, wa lā ya'ūduhū ḥifẓuhumā, wa huwal-'aliyyul-'aẓīm.",
            translation = "Allah, tidak ada tuhan selain Dia. Yang Mahahidup, yang terus-menerus mengurus makhluk-Nya, tidak mengantuk dan tidak tidur...",
            count = "1x"
        ),
        TahlilItem(
            id = 8,
            title = "Istighfar",
            arab = "أَسْتَغْفِرُ اللهَ الْعَظِيْمَ",
            latin = "Astaghfirullaahal-'azhiim.",
            translation = "Aku memohon ampunan kepada Allah Yang Maha Agung.",
            count = "3x"
        ),
        TahlilItem(
            id = 9,
            title = "Pernyataan Utama Tahlil",
            arab = "لَا إِلٰهَ إِلَّا اللهُ، لَا إِلٰهَ إِلَّا اللهُ، لَا إِلٰهَ إِلَّا اللهُ مُحَمَّدٌ رَسُولُ اللهِ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ",
            latin = "Laa ilaaha illallaahu, Laa ilaaha illallaahu, Laa ilaaha illallaahu Muhammadur Rasuulullaahi shallallahu 'alaihi wa sallam.",
            translation = "Tiada Tuhan selain Allah. Muhammad adalah utusan Allah semoga shalawat dan keselamatan tercurah kepadanya.",
            count = "33x / 100x"
        ),
        TahlilItem(
            id = 10,
            title = "Tasbih & Tahmid",
            arab = "سُبْحَانَ اللهِ وَبِحَمْدِهِ سُبْحَانَ اللهِ الْعَظِيمِ",
            latin = "Subhaanallaahi wa bihamdihii, Subhaanallaahil-'azhiim.",
            translation = "Maha Suci Allah dan segala puji bagi-Nya, Maha Suci Allah Yang Maha Agung.",
            count = "33x"
        ),
        TahlilItem(
            id = 11,
            title = "Shalawat Nabi",
            arab = "اللَّهُمَّ صَلِّ عَلَى حَبِيبِكَ سَيِّدِنَا مُحَمَّدٍ وَآلِهِ وَصَحْبِهِ وَسَلِّمْ",
            latin = "Allaahumma shalli 'alaa habiibika sayyidinaa Muhammadin wa aalihii wa shahbihii wa sallim.",
            translation = "Ya Allah, limpahkanlah rahmat dan salam kepada kekasih-Mu junjungan kami Nabi Muhammad beserta segenap keluarga dan sahabat beliau.",
            count = "3x"
        ),
        TahlilItem(
            id = 12,
            title = "Doa Tahlil & Doa Arwah",
            arab = "اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَمِيْنَ حَمْدًا يُوَافِي نِعَمَهُ وَيُكَافِئُ مَزِيْدَهُ. اَللّٰهُمَّ صَلِّ عَلَى سَيِّدِنَا مُحَمَّدٍ وَعَلَى آلِ سَيِّدِنَا مُحَمَّدٍ. اَللّٰهُمَّ تَقَبَّلْ وَأَوْصِلْ ثَوَابَ مَا قَرَأْنَاهُ مِنْ كِتَابِكَ الْعَزِيْزِ، وَمَا هَلَّلْنَا وَمَا سَبَّحْنَا وَمَا اسْتَغْفَرْنَا وَمَا صَلَّيْنَا عَلَى حَبِيْبِكَ سَيِّدِنَا مُحَمَّدٍ، هَدِيَّةً وَاصِلَةً وَرَحْمَةً نَازِلَةً وَبَرَكَةً شَامِلَةً إِلَى حَضْرَةِ حَبِيْبِنَا وَشَفِيْعِنَا رَسُوْلِ اللهِ مُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ، ثُمَّ إِلَى أَرْوَاحِ جَمِيْعِ إِخْوَانِهِ مِنَ الْأَنْبِيَاءِ وَالْمُرْسَلِيْنَ وَالْأَوْلِيَاءِ وَالشُّهَدَاءِ وَالصَّالِحِيْنَ، وَخُصُوْصًا إِلَى أَرْوَاحِ مَنْ جَمَعْنَا هٰهُنَا بِسَبَبِهِمْ وَلِأَجْلِهِمْ. اَللّٰهُمَّ اغْفِرْ لَهُمْ وَارْحَمْهُمْ وَعَافِهِمْ وَاعْفُ عَنْهُمْ. اَللّٰهُمَّ اجْعَلْ قُبُوْرَهُمْ رَوْضَةً مِنْ رِيَاضِ الْجِنَانِ، وَلَا تَجْعَلْهَا حُفْرَةً مِنْ حُفَرِ النِّيْرَانِ. رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ.",
            latin = "Alhamdulillaahi rabbil-'aalamiin, hamdan yuwaafii ni'amahuu wa yukaafi'u maziidah. Allaahumma shalli 'alaa sayyidinaa Muhammadin wa 'alaa aali sayyidinaa Muhammad. Allaahumma taqabbal wa aushil tsawaaba maa qara'naahu min kitaabikal-'aziiz, wa maa hallalnaa wa maa sabbahnaa wa mastaghfarnaa wa maa shallainaa 'alaa habiibika sayyidinaa Muhammad, hadiyyatan waashilatan wa rahmatan naazilatan wa barakatan syaamilatan ilaa hadhrati habiibinaa wa syafii'inaa rasuulillaahi Muhammadin shallallaahu 'alaihi wa sallam, tsumma ilaa arwaahi jami'i ikhwaanihii minal-anbiyaa'i wal-mursaliin... Allaahummaghfir lahum warhamhum wa 'aafihim wa'fu 'anhum. Rabbanaa aatinaa fid-dunyaa hasanatan wa fil-aakhirati hasanatan wa qinaa 'adzaaban-naar.",
            translation = "Segala puji bagi Allah Tuhan seluruh alam. Ya Allah sampaikanlah pahala ayat-ayat Al-Qur'an, tahlil, tasbih, istighfar, dan shalawat yang telah kami baca sebagai bingkisan hadiah rahmat kepada junjungan kami Nabi Muhammad SAW, serta segenap para anbiya, para syuhada, dan arwah ahli kubur kaum muslimin wal muslimat. Ya Allah ampunilah mereka, rahmatilah mereka, dan jadikanlah kubur mereka taman dari taman-taman surga.",
            count = "1x"
        )
    )

    // Key verses of Surah Yasin (bundled for instant offline access)
    val YASIN_SAMPLE_AYAT: List<Ayat> = listOf(
        Ayat(1, "يس", "Yā Sīn.", "Ya Sin."),
        Ayat(2, "وَالْقُرْآنِ الْحَكِيمِ", "Wal-qur'ānil-ḥakīm(i).", "Demi Al-Qur'an yang penuh hikmah,"),
        Ayat(3, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Innaka laminal-mursalīn(a).", "sungguh engkau (Muhammad) adalah salah seorang rasul,"),
        Ayat(4, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "'Alā ṣirāṭim mustaqīm(in).", "di atas jalan yang lurus,"),
        Ayat(5, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "Tanzīlal-'azīzir-raḥīm(i).", "(sebagai wahyu) yang diturunkan oleh (Allah) Yang Mahaperkasa, Maha Penyayang,"),
        Ayat(6, "لِتُنذِرَ قَوْمًا مَّا أُنذِرَ آبَاؤُهُمْ فَهُمْ غَافِلُونَ", "Litunżira qaumam mā unżira ābā'uhum fahum gāfilūn(a).", "agar engkau memberi peringatan kepada suatu kaum yang nenek moyangnya belum pernah diberi peringatan, karena itu mereka lalai."),
        Ayat(7, "لَقَدْ حَقَّ الْقَوْلُ عَلَىٰ أَكْثَرِهِمْ فَهُمْ لَا يُؤْمِنُونَ", "Laqad ḥaqqal-qaulu 'alā akṡarihim fahum lā yu'minūn(a).", "Sungguh, pasti berlaku perkataan (hukuman) terhadap kebanyakan mereka, karena mereka tidak beriman."),
        Ayat(8, "إِنَّا جَعَلْنَا فِي أَعْنَاقِهِمْ أَغْلَالًا فَهِيَ إِلَى الْأَذْقَانِ فَهُم مُّقْمَحُونَ", "Innā ja'alnā fī a'nāqihim aglālan fahiya ilal-ażqāni fahum muqmaḥūn(a).", "Sungguh, Kami telah memasang belenggu di leher mereka, lalu tangan mereka (diangkat) ke dagu, karena itu mereka tertengadah."),
        Ayat(9, "وَجَعَلْنَا مِن بَيْنِ أَيْدِيهِمْ سَدًّا وَمِنْ خَلْفِهِمْ سَدًّا فَأَغْشَيْنَاهُمْ فَهُمْ لَا يُبْصِرُونَ", "Wa ja'alnā mim baini aidīhim saddaw wa min khalfihim saddan fa agsyaināhum fahum lā yubṣirūn(a).", "Dan Kami jadikan di hadapan mereka sekat dan di belakang mereka sekat, dan Kami tutup (mata) mereka sehingga mereka tidak dapat melihat."),
        Ayat(10, "وَسَوَاءٌ عَلَيْهِمْ أَأَنذَرْتَهُمْ أَمْ لَمْ تُنذِرْهُمْ لَا يُؤْمِنُونَ", "Wa sawā'un 'alaihim a'anżartahum am lam tunżirhum lā yu'minūn(a).", "Dan sama saja bagi mereka, apakah engkau memberi peringatan kepada mereka atau engkau tidak memberi peringatan kepada mereka, mereka tidak akan beriman."),
        Ayat(11, "إِنَّمَا تُنذِرُ مَنِ اتَّبَعَ الذِّكْرَ وَخَشِيَ الرَّحْمَٰنَ بِالْغَيْبِ ۖ فَبَشِّرْهُ بِمَغْفِرَةٍ وَأَجْرٍ كَرِيمٍ", "Innamā tunżiru manittaba'aż-żikra wa khasyiyar-raḥmāna bil-gaib(i), fa basysyirhu bimāgfiratiw wa ajrin karīm(in).", "Sesungguhnya engkau hanya memberi peringatan kepada orang-orang yang mau mengikuti peringatan dan yang takut kepada Tuhan Yang Maha Pengasih walaupun mereka tidak melihat-Nya. Maka berilah mereka kabar gembira dengan ampunan dan pahala yang mulia."),
        Ayat(12, "إِنَّا نَحْنُ نُحْيِي الْمَوْتَىٰ وَنَكْتُبُ مَا قَدَّمُوا وَآثَارَهُمْ ۚ وَكُلَّ شَيْءٍ أَحْصَيْنَاهُ فِي إِمَامٍ مُّبِينٍ", "Innā naḥnu nuḥyil-mautā wa naktubu mā qaddamū wa āṡārahum, wa kulla syai'in aḥṣaināhu fī imāmim mubīn(in).", "Sungguh, Kamilah yang menghidupkan orang-orang yang mati, dan Kamilah yang mencatat apa yang telah mereka kerjakan dan bekas-bekas yang mereka tinggalkan. Dan segala sesuatu Kami kumpulkan dalam Kitab yang nyata (Lauh Mahfuzh).")
    )
}
