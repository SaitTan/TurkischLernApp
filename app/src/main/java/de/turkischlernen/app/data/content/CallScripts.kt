package de.turkischlernen.app.data.content

/** Eine Antwortmöglichkeit im Telefonat. [nextId] null = Gespräch endet danach. */
data class CallOption(
    val tr: String,
    val de: String,
    val nextId: String?
)

/** Was der Gesprächspartner sagt, samt möglicher Antworten. */
data class CallLine(
    val id: String,
    val partnerTr: String,
    val partnerDe: String,
    val options: List<CallOption> = emptyList()
)

/** Ein komplettes Telefonat zu einem Thema. */
data class CallScript(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val startId: String,
    val lines: List<CallLine>
) {
    fun line(id: String): CallLine? = lines.firstOrNull { it.id == id }
}

/**
 * Feste Telefonate mit Verzweigungen – ohne KI, komplett offline.
 * Der Kangal spricht über die Sprachausgabe, das Kind antwortet laut oder per Auswahl.
 */
object CallScripts {

    private val begruessung = CallScript(
        id = "call_merhaba",
        title = "Merhaba!",
        subtitle = "Sich begrüßen und vorstellen",
        emoji = "👋",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Merhaba! Nasılsın?",
                partnerDe = "Hallo! Wie geht es dir?",
                options = listOf(
                    CallOption("İyiyim, teşekkürler", "Mir geht es gut, danke", "name"),
                    CallOption("Çok iyiyim", "Mir geht es sehr gut", "name"),
                    CallOption("Yoruldum", "Ich bin müde", "muede")
                )
            ),
            CallLine(
                id = "muede",
                partnerTr = "Olsun! Adın ne?",
                partnerDe = "Macht nichts! Wie heißt du?",
                options = listOf(
                    CallOption("Adım Ömer", "Ich heiße Ömer", "ende"),
                    CallOption("Benim adım Ömer", "Mein Name ist Ömer", "ende")
                )
            ),
            CallLine(
                id = "name",
                partnerTr = "Güzel! Adın ne?",
                partnerDe = "Schön! Wie heißt du?",
                options = listOf(
                    CallOption("Adım Ömer", "Ich heiße Ömer", "ende"),
                    CallOption("Benim adım Ömer", "Mein Name ist Ömer", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Tanıştığımıza memnun oldum. Görüşürüz!",
                partnerDe = "Schön, dich kennenzulernen. Bis bald!"
            )
        )
    )

    private val essen = CallScript(
        id = "call_lokanta",
        title = "Im Restaurant",
        subtitle = "Etwas zu essen bestellen",
        emoji = "🍽️",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Hoş geldin! Ne istersin?",
                partnerDe = "Willkommen! Was möchtest du?",
                options = listOf(
                    CallOption("Su istiyorum", "Ich möchte Wasser", "mehr"),
                    CallOption("Ekmek istiyorum", "Ich möchte Brot", "mehr"),
                    CallOption("Çay istiyorum", "Ich möchte Tee", "mehr")
                )
            ),
            CallLine(
                id = "mehr",
                partnerTr = "Buyur! Başka bir şey?",
                partnerDe = "Bitte sehr! Noch etwas?",
                options = listOf(
                    CallOption("Elma istiyorum", "Ich möchte einen Apfel", "danke"),
                    CallOption("Hayır, teşekkürler", "Nein, danke", "ende")
                )
            ),
            CallLine(
                id = "danke",
                partnerTr = "Tabii! Afiyet olsun.",
                partnerDe = "Natürlich! Guten Appetit.",
                options = listOf(
                    CallOption("Teşekkür ederim", "Danke schön", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Görüşürüz, iyi günler!",
                partnerDe = "Bis bald, schönen Tag!"
            )
        )
    )

    private val familie = CallScript(
        id = "call_aile",
        title = "Meine Familie",
        subtitle = "Von zu Hause erzählen",
        emoji = "👨‍👩‍👧",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Ailende kim var?",
                partnerDe = "Wer gehört zu deiner Familie?",
                options = listOf(
                    CallOption("Annem ve babam", "Meine Mama und mein Papa", "geschwister"),
                    CallOption("Bir kardeşim var", "Ich habe ein Geschwisterkind", "geschwister")
                )
            ),
            CallLine(
                id = "geschwister",
                partnerTr = "Evinizde hayvan var mı?",
                partnerDe = "Habt ihr ein Tier zu Hause?",
                options = listOf(
                    CallOption("Evet, bir kedi var", "Ja, eine Katze", "ende"),
                    CallOption("Evet, bir köpek var", "Ja, ein Hund", "ende"),
                    CallOption("Hayır, yok", "Nein, haben wir nicht", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Çok güzel! Ailene selam söyle.",
                partnerDe = "Sehr schön! Grüß deine Familie."
            )
        )
    )

    private val schule = CallScript(
        id = "call_okul",
        title = "In der Schule",
        subtitle = "Über den Schultag reden",
        emoji = "🏫",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Bugün okula gittin mi?",
                partnerDe = "Warst du heute in der Schule?",
                options = listOf(
                    CallOption("Evet, okula gittim", "Ja, ich war in der Schule", "lehrer"),
                    CallOption("Hayır, bugün tatil", "Nein, heute ist frei", "frei")
                )
            ),
            CallLine(
                id = "frei",
                partnerTr = "Güzel! Ne yaptın?",
                partnerDe = "Schön! Was hast du gemacht?",
                options = listOf(
                    CallOption("Oyun oynadım", "Ich habe gespielt", "ende"),
                    CallOption("Kitap okudum", "Ich habe ein Buch gelesen", "ende")
                )
            ),
            CallLine(
                id = "lehrer",
                partnerTr = "Öğretmenin nasıl?",
                partnerDe = "Wie ist deine Lehrkraft?",
                options = listOf(
                    CallOption("Öğretmenim çok iyi", "Meine Lehrkraft ist sehr nett", "ende"),
                    CallOption("Öğretmenim çok komik", "Meine Lehrkraft ist sehr lustig", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Aferin! İyi dersler.",
                partnerDe = "Gut gemacht! Viel Erfolg beim Lernen."
            )
        )
    )

    private val einkaufen = CallScript(
        id = "call_market",
        title = "Einkaufen",
        subtitle = "Im Laden etwas kaufen",
        emoji = "🛒",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Markete hoş geldin! Ne alacaksın?",
                partnerDe = "Willkommen im Laden! Was möchtest du kaufen?",
                options = listOf(
                    CallOption("Ekmek alacağım", "Ich kaufe Brot", "menge"),
                    CallOption("Süt alacağım", "Ich kaufe Milch", "menge"),
                    CallOption("Elma alacağım", "Ich kaufe Äpfel", "menge")
                )
            ),
            CallLine(
                id = "menge",
                partnerTr = "Kaç tane istersin?",
                partnerDe = "Wie viele möchtest du?",
                options = listOf(
                    CallOption("Bir tane", "Eines", "bezahlen"),
                    CallOption("İki tane", "Zwei", "bezahlen"),
                    CallOption("Üç tane", "Drei", "bezahlen")
                )
            ),
            CallLine(
                id = "bezahlen",
                partnerTr = "Buyur. Başka bir şey var mı?",
                partnerDe = "Bitte sehr. Noch etwas?",
                options = listOf(
                    CallOption("Hayır, teşekkürler", "Nein, danke", "ende"),
                    CallOption("Evet, su istiyorum", "Ja, ich möchte Wasser", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Teşekkürler, iyi günler!",
                partnerDe = "Danke, schönen Tag!"
            )
        )
    )

    private val geburtstag = CallScript(
        id = "call_dogumgunu",
        title = "Geburtstag",
        subtitle = "Gratulieren und feiern",
        emoji = "🎂",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Bugün benim doğum günüm!",
                partnerDe = "Heute ist mein Geburtstag!",
                options = listOf(
                    CallOption("Doğum günün kutlu olsun!", "Herzlichen Glückwunsch!", "alter"),
                    CallOption("Kaç yaşındasın?", "Wie alt bist du?", "alter")
                )
            ),
            CallLine(
                id = "alter",
                partnerTr = "Teşekkürler! Sekiz yaşındayım. Sen kaç yaşındasın?",
                partnerDe = "Danke! Ich bin acht. Wie alt bist du?",
                options = listOf(
                    CallOption("Ben de sekiz yaşındayım", "Ich bin auch acht", "kuchen"),
                    CallOption("Dokuz yaşındayım", "Ich bin neun", "kuchen")
                )
            ),
            CallLine(
                id = "kuchen",
                partnerTr = "Pasta ister misin?",
                partnerDe = "Möchtest du Kuchen?",
                options = listOf(
                    CallOption("Evet, lütfen", "Ja, bitte", "ende"),
                    CallOption("Hayır, teşekkürler", "Nein, danke", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Çok güzel! Hadi oynayalım.",
                partnerDe = "Sehr schön! Lass uns spielen."
            )
        )
    )

    private val arzt = CallScript(
        id = "call_doktor",
        title = "Beim Arzt",
        subtitle = "Sagen, was weh tut",
        emoji = "🩺",
        startId = "start",
        lines = listOf(
            CallLine(
                id = "start",
                partnerTr = "Merhaba! Neyin var?",
                partnerDe = "Hallo! Was hast du?",
                options = listOf(
                    CallOption("Karnım ağrıyor", "Mein Bauch tut weh", "wasser"),
                    CallOption("Başım ağrıyor", "Mein Kopf tut weh", "wasser"),
                    CallOption("İyiyim, teşekkürler", "Mir geht es gut, danke", "ende")
                )
            ),
            CallLine(
                id = "wasser",
                partnerTr = "Geçmiş olsun! Su içtin mi?",
                partnerDe = "Gute Besserung! Hast du Wasser getrunken?",
                options = listOf(
                    CallOption("Evet, içtim", "Ja, habe ich", "ruhe"),
                    CallOption("Hayır, içmedim", "Nein, habe ich nicht", "ruhe")
                )
            ),
            CallLine(
                id = "ruhe",
                partnerTr = "Biraz dinlen, tamam mı?",
                partnerDe = "Ruh dich ein bisschen aus, okay?",
                options = listOf(
                    CallOption("Tamam, teşekkür ederim", "Okay, danke schön", "ende")
                )
            ),
            CallLine(
                id = "ende",
                partnerTr = "Geçmiş olsun! Görüşürüz.",
                partnerDe = "Gute Besserung! Bis bald."
            )
        )
    )

    val all: List<CallScript> =
        listOf(begruessung, essen, familie, schule, einkaufen, geburtstag, arzt)

    /** XP für ein beendetes Telefonat. */
    const val XP_REWARD = 10

    /** Spielzeit für ein beendetes Telefonat. */
    const val PLAY_MINUTES_REWARD = 1
}
