package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.Phrase
import de.turkischlernen.app.data.model.Word

/**
 * Zusätzliche Einheiten: je neun Wörter, zwei Sätze und eine Abschlussprüfung.
 * IDs bleiben stabil, damit gespeicherter Fortschritt erhalten bleibt.
 */
internal object ExpandedCurriculum {

    data class UnitContent(
        val id: String,
        val title: String,
        val subtitle: String,
        val emoji: String,
        val colorHex: Long,
        val words: List<Word>,
        val phrases: List<Phrase>
    )

    val units: List<UnitContent> = listOf(
        UnitContent(
            id = "u_schule",
            title = "Schule",
            subtitle = "Mein Tag in der Schule",
            emoji = "🏫",
            colorHex = 0xFF1CB0F6,
            words = listOf(
                Word("okul", "okul", "Schule", "🏫", "O-kul"),
                Word("ogretmen", "öğretmen", "Lehrkraft", "🧑‍🏫", "Ö-ret-men"),
                Word("ogrenci", "öğrenci", "Schulkind", "🧑‍🎓", "Ö-ren-dschi"),
                Word("kalem", "kalem", "Stift", "✏️", "Ka-lem"),
                Word("defter", "defter", "Heft", "📓", "Def-ter"),
                Word("silgi", "silgi", "Radiergummi", "🧽", "Ssil-gi"),
                Word("canta", "çanta", "Tasche", "🎒", "Tschan-ta"),
                Word("cetvel", "cetvel", "Lineal", "📏", "Dschet-wel"),
                Word("makas", "makas", "Schere", "✂️", "Ma-kas")
            ),
            phrases = listOf(
                Phrase("p_okula_gidiyorum", "okula gidiyorum", "Ich gehe zur Schule", "🏫", "O-ku-la gi-di-jo-rum"),
                Phrase("p_kalem_istiyorum", "kalem istiyorum", "Ich möchte einen Stift", "✏️", "Ka-lem is-ti-jo-rum")
            )
        ),
        UnitContent(
            id = "u_koerper",
            title = "Mein Körper",
            subtitle = "Von Kopf bis Fuß",
            emoji = "✋",
            colorHex = 0xFF58CC02,
            words = listOf(
                Word("bas", "baş", "Kopf", "🧑", "Basch"),
                Word("goz", "göz", "Auge", "👁️", "Gös"),
                Word("kulak", "kulak", "Ohr", "👂", "Ku-lak"),
                Word("burun", "burun", "Nase", "👃", "Bu-run"),
                Word("agiz", "ağız", "Mund", "👄", "A-ıs"),
                Word("dis", "diş", "Zahn", "🦷", "Disch"),
                Word("el", "el", "Hand", "✋", "El"),
                Word("ayak", "ayak", "Fuß", "🦶", "A-jak"),
                Word("bacak", "bacak", "Bein", "🦵", "Ba-dschak")
            ),
            phrases = listOf(
                Phrase("p_ellerimi_yikiyorum", "ellerimi yıkıyorum", "Ich wasche meine Hände", "🧼", "El-le-ri-mi jı-kı-jo-rum"),
                Phrase("p_dislerimi_fircaliyorum", "dişlerimi fırçalıyorum", "Ich putze meine Zähne", "🪥", "Disch-le-ri-mi fır-tscha-lı-jo-rum")
            )
        ),
        UnitContent(
            id = "u_kleidung",
            title = "Kleidung",
            subtitle = "Was ziehe ich an?",
            emoji = "👕",
            colorHex = 0xFFCE82FF,
            words = listOf(
                Word("tisort", "tişört", "T-Shirt", "👕", "Ti-schört"),
                Word("pantolon", "pantolon", "Hose", "👖", "Pan-to-lon"),
                Word("elbise", "elbise", "Kleid", "👗", "El-bi-sse"),
                Word("ayakkabi", "ayakkabı", "Schuh", "👟", "A-jak-ka-bı"),
                Word("corap", "çorap", "Socke", "🧦", "Tscho-rap"),
                Word("sapka", "şapka", "Hut", "👒", "Schap-ka"),
                Word("mont", "mont", "Jacke", "🧥", "Mont"),
                Word("eldiven", "eldiven", "Handschuh", "🧤", "El-di-wen"),
                Word("atki", "atkı", "Schal", "🧣", "At-kı")
            ),
            phrases = listOf(
                Phrase("p_montumu_giyiyorum", "montumu giyiyorum", "Ich ziehe meine Jacke an", "🧥", "Mon-tu-mu gi-ji-jo-rum"),
                Phrase("p_sapkam_nerede", "şapkam nerede", "Wo ist mein Hut?", "👒", "Schap-kam ne-re-de")
            )
        ),
        UnitContent(
            id = "u_obst",
            title = "Noch mehr Obst",
            subtitle = "Süß und bunt",
            emoji = "🍓",
            colorHex = 0xFFFF4B4B,
            words = listOf(
                Word("cilek", "çilek", "Erdbeere", "🍓", "Tschi-lek"),
                Word("armut", "armut", "Birne", "🍐", "Ar-mut"),
                Word("uzum", "üzüm", "Traube", "🍇", "Ü-süm"),
                Word("kiraz", "kiraz", "Kirsche", "🍒", "Ki-ras"),
                Word("karpuz", "karpuz", "Wassermelone", "🍉", "Kar-pus"),
                Word("kavun", "kavun", "Melone", "🍈", "Ka-wun"),
                Word("seftali", "şeftali", "Pfirsich", "🍑", "Schef-ta-li"),
                Word("limon", "limon", "Zitrone", "🍋", "Li-mon"),
                Word("ananas", "ananas", "Ananas", "🍍", "A-na-nas")
            ),
            phrases = listOf(
                Phrase("p_cilek_seviyorum", "çilek seviyorum", "Ich mag Erdbeeren", "🍓", "Tschi-lek sse-wi-jo-rum"),
                Phrase("p_armut_yiyorum", "armut yiyorum", "Ich esse eine Birne", "🍐", "Ar-mut ji-jo-rum")
            )
        ),
        UnitContent(
            id = "u_gemuese",
            title = "Gemüse",
            subtitle = "Was wächst im Garten?",
            emoji = "🥕",
            colorHex = 0xFF58CC02,
            words = listOf(
                Word("domates", "domates", "Tomate", "🍅", "Do-ma-tes"),
                Word("salatalik", "salatalık", "Gurke", "🥒", "Ssa-la-ta-lık"),
                Word("havuc", "havuç", "Karotte", "🥕", "Ha-wutsch"),
                Word("patates", "patates", "Kartoffel", "🥔", "Pa-ta-tes"),
                Word("sogan", "soğan", "Zwiebel", "🧅", "Sso-an"),
                Word("sarimsak", "sarımsak", "Knoblauch", "🧄", "Ssa-rım-ssak"),
                Word("patlican", "patlıcan", "Aubergine", "🍆", "Pat-lı-dschan"),
                Word("brokoli", "brokoli", "Brokkoli", "🥦", "Bro-ko-li"),
                Word("misir", "mısır", "Mais", "🌽", "Mı-ssır")
            ),
            phrases = listOf(
                Phrase("p_havuc_yiyorum", "havuç yiyorum", "Ich esse eine Karotte", "🥕", "Ha-wutsch ji-jo-rum"),
                Phrase("p_domates_kirmizi", "domates kırmızı", "Die Tomate ist rot", "🍅", "Do-ma-tes kır-mı-sı")
            )
        ),
        UnitContent(
            id = "u_verkehr",
            title = "Unterwegs",
            subtitle = "Fahren, fliegen und reisen",
            emoji = "🚌",
            colorHex = 0xFF1CB0F6,
            words = listOf(
                Word("araba", "araba", "Auto", "🚗", "A-ra-ba"),
                Word("otobus", "otobüs", "Bus", "🚌", "O-to-büs"),
                Word("tren", "tren", "Zug", "🚆", "Tren"),
                Word("ucak", "uçak", "Flugzeug", "✈️", "U-tschak"),
                Word("bisiklet", "bisiklet", "Fahrrad", "🚲", "Bi-ssik-let"),
                Word("gemi", "gemi", "Schiff", "🚢", "Ge-mi"),
                Word("taksi", "taksi", "Taxi", "🚕", "Tak-ssi"),
                Word("motosiklet", "motosiklet", "Motorrad", "🏍️", "Mo-to-ssik-let"),
                Word("helikopter", "helikopter", "Hubschrauber", "🚁", "He-li-kop-ter")
            ),
            phrases = listOf(
                Phrase("p_otobus_geliyor", "otobüs geliyor", "Der Bus kommt", "🚌", "O-to-büs ge-li-jor"),
                Phrase("p_bisiklet_suruyorum", "bisiklet sürüyorum", "Ich fahre Fahrrad", "🚲", "Bi-ssik-let ssü-rü-jo-rum")
            )
        ),
        UnitContent(
            id = "u_stadt",
            title = "In der Stadt",
            subtitle = "Kennst du diese Orte?",
            emoji = "🏙️",
            colorHex = 0xFFCE82FF,
            words = listOf(
                Word("park", "park", "Park", "🏞️", "Park"),
                Word("hastane", "hastane", "Krankenhaus", "🏥", "Has-ta-ne"),
                Word("eczane", "eczane", "Apotheke", "💊", "Edsch-sa-ne"),
                Word("kutuphane", "kütüphane", "Bücherei", "📚", "Kü-tüp-ha-ne"),
                Word("market", "market", "Supermarkt", "🛒", "Mar-ket"),
                Word("firin", "fırın", "Bäckerei", "🥖", "Fı-rın"),
                Word("restoran", "restoran", "Restaurant", "🍽️", "Res-to-ran"),
                Word("sinema", "sinema", "Kino", "🎬", "Ssi-ne-ma"),
                Word("muze", "müze", "Museum", "🏛️", "Mü-se")
            ),
            phrases = listOf(
                Phrase("p_park_nerede", "park nerede", "Wo ist der Park?", "🏞️", "Park ne-re-de"),
                Phrase("p_markete_gidiyoruz", "markete gidiyoruz", "Wir gehen zum Supermarkt", "🛒", "Mar-ke-te gi-di-jo-rus")
            )
        ),
        UnitContent(
            id = "u_wetter",
            title = "Wetter & Jahreszeiten",
            subtitle = "Wie ist das Wetter?",
            emoji = "🌦️",
            colorHex = 0xFFFFC800,
            words = listOf(
                Word("hava", "hava", "Wetter", "🌤️", "Ha-wa"),
                Word("ruzgar", "rüzgar", "Wind", "💨", "Rüs-gar"),
                Word("sis", "sis", "Nebel", "🌫️", "Ssis"),
                Word("firtina", "fırtına", "Sturm", "🌪️", "Fır-tı-na"),
                Word("gokkusagi", "gökkuşağı", "Regenbogen", "🌈", "Gök-ku-scha-ı"),
                Word("ilkbahar", "ilkbahar", "Frühling", "🌷", "Ilk-ba-har"),
                Word("yaz", "yaz", "Sommer", "🏖️", "Jas"),
                Word("sonbahar", "sonbahar", "Herbst", "🍂", "Sson-ba-har"),
                Word("kis", "kış", "Winter", "⛄", "Kısch")
            ),
            phrases = listOf(
                Phrase("p_yagmur_yagiyor", "yağmur yağıyor", "Es regnet", "🌧️", "Ja-mur ja-ı-jor"),
                Phrase("p_hava_gunesli", "hava güneşli", "Es ist sonnig", "☀️", "Ha-wa gü-nesch-li")
            )
        ),
        UnitContent(
            id = "u_hobbys",
            title = "Freizeit & Hobbys",
            subtitle = "Was macht dir Spaß?",
            emoji = "⚽",
            colorHex = 0xFF2FB98B,
            words = listOf(
                Word("futbol", "futbol", "Fußball", "⚽", "Fut-bol"),
                Word("basketbol", "basketbol", "Basketball", "🏀", "Bas-ket-bol"),
                Word("tenis", "tenis", "Tennis", "🎾", "Te-nis"),
                Word("yuzme", "yüzme", "Schwimmen", "🏊", "Jüs-me"),
                Word("muzik", "müzik", "Musik", "🎵", "Mü-sik"),
                Word("dans", "dans", "Tanz", "💃", "Dans"),
                Word("satranc", "satranç", "Schach", "♟️", "Ssat-rantsch"),
                Word("gitar", "gitar", "Gitarre", "🎸", "Gi-tar"),
                Word("piyano", "piyano", "Klavier", "🎹", "Pi-ja-no")
            ),
            phrases = listOf(
                Phrase("p_futbol_oynuyorum", "futbol oynuyorum", "Ich spiele Fußball", "⚽", "Fut-bol oi-nu-jo-rum"),
                Phrase("p_muzik_dinliyorum", "müzik dinliyorum", "Ich höre Musik", "🎧", "Mü-sik din-li-jo-rum")
            )
        ),
        UnitContent(
            id = "u_verben",
            title = "Was machst du?",
            subtitle = "Wichtige Tätigkeiten",
            emoji = "🏃",
            colorHex = 0xFFFF9600,
            words = listOf(
                Word("kosmak", "koşmak", "rennen", "🏃", "Kosch-mak"),
                Word("yurumek", "yürümek", "gehen", "🚶", "Jü-rü-mek"),
                Word("atlamak", "atlamak", "springen", "🤸", "At-la-mak"),
                Word("uyumak", "uyumak", "schlafen", "😴", "U-ju-mak"),
                Word("okumak", "okumak", "lesen", "📖", "O-ku-mak"),
                Word("yazmak", "yazmak", "schreiben", "✍️", "Jas-mak"),
                Word("icmek", "içmek", "trinken", "🥤", "Itsch-mek"),
                Word("gulmek", "gülmek", "lachen", "😄", "Gül-mek"),
                Word("konusmak", "konuşmak", "sprechen", "🗣️", "Ko-nusch-mak")
            ),
            phrases = listOf(
                Phrase("p_kitap_okuyorum", "kitap okuyorum", "Ich lese ein Buch", "📖", "Ki-tap o-ku-jo-rum"),
                Phrase("p_su_iciyorum", "su içiyorum", "Ich trinke Wasser", "🥤", "Ssu i-tschi-jo-rum")
            )
        ),
        UnitContent(
            id = "u_eigenschaften",
            title = "Eigenschaften",
            subtitle = "Groß, klein, schnell und langsam",
            emoji = "📏",
            colorHex = 0xFFCE82FF,
            words = listOf(
                Word("buyuk", "büyük", "groß", "🐘", "Bü-jük"),
                Word("kucuk", "küçük", "klein", "🐭", "Kü-tschük"),
                Word("uzun", "uzun", "lang", "📏", "U-sun"),
                Word("kisa", "kısa", "kurz", "📐", "Kı-ssa"),
                Word("sicak", "sıcak", "heiß", "🔥", "Ssı-dschak"),
                Word("soguk", "soğuk", "kalt", "🧊", "Sso-uk"),
                Word("hizli", "hızlı", "schnell", "🐆", "Hıs-lı"),
                Word("yavas", "yavaş", "langsam", "🐌", "Ja-wasch"),
                Word("guzel", "güzel", "schön", "🌺", "Gü-sel")
            ),
            phrases = listOf(
                Phrase("p_fil_buyuk", "fil büyük", "Der Elefant ist groß", "🐘", "Fil bü-jük"),
                Phrase("p_su_soguk", "su soğuk", "Das Wasser ist kalt", "🧊", "Ssu sso-uk")
            )
        ),
        UnitContent(
            id = "u_zeit",
            title = "Zeit & Tagesablauf",
            subtitle = "Gestern, heute und morgen",
            emoji = "🕒",
            colorHex = 0xFF1CB0F6,
            words = listOf(
                Word("sabah", "sabah", "Morgen (Tageszeit)", "🌅", "Ssa-bah"),
                Word("ogle", "öğle", "Mittag", "🌞", "Ö-le"),
                Word("aksam", "akşam", "Abend", "🌇", "Ak-scham"),
                Word("gece", "gece", "Nacht", "🌃", "Ge-dsche"),
                Word("bugun", "bugün", "heute", "📍", "Bu-gün"),
                Word("yarin", "yarın", "morgen (nächster Tag)", "➡️", "Ja-rın"),
                Word("dun", "dün", "gestern", "⬅️", "Dün"),
                Word("hafta", "hafta", "Woche", "📅", "Haf-ta"),
                Word("yil", "yıl", "Jahr", "🗓️", "Jıl")
            ),
            phrases = listOf(
                Phrase("p_sabah_kalkiyorum", "sabah kalkıyorum", "Ich stehe morgens auf", "🌅", "Ssa-bah kal-kı-jo-rum"),
                Phrase("p_gece_uyuyorum", "gece uyuyorum", "Ich schlafe nachts", "🌃", "Ge-dsche u-ju-jo-rum")
            )
        ),
        UnitContent(
            id = "u_zahlen_11_19",
            title = "Zahlen 11–19",
            subtitle = "Wir zählen weiter",
            emoji = "🔢",
            colorHex = 0xFFFFC800,
            words = listOf(
                Word("on_bir", "on bir", "elf", "⑪", "On bir"),
                Word("on_iki", "on iki", "zwölf", "⑫", "On i-ki"),
                Word("on_uc", "on üç", "dreizehn", "⑬", "On ütsch"),
                Word("on_dort", "on dört", "vierzehn", "⑭", "On dört"),
                Word("on_bes", "on beş", "fünfzehn", "⑮", "On besch"),
                Word("on_alti", "on altı", "sechzehn", "⑯", "On al-tı"),
                Word("on_yedi", "on yedi", "siebzehn", "⑰", "On je-di"),
                Word("on_sekiz", "on sekiz", "achtzehn", "⑱", "On sse-kis"),
                Word("on_dokuz", "on dokuz", "neunzehn", "⑲", "On do-kus")
            ),
            phrases = listOf(
                Phrase("p_on_bir_elma", "on bir elma", "Elf Äpfel", "🍎", "On bir el-ma"),
                Phrase("p_on_iki_kalem", "on iki kalem", "Zwölf Stifte", "✏️", "On i-ki ka-lem")
            )
        ),
        UnitContent(
            id = "u_emotionen",
            title = "Meine Gefühle",
            subtitle = "Wie fühlst du dich?",
            emoji = "😊",
            colorHex = 0xFFFF9600,
            words = listOf(
                Word("mutlu", "mutlu", "glücklich", "😊", "Mut-lu"),
                Word("uzgun", "üzgün", "traurig", "😢", "Üs-gün"),
                Word("kizgin", "kızgın", "wütend", "😠", "Kıs-gın"),
                Word("korkmus", "korkmuş", "ängstlich", "😨", "Kork-musch"),
                Word("saskin", "şaşkın", "überrascht", "😲", "Schasch-kın"),
                Word("heyecanli", "heyecanlı", "aufgeregt", "🤩", "He-je-dschan-lı"),
                Word("sakin", "sakin", "ruhig", "😌", "Ssa-kin"),
                Word("hasta", "hasta", "krank", "🤒", "Has-ta"),
                Word("yorgun", "yorgun", "müde", "🥱", "Jor-gun")
            ),
            phrases = listOf(
                Phrase("p_ben_mutluyum", "ben mutluyum", "Ich bin glücklich", "😊", "Ben mut-lu-jum"),
                Phrase("p_bugun_uzgunum", "bugün üzgünüm", "Heute bin ich traurig", "😢", "Bu-gün üs-gü-nüm")
            )
        ),
        UnitContent(
            id = "u_reisen",
            title = "Urlaub & Reisen",
            subtitle = "Auf ins nächste Abenteuer",
            emoji = "🧳",
            colorHex = 0xFF2FB98B,
            words = listOf(
                Word("bilet", "bilet", "Ticket", "🎫", "Bi-let"),
                Word("valiz", "valiz", "Koffer", "🧳", "Wa-lis"),
                Word("pasaport", "pasaport", "Reisepass", "🛂", "Pa-ssa-port"),
                Word("otel", "otel", "Hotel", "🏨", "O-tel"),
                Word("tatil", "tatil", "Urlaub", "🏖️", "Ta-til"),
                Word("yolculuk", "yolculuk", "Reise", "🗺️", "Jol-dschu-luk"),
                Word("harita", "harita", "Landkarte", "🗺️", "Ha-ri-ta"),
                Word("plaj", "plaj", "Strand", "🏝️", "Plasch"),
                Word("havalimani", "havalimanı", "Flughafen", "🛫", "Ha-wa-li-ma-nı")
            ),
            phrases = listOf(
                Phrase("p_tatile_gidiyoruz", "tatile gidiyoruz", "Wir fahren in den Urlaub", "🏖️", "Ta-ti-le gi-di-jo-rus"),
                Phrase("p_valizim_nerede", "valizim nerede", "Wo ist mein Koffer?", "🧳", "Wa-li-sim ne-re-de")
            )
        )
    )

    val words: List<Word> = units.flatMap { it.words }
    val phrases: List<Phrase> = units.flatMap { it.phrases }
}