package de.turkischlernen.app.data.content

import java.util.Locale

/**
 * Vergleicht, was das Kind gesagt hat, mit dem gesuchten türkischen Wort.
 * Großschreibung, Satzzeichen und türkische Sonderzeichen spielen keine Rolle,
 * und ein Buchstabe daneben ist noch richtig – Kinderaussprache soll nicht
 * an Kleinigkeiten scheitern.
 */
object SpeechMatch {

    /** Ab dieser Ähnlichkeit gilt die Antwort als richtig. */
    const val THRESHOLD = 0.7

    private val TURKISH = Locale("tr", "TR")

    private val SONDERZEICHEN = mapOf(
        'ç' to 'c', 'ğ' to 'g', 'ı' to 'i', 'ö' to 'o', 'ş' to 's', 'ü' to 'u',
        'â' to 'a', 'î' to 'i', 'û' to 'u'
    )

    /** Kleinschreibung, einfache Buchstaben, keine Satzzeichen, einfache Leerzeichen. */
    fun normalize(text: String): String {
        val klein = text.lowercase(TURKISH)
        val einfach = klein.map { SONDERZEICHEN[it] ?: it }.joinToString("")
        return einfach
            .filter { it.isLetterOrDigit() || it == ' ' }
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /** 1.0 = gleich, 0.0 = nichts gemeinsam. */
    fun similarity(spoken: String, target: String): Double {
        val a = normalize(spoken)
        val b = normalize(target)
        if (a.isEmpty() || b.isEmpty()) return 0.0
        if (a == b) return 1.0
        val distanz = levenshtein(a, b)
        return 1.0 - distanz.toDouble() / maxOf(a.length, b.length)
    }

    fun matches(spoken: String, target: String): Boolean = similarity(spoken, target) >= THRESHOLD

    /** Wie viele Buchstaben müssten geändert werden, damit beide gleich sind. */
    private fun levenshtein(a: String, b: String): Int {
        var vorherige = IntArray(b.length + 1) { it }
        var aktuelle = IntArray(b.length + 1)

        for (i in 1..a.length) {
            aktuelle[0] = i
            for (j in 1..b.length) {
                val kosten = if (a[i - 1] == b[j - 1]) 0 else 1
                aktuelle[j] = minOf(
                    aktuelle[j - 1] + 1,
                    vorherige[j] + 1,
                    vorherige[j - 1] + kosten
                )
            }
            val tausch = vorherige
            vorherige = aktuelle
            aktuelle = tausch
        }
        return vorherige[b.length]
    }
}
