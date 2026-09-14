package de.turkischlernen.app.data.content

import de.turkischlernen.app.data.model.LearnUnit
import de.turkischlernen.app.data.model.Lesson
import de.turkischlernen.app.data.model.LessonKind
import de.turkischlernen.app.data.model.Phrase
import de.turkischlernen.app.data.model.Word

/**
 * Der komplette Lernstoff der App – vollständig offline, ohne Netzwerk.
 *
 * Aufbau wie bei den großen Sprachlern-Apps: Einheiten (Units) bestehen aus
 * mehreren kurzen Lektionen und enden mit einer Abschlussprüfung.
 */
object Curriculum {

    // ---------------------------------------------------------------- Wörter

    private val essen = listOf(
        Word("elma", "elma", "Apfel", "🍎", "El-ma"),
        Word("su", "su", "Wasser", "💧", "Ssu", known = true),
        Word("ekmek", "ekmek", "Brot", "🍞", "Ek-mek"),
        Word("muz", "muz", "Banane", "🍌", "Muhs"),
        Word("sut", "süt", "Milch", "🥛", "Sütt"),
        Word("peynir", "peynir", "Käse", "🧀", "Pej-nir"),
        Word("cay", "çay", "Tee", "🍵", "Tschai"),
        Word("yumurta", "yumurta", "Ei", "🥚", "Ju-mur-ta"),
        Word("cikolata", "çikolata", "Schokolade", "🍫", "Tschi-ko-la-ta"),
        Word("dondurma", "dondurma", "Eis", "🍦", "Don-dur-ma"),
        Word("yemek", "yemek", "Essen", "🍽️", "Je-mek", known = true),
        Word("portakal", "portakal", "Orange", "🍊", "Por-ta-kal")
    )

    private val tiere = listOf(
        Word("kopek", "köpek", "Hund", "🐶", "Kö-pek"),
        Word("kedi", "kedi", "Katze", "🐱", "Ke-di"),
        Word("kus", "kuş", "Vogel", "🐦", "Kusch"),
        Word("balik", "balık", "Fisch", "🐟", "Ba-lık"),
        Word("at", "at", "Pferd", "🐴", "At"),
        Word("inek", "inek", "Kuh", "🐄", "I-nek"),
        Word("tavsan", "tavşan", "Hase", "🐰", "Taw-schan"),
        Word("aslan", "aslan", "Löwe", "🦁", "As-lan"),
        Word("kelebek", "kelebek", "Schmetterling", "🦋", "Ke-le-bek"),
        Word("fil", "fil", "Elefant", "🐘", "Fil"),
        Word("ari", "arı", "Biene", "🐝", "A-rı"),
        Word("kaplumbaga", "kaplumbağa", "Schildkröte", "🐢", "Kap-lum-ba-a")
    )

    private val natur = listOf(
        Word("agac", "ağaç", "Baum", "🌳", "A-atsch"),
        Word("cicek", "çiçek", "Blume", "🌸", "Tschi-tschek"),
        Word("gunes", "güneş", "Sonne", "☀️", "Gü-nesch"),
        Word("bahce", "bahçe", "Garten", "🏡", "Bach-tsche", known = true),
        Word("yagmur", "yağmur", "Regen", "🌧️", "Ja-mur"),
        Word("ay", "ay", "Mond", "🌙", "Ai"),
        Word("deniz", "deniz", "Meer", "🌊", "De-nis"),
        Word("kar", "kar", "Schnee", "❄️", "Kar"),
        Word("yildiz", "yıldız", "Stern", "⭐", "Jıl-dıs"),
        Word("tas", "taş", "Stein", "🪨", "Tasch"),
        Word("orman", "orman", "Wald", "🌲", "Or-man"),
        Word("bulut", "bulut", "Wolke", "☁️", "Bu-lut")
    )

    private val zuhause = listOf(
        Word("ev", "ev", "Haus", "🏠", "Ew"),
        Word("kapi", "kapı", "Tür", "🚪", "Ka-pı"),
        Word("masa", "masa", "Tisch", "🍽️", "Ma-ssa"),
        Word("sandalye", "sandalye", "Stuhl", "🪑", "San-dal-je"),
        Word("yatak", "yatak", "Bett", "🛏️", "Ja-tak"),
        Word("pencere", "pencere", "Fenster", "🪟", "Pen-dsche-re"),
        Word("mutfak", "mutfak", "Küche", "🍳", "Mut-fak"),
        Word("banyo", "banyo", "Badezimmer", "🛁", "Ban-jo"),
        Word("lamba", "lamba", "Lampe", "💡", "Lam-ba"),
        Word("anahtar", "anahtar", "Schlüssel", "🔑", "A-nach-tar"),
        Word("kitap", "kitap", "Buch", "📚", "Ki-tap"),
        Word("oyuncak", "oyuncak", "Spielzeug", "🧸", "O-jun-dschak")
    )

    private val farben = listOf(
        Word("kirmizi", "kırmızı", "rot", "🔴", "Kır-mı-sı", colorHex = 0xFFE53935),
        Word("mavi", "mavi", "blau", "🔵", "Ma-wi", colorHex = 0xFF1E88E5),
        Word("sari", "sarı", "gelb", "🟡", "Ssa-rı", colorHex = 0xFFFDD835),
        Word("yesil", "yeşil", "grün", "🟢", "Je-schil", colorHex = 0xFF43A047),
        Word("beyaz", "beyaz", "weiß", "⚪", "Be-jas", colorHex = 0xFFFAFAFA),
        Word("siyah", "siyah", "schwarz", "⚫", "Ssi-jach", colorHex = 0xFF212121),
        Word("turuncu", "turuncu", "orange", "🟠", "Tu-run-dschu", colorHex = 0xFFFB8C00),
        Word("mor", "mor", "lila", "🟣", "Mor", colorHex = 0xFF8E24AA),
        Word("pembe", "pembe", "rosa", "🌸", "Pem-be", colorHex = 0xFFEC407A),
        Word("kahverengi", "kahverengi", "braun", "🟤", "Kach-we-ren-gi", colorHex = 0xFF6D4C41)
    )

    private val zahlen = listOf(
        Word("bir", "bir", "eins", "1️⃣", "Bir"),
        Word("iki", "iki", "zwei", "2️⃣", "I-ki"),
        Word("uc", "üç", "drei", "3️⃣", "Ütsch"),
        Word("dort", "dört", "vier", "4️⃣", "Dört"),
        Word("bes", "beş", "fünf", "5️⃣", "Besch"),
        Word("alti", "altı", "sechs", "6️⃣", "Al-tı"),
        Word("yedi", "yedi", "sieben", "7️⃣", "Je-di"),
        Word("sekiz", "sekiz", "acht", "8️⃣", "Se-kis"),
        Word("dokuz", "dokuz", "neun", "9️⃣", "Do-kus"),
        Word("on", "on", "zehn", "🔟", "On")
    )

    private val familie = listOf(
        Word("anne", "anne", "Mama", "👩", "An-ne"),
        Word("baba", "baba", "Papa", "👨", "Ba-ba"),
        Word("kardes", "kardeş", "Geschwister", "🧒", "Kar-desch"),
        Word("abla", "abla", "große Schwester", "👧", "Ab-la"),
        Word("abi", "abi", "großer Bruder", "👦", "A-bi"),
        Word("dede", "dede", "Opa", "👴", "De-de"),
        Word("nine", "nine", "Oma", "👵", "Ni-ne"),
        Word("bebek", "bebek", "Baby", "👶", "Be-bek"),
        Word("arkadas", "arkadaş", "Freund", "🤝", "Ar-ka-dasch"),
        Word("aile", "aile", "Familie", "👨‍👩‍👧‍👦", "A-i-le")
    )

    private val begruessung = listOf(
        Word("merhaba", "merhaba", "Hallo", "👋", "Mer-ha-ba"),
        Word("gunaydin", "günaydın", "Guten Morgen", "🌅", "Gü-nai-dın"),
        Word("lutfen", "lütfen", "bitte", "🥺", "Lüt-fen"),
        Word("evet", "evet", "ja", "✅", "E-wet"),
        Word("hayir", "hayır", "nein", "❌", "Ha-jır"),
        Word("gorusuruz", "görüşürüz", "Tschüss", "👋", "Gö-rü-schü-rüs"),
        Word("tamam", "tamam", "okay", "👌", "Ta-mam"),
        Word("affedersin", "affedersin", "Entschuldigung", "🙇", "Af-fe-der-ssin")
    )

    private val gefuehle = listOf(
        Word("aciktim", "acıktım", "Ich habe Hunger", "😋", "A-dschık-tım", known = true),
        Word("susadim", "susadım", "Ich habe Durst", "🥤", "Ssu-ssa-dım", known = true),
        Word("yoruldum", "yoruldum", "Ich bin müde", "😴", "Jo-rul-dum"),
        Word("bittim", "bittim", "Ich bin fertig", "🙌", "Bit-tim"),
        Word("anlamiyorum", "anlamıyorum", "Ich verstehe nicht", "🤔", "An-la-mı-jo-rum"),
        Word("usudum", "üşüdüm", "Mir ist kalt", "🥶", "Ü-schü-düm")
    )

    /** Alle Vokabeln der App. */
    val words: List<Word> =
        essen + tiere + natur + zuhause + farben + zahlen + familie + begruessung + gefuehle

    // ----------------------------------------------------------------- Sätze

    private val begruessungSaetze = listOf(
        Phrase("p_iyi_geceler", "iyi geceler", "Gute Nacht", "🌙", "I-ji ge-dsche-ler"),
        Phrase("p_iyi_gunler", "iyi günler", "Guten Tag", "🌞", "I-ji gün-ler"),
        Phrase("p_tesekkur", "teşekkür ederim", "Danke schön", "🙏", "Te-schek-kür e-de-rim"),
        Phrase("p_ben_iyiyim", "ben iyiyim", "Mir geht es gut", "👍", "Ben i-ji-jim")
    )

    private val alltagSaetze = listOf(
        Phrase("p_su_istiyorum", "su istiyorum", "Ich möchte Wasser", "💧", "Ssu is-ti-jo-rum"),
        Phrase(
            "p_tuvalet", "tuvalete gitmem lazım", "Ich muss auf die Toilette", "🚻",
            "Tu-wa-le-te git-mem la-sım"
        ),
        Phrase(
            "p_oynamak", "oynamak istiyorum", "Ich möchte spielen", "🎲",
            "Oi-na-mak is-ti-jo-rum"
        ),
        Phrase("p_yardim", "lütfen yardım et", "Hilf mir bitte", "🆘", "Lüt-fen jar-dım et"),
        Phrase(
            "p_eve_gitmek", "eve gitmek istiyorum", "Ich möchte nach Hause", "🏠",
            "E-we git-mek is-ti-jo-rum"
        ),
        Phrase("p_karnim", "karnım ağrıyor", "Mein Bauch tut weh", "🤒", "Kar-nım a-rı-jor")
    )

    /** Alle Sätze der App. */
    val phrases: List<Phrase> = begruessungSaetze + alltagSaetze

    // --------------------------------------------------------------- Einheiten

    val units: List<LearnUnit> = listOf(
        buildUnit(
            id = "u_essen",
            title = "Essen & Trinken",
            subtitle = "Was schmeckt dir?",
            emoji = "🍎",
            colorHex = 0xFFFF9600,
            words = essen
        ),
        buildUnit(
            id = "u_tiere",
            title = "Tiere",
            subtitle = "Tiere auf Türkisch",
            emoji = "🐶",
            colorHex = 0xFF58CC02,
            words = tiere
        ),
        buildUnit(
            id = "u_farben",
            title = "Farben",
            subtitle = "Bunt wie ein Regenbogen",
            emoji = "🎨",
            colorHex = 0xFFCE82FF,
            words = farben
        ),
        buildUnit(
            id = "u_zahlen",
            title = "Zahlen 1–10",
            subtitle = "Zählen lernen",
            emoji = "🔢",
            colorHex = 0xFF1CB0F6,
            words = zahlen
        ),
        buildUnit(
            id = "u_natur",
            title = "Natur",
            subtitle = "Draußen unterwegs",
            emoji = "🌳",
            colorHex = 0xFF2FB98B,
            words = natur
        ),
        buildUnit(
            id = "u_zuhause",
            title = "Zuhause",
            subtitle = "Alles im Haus",
            emoji = "🏠",
            colorHex = 0xFFFF4B4B,
            words = zuhause
        ),
        buildUnit(
            id = "u_familie",
            title = "Familie",
            subtitle = "Meine Lieblingsmenschen",
            emoji = "👨‍👩‍👧‍👦",
            colorHex = 0xFFFFC800,
            words = familie
        ),
        buildUnit(
            id = "u_begruessung",
            title = "Begrüßen & Danke",
            subtitle = "Höfliche Wörter",
            emoji = "👋",
            colorHex = 0xFF1CB0F6,
            words = begruessung,
            phrases = begruessungSaetze
        ),
        buildUnit(
            id = "u_alltag",
            title = "Ich brauche …",
            subtitle = "Sätze für jeden Tag",
            emoji = "🙋",
            colorHex = 0xFFFF9600,
            words = gefuehle,
            phrases = alltagSaetze
        )
    )

    // ------------------------------------------------------------ Nachschlagen

    private val wordsById: Map<String, Word> = words.associateBy { it.id }
    private val phrasesById: Map<String, Phrase> = phrases.associateBy { it.id }

    val lessons: List<Lesson> = units.flatMap { it.lessons }
    private val lessonsById: Map<String, Lesson> = lessons.associateBy { it.id }
    private val unitByLessonId: Map<String, LearnUnit> =
        units.flatMap { unit -> unit.lessons.map { it.id to unit } }.toMap()
    private val unitIdByItemId: Map<String, String> =
        units.flatMap { unit -> (unit.wordIds + unit.phraseIds).map { it to unit.id } }.toMap()

    fun word(id: String): Word? = wordsById[id]

    /** Wort ODER Satz anhand der ID. */
    fun item(id: String): de.turkischlernen.app.data.model.LearnItem? =
        wordsById[id] ?: phrasesById[id]
    fun phrase(id: String): Phrase? = phrasesById[id]
    fun lesson(id: String): Lesson? = lessonsById[id]
    fun unitOfLesson(lessonId: String): LearnUnit? = unitByLessonId[lessonId]
    fun unit(id: String): LearnUnit? = units.firstOrNull { it.id == id }

    /** Zu welcher Einheit gehört eine Vokabel? (für passende Antwortauswahl) */
    fun unitIdOfItem(itemId: String): String? = unitIdByItemId[itemId]

    /** Gesamtzahl aller lernbaren Einträge (für die Fortschrittsanzeige). */
    val totalItemCount: Int get() = words.size + phrases.size

    // ------------------------------------------------------------------ Bauen

    /**
     * Baut aus einer Wort-/Satzliste eine Einheit: je [wordsPerLesson] Wörter
     * bzw. [phrasesPerLesson] Sätze ergeben eine Lektion, am Ende steht immer
     * eine Abschlussprüfung über die ganze Einheit.
     */
    private fun buildUnit(
        id: String,
        title: String,
        subtitle: String,
        emoji: String,
        colorHex: Long,
        words: List<Word>,
        phrases: List<Phrase> = emptyList(),
        wordsPerLesson: Int = 3,
        phrasesPerLesson: Int = 2
    ): LearnUnit {
        val lessons = mutableListOf<Lesson>()
        var index = 0

        words.chunked(wordsPerLesson).forEach { chunk ->
            index++
            lessons += Lesson(
                id = "${id}_l$index",
                unitId = id,
                index = index,
                title = "Lektion $index",
                wordIds = chunk.map { it.id }
            )
        }
        phrases.chunked(phrasesPerLesson).forEach { chunk ->
            index++
            lessons += Lesson(
                id = "${id}_l$index",
                unitId = id,
                index = index,
                title = "Sätze $index",
                phraseIds = chunk.map { it.id }
            )
        }
        index++
        lessons += Lesson(
            id = "${id}_test",
            unitId = id,
            index = index,
            title = "Prüfung",
            wordIds = words.map { it.id },
            phraseIds = phrases.map { it.id },
            kind = LessonKind.TEST
        )

        return LearnUnit(id, title, subtitle, emoji, colorHex, lessons)
    }
}
