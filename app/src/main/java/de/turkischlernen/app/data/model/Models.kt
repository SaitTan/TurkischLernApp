package de.turkischlernen.app.data.model

/**
 * Gemeinsame Basis von [Word] und [Phrase] – alles, was gelernt, vorgelesen
 * und abgefragt werden kann.
 */
interface LearnItem {
    val id: String
    val tr: String
    val de: String
    val emoji: String
    val hint: String
}

/**
 * Ein einzelnes Vokabel-Item (Wort oder kurzer Ausdruck).
 *
 * @param id        eindeutige, stabile ID (wird für den Fortschritt gespeichert)
 * @param tr        türkisches Wort
 * @param de        deutsche Bedeutung
 * @param emoji     Illustration (Emoji, damit die App komplett offline und ohne
 *                  Bild-Assets auskommt)
 * @param hint      Aussprachehilfe für deutschsprachige Kinder
 * @param colorHex  optionale Farbe – wird in der Kategorie "Farben" statt des
 *                  Emojis als großer Farbkreis gezeichnet
 * @param known     Wörter, die das Kind bereits kennt (werden mit ⭐ markiert)
 */
data class Word(
    override val id: String,
    override val tr: String,
    override val de: String,
    override val emoji: String,
    override val hint: String,
    val colorHex: Long? = null,
    val known: Boolean = false
) : LearnItem

/**
 * Ein ganzer Satz. Sätze werden zusätzlich als "Satz bauen"-Aufgabe
 * (Wortkacheln) trainiert.
 */
data class Phrase(
    override val id: String,
    override val tr: String,
    override val de: String,
    override val emoji: String,
    override val hint: String,
    val known: Boolean = false
) : LearnItem {
    /** Einzelne Wörter des Satzes – Grundlage für die Wortkacheln. */
    val tokens: List<String> get() = tr.split(" ").filter { it.isNotBlank() }
}

/** Karte für den Bereich "Ich brauche" (Kommunikationstafel). */
data class Situation(
    val id: String,
    val de: String,
    val tr: String,
    val emoji: String,
    val hint: String
)

enum class LessonKind {
    /** Normale Lektion. */
    NORMAL,

    /** Abschlussprüfung einer Einheit (alle Wörter der Einheit, mehr XP). */
    TEST
}

data class Lesson(
    val id: String,
    val unitId: String,
    val index: Int,
    val title: String,
    val wordIds: List<String> = emptyList(),
    val phraseIds: List<String> = emptyList(),
    val kind: LessonKind = LessonKind.NORMAL
) {
    val itemIds: List<String> get() = wordIds + phraseIds
    val xpReward: Int get() = if (kind == LessonKind.TEST) 30 else 15
}

data class LearnUnit(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val colorHex: Long,
    val lessons: List<Lesson>
) {
    val wordIds: List<String> get() = lessons.flatMap { it.wordIds }.distinct()
    val phraseIds: List<String> get() = lessons.flatMap { it.phraseIds }.distinct()
}
