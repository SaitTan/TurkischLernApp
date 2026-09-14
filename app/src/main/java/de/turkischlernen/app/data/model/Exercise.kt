package de.turkischlernen.app.data.model

/** Aufgabentypen einer Lektion. */
sealed interface Exercise {

    /** Die geübten Vokabeln – für Fortschritt und Fehlerliste. */
    val itemIds: List<String>

    /** Deutsche Anweisung über der Aufgabe. */
    val prompt: String

    /**
     * Bild zeigen, türkisches Wort auswählen.
     * Tippt das Kind auf das Bild, wird das Wort vorgelesen.
     */
    data class PictureChoice(
        val target: LearnItem,
        val options: List<LearnItem>
    ) : Exercise {
        override val itemIds get() = listOf(target.id)
        override val prompt get() = "Wie heißt das auf Türkisch?"
    }

    /** Türkisches Wort (mit Ton) zeigen, deutsche Bedeutung auswählen. */
    data class TranslateToGerman(
        val target: LearnItem,
        val options: List<LearnItem>
    ) : Exercise {
        override val itemIds get() = listOf(target.id)
        override val prompt get() = "Was bedeutet das?"
    }

    /** Nur Ton: das gehörte Wort antippen. */
    data class Listening(
        val target: LearnItem,
        val options: List<LearnItem>
    ) : Exercise {
        override val itemIds get() = listOf(target.id)
        override val prompt get() = "Hör gut zu und tippe an"
    }

    /** Satz aus Wortkacheln zusammensetzen. */
    data class WordBank(
        val target: Phrase,
        val tiles: List<String>
    ) : Exercise {
        override val itemIds get() = listOf(target.id)
        override val prompt get() = "Baue den Satz"
        val solution: List<String> get() = target.tokens
    }

    /** Paare finden: türkisch ↔ deutsch. */
    data class MatchPairs(
        val items: List<LearnItem>
    ) : Exercise {
        override val itemIds get() = items.map { it.id }
        override val prompt get() = "Finde die Paare"
    }
}
