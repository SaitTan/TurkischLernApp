package de.turkischlernen.app.data.progress

import java.time.LocalDate

/** Lernstand eines Wortes: Stufe und wann es wieder drankommt. */
data class ItemReview(
    val itemId: String,
    val level: Int,
    val dueDate: String
)

/**
 * Schlaues Wiederholen: Wer ein Wort richtig hat, bekommt es später wieder;
 * wer es falsch hat, schon morgen. So wird wiederholt, bevor vergessen wird.
 */
object ReviewLogic {

    /** Abstand in Tagen je Stufe (Stufe 1 bis 5). */
    val INTERVALS = listOf(1, 3, 7, 14, 30)

    val MAX_LEVEL = INTERVALS.size

    fun nextLevel(level: Int, correct: Boolean): Int =
        if (correct) (level + 1).coerceAtMost(MAX_LEVEL) else (level - 1).coerceAtLeast(0)

    /** Wann das Wort nach dieser Stufe wieder fällig ist. */
    fun dueDate(level: Int, today: LocalDate): LocalDate {
        val tage = INTERVALS.getOrNull(level - 1) ?: 1
        return today.plusDays(tage.toLong())
    }

    /** Nach einer Antwort: neuer Lernstand. */
    fun afterAnswer(review: ItemReview?, itemId: String, correct: Boolean, today: LocalDate): ItemReview {
        val stufe = nextLevel(review?.level ?: 0, correct)
        val faellig = if (stufe == 0) today.plusDays(1) else dueDate(stufe, today)
        return ItemReview(itemId, stufe, faellig.toString())
    }

    fun isDue(review: ItemReview, today: LocalDate): Boolean =
        runCatching { LocalDate.parse(review.dueDate) <= today }.getOrDefault(true)

    /** Am längsten überfällig zuerst, dann die niedrigste Stufe. */
    fun sortByUrgency(reviews: Collection<ItemReview>, today: LocalDate): List<ItemReview> =
        reviews.sortedWith(
            compareBy(
                { runCatching { LocalDate.parse(it.dueDate) }.getOrDefault(today) },
                { it.level }
            )
        )

    /** Die heute fälligen Wörter, dringendste zuerst. */
    fun dueItems(reviews: Collection<ItemReview>, today: LocalDate): List<ItemReview> =
        sortByUrgency(reviews.filter { isDue(it, today) }, today)

    // --- Speicherformat: "wortId:stufe:datum" ----------------------------------

    fun encode(reviews: Collection<ItemReview>): Set<String> =
        reviews.map { "${it.itemId}:${it.level}:${it.dueDate}" }.toSet()

    /** Liest das Speicherformat; kaputte Einträge werden übersprungen. */
    fun decode(raw: Set<String>): Map<String, ItemReview> =
        raw.mapNotNull { eintrag ->
            val teile = eintrag.split(":")
            val stufe = teile.getOrNull(1)?.toIntOrNull()
            val datum = teile.getOrNull(2)
            if (teile.size == 3 && teile[0].isNotBlank() && stufe != null && !datum.isNullOrBlank()) {
                teile[0] to ItemReview(teile[0], stufe.coerceIn(0, MAX_LEVEL), datum)
            } else {
                null
            }
        }.toMap()
}
