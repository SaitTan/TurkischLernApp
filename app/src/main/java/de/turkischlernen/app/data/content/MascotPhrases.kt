package de.turkischlernen.app.data.content

import kotlin.random.Random

/** Ein Zuruf des Maskottchens: türkisch mit deutscher Übersetzung. */
data class MascotPhrase(val tr: String, val de: String)

/** Was der Kangal sagt – nach Anlass sortiert. */
object MascotPhrases {

    /** Nach einer richtigen Antwort. */
    val success = listOf(
        MascotPhrase("Harika!", "Toll!"),
        MascotPhrase("Aferin!", "Gut gemacht!"),
        MascotPhrase("Çok iyi!", "Sehr gut!"),
        MascotPhrase("Süper!", "Super!"),
        MascotPhrase("Devam!", "Weiter so!")
    )

    /** Nach einer falschen Antwort – nie tadelnd. */
    val comfort = listOf(
        MascotPhrase("Olsun!", "Macht nichts!"),
        MascotPhrase("Tekrar dene!", "Versuch es nochmal!"),
        MascotPhrase("Önemli değil!", "Nicht schlimm!")
    )

    /** Am Ende einer Lektion. */
    val cheer = listOf(
        MascotPhrase("Bravo!", "Bravo!"),
        MascotPhrase("Tebrikler!", "Glückwunsch!"),
        MascotPhrase("Çok güzel!", "Sehr schön!")
    )

    /** Zufälliger Ruf aus [from], nie derselbe wie [previous]. */
    fun pick(from: List<MascotPhrase>, previous: MascotPhrase?, random: Random): MascotPhrase {
        val candidates = from.filter { it != previous }.ifEmpty { from }
        return candidates[random.nextInt(candidates.size)]
    }
}
