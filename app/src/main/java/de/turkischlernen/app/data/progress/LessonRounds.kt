package de.turkischlernen.app.data.progress

/** Zustand eines Lektions-Knotens auf dem Lernpfad. */
enum class NodeStage {
    /** Noch gesperrt. */
    LOCKED,

    /** Frei, aber noch keine Runde gespielt. */
    OPEN,

    /** Mindestens eine, aber noch nicht alle Runden geschafft. */
    IN_PROGRESS,

    /** Alle Runden geschafft. */
    DONE,

    /** Nach dem Abschluss noch einmal gespielt – komplett. */
    GOLD
}

/**
 * Eine Lektion besteht aus mehreren Runden. Erst wenn alle geschafft sind, ist sie
 * abgeschlossen und die nächste wird frei; eine weitere Runde macht sie golden.
 */
object LessonRounds {

    const val ROUNDS_PER_LESSON = 3

    /** Spielzeit je gespielter Runde. */
    const val PLAY_MINUTES_PER_ROUND = 1

    fun isDone(rounds: Int): Boolean = rounds >= ROUNDS_PER_LESSON

    fun isGold(rounds: Int): Boolean = rounds > ROUNDS_PER_LESSON

    fun stageOf(rounds: Int, unlocked: Boolean): NodeStage = when {
        !unlocked -> NodeStage.LOCKED
        isGold(rounds) -> NodeStage.GOLD
        isDone(rounds) -> NodeStage.DONE
        rounds > 0 -> NodeStage.IN_PROGRESS
        else -> NodeStage.OPEN
    }

    /** Anteil des Rings, der gefüllt ist (0 bis 1). */
    fun ringFraction(rounds: Int): Float =
        rounds.coerceIn(0, ROUNDS_PER_LESSON) / ROUNDS_PER_LESSON.toFloat()

    /** Beschriftung auf der Start-Karte. */
    fun nextRoundLabel(rounds: Int): String = when {
        isDone(rounds) -> "Alle Runden geschafft"
        else -> "Runde ${rounds + 1} von $ROUNDS_PER_LESSON"
    }

    /** Knopfbeschriftung: die erste Runde heißt "LOS!", spätere "WEITER". */
    fun buttonLabel(rounds: Int): String = when {
        isDone(rounds) -> "WIEDERHOLEN"
        rounds > 0 -> "WEITER"
        else -> "LOS!"
    }

    // --- Speicherformat: "lektionsId:anzahl" ------------------------------------

    fun encode(rounds: Map<String, Int>): Set<String> =
        rounds.filterValues { it > 0 }.map { (id, anzahl) -> "$id:$anzahl" }.toSet()

    /** Liest das Speicherformat; kaputte Einträge werden übersprungen. */
    fun decode(raw: Set<String>): Map<String, Int> =
        raw.mapNotNull { eintrag ->
            val teile = eintrag.split(":")
            val anzahl = teile.getOrNull(1)?.toIntOrNull()
            if (teile.size == 2 && teile[0].isNotBlank() && anzahl != null) teile[0] to anzahl else null
        }.toMap()

    /**
     * Alte Installationen kennen nur abgeschlossene Lektionen. Die zählen als
     * vollständig gespielt, damit niemand von vorne anfangen muss.
     */
    fun withMigratedLessons(rounds: Map<String, Int>, completedLessons: Set<String>): Map<String, Int> {
        val ergaenzt = rounds.toMutableMap()
        completedLessons.forEach { id ->
            if (ergaenzt[id] == null) ergaenzt[id] = ROUNDS_PER_LESSON
        }
        return ergaenzt
    }
}
