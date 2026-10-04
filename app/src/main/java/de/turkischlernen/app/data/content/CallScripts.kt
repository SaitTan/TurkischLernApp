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

    val all: List<CallScript> = listOf(begruessung, essen, familie)

    /** XP für ein beendetes Telefonat. */
    const val XP_REWARD = 10

    /** Spielzeit für ein beendetes Telefonat. */
    const val PLAY_MINUTES_REWARD = 1
}
