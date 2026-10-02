package de.turkischlernen.app.data.progress

import java.time.DayOfWeek
import java.time.LocalDate

/** Rechnet mit den Tagen, an denen gelernt wurde. */
object StreakCalendar {

    /** So viele Tage Verlauf werden gespeichert. */
    const val MAX_HISTORY_DAYS = 90

    /** Montag bis Sonntag der Woche, in der [date] liegt. */
    fun weekOf(date: LocalDate): List<LocalDate> {
        val montag = date.minusDays((date.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
        return (0L until 7L).map { montag.plusDays(it) }
    }

    fun isActive(date: LocalDate, activeDays: Set<String>): Boolean = date.toString() in activeDays

    /** Längste Kette aufeinanderfolgender Tage. */
    fun longestStreak(activeDays: Set<String>): Int {
        val tage = activeDays.mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }.sorted()
        if (tage.isEmpty()) return 0

        var laengste = 1
        var aktuell = 1
        tage.zipWithNext { vorher, jetzt ->
            aktuell = if (vorher.plusDays(1) == jetzt) aktuell + 1 else 1
            if (aktuell > laengste) laengste = aktuell
        }
        return laengste
    }

    /** Wirft Tage weg, die älter als [MAX_HISTORY_DAYS] sind. */
    fun trim(activeDays: Set<String>, today: LocalDate): Set<String> {
        val grenze = today.minusDays(MAX_HISTORY_DAYS.toLong())
        return activeDays.filter { tag ->
            runCatching { LocalDate.parse(tag) >= grenze }.getOrDefault(false)
        }.toSet()
    }
}
