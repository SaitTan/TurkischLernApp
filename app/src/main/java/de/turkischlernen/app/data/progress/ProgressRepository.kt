package de.turkischlernen.app.data.progress

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lernfortschritt")

/**
 * Speichert den Lernfortschritt lokal (DataStore). Kein Login, keine Cloud.
 */
class ProgressRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    private object Keys {
        val totalXp = intPreferencesKey("total_xp")
        val xpToday = intPreferencesKey("xp_today")
        val dailyGoal = intPreferencesKey("daily_goal")
        val streak = intPreferencesKey("streak")
        val lastActive = stringPreferencesKey("last_active")
        val hearts = intPreferencesKey("hearts")
        val heartsUpdated = longPreferencesKey("hearts_updated")
        val unlimitedHearts = booleanPreferencesKey("unlimited_hearts")
        val completed = stringSetPreferencesKey("completed_lessons")
        val perfect = stringSetPreferencesKey("perfect_lessons")
        val learned = stringSetPreferencesKey("learned_items")
        val mistakes = stringSetPreferencesKey("mistake_items")
        val runs = intPreferencesKey("lesson_runs")
    }

    val progress: Flow<UserProgress> = store.data.map { prefs -> prefs.toProgress() }

    private fun Preferences.toProgress(): UserProgress {
        val raw = UserProgress(
            totalXp = this[Keys.totalXp] ?: 0,
            xpToday = this[Keys.xpToday] ?: 0,
            dailyGoal = this[Keys.dailyGoal] ?: 30,
            streakDays = this[Keys.streak] ?: 0,
            lastActiveDate = this[Keys.lastActive] ?: "",
            hearts = this[Keys.hearts] ?: UserProgress.MAX_HEARTS,
            heartsUpdatedAt = this[Keys.heartsUpdated] ?: 0L,
            unlimitedHearts = this[Keys.unlimitedHearts] ?: true,
            completedLessons = this[Keys.completed] ?: emptySet(),
            perfectLessons = this[Keys.perfect] ?: emptySet(),
            learnedItems = this[Keys.learned] ?: emptySet(),
            mistakeItems = this[Keys.mistakes] ?: emptySet(),
            lessonRuns = this[Keys.runs] ?: 0
        )
        val today = LocalDate.now().toString()
        return raw.copy(
            hearts = ProgressLogic.regeneratedHearts(raw, System.currentTimeMillis()),
            // Tages-XP gehören zum heutigen Tag; an einem neuen Tag starten wir bei 0.
            xpToday = if (raw.lastActiveDate == today) raw.xpToday else 0,
            // Ein ausgelassener Tag beendet die Serie.
            streakDays = when (raw.lastActiveDate) {
                today, LocalDate.now().minusDays(1).toString() -> raw.streakDays
                else -> 0
            }
        )
    }

    /**
     * Wird nach jeder abgeschlossenen Runde aufgerufen.
     * [lessonId] ist null bei einer freien Wiederholungs-Session – diese zählt
     * für XP und Serie, gilt aber nicht als abgeschlossene Lektion.
     */
    suspend fun completeLesson(
        lessonId: String?,
        earnedXp: Int,
        mistakes: Int,
        practicedItemIds: List<String>,
        today: LocalDate = LocalDate.now()
    ) {
        store.edit { prefs ->
            val lastActive = prefs[Keys.lastActive] ?: ""
            val streak = prefs[Keys.streak] ?: 0
            val xpToday = prefs[Keys.xpToday] ?: 0

            prefs[Keys.totalXp] = (prefs[Keys.totalXp] ?: 0) + earnedXp
            prefs[Keys.xpToday] = ProgressLogic.nextXpToday(lastActive, xpToday, earnedXp, today)
            prefs[Keys.streak] = ProgressLogic.nextStreak(lastActive, streak, today)
            prefs[Keys.lastActive] = today.toString()
            prefs[Keys.learned] = (prefs[Keys.learned] ?: emptySet()) + practicedItemIds
            prefs[Keys.runs] = (prefs[Keys.runs] ?: 0) + 1
            if (lessonId != null) {
                prefs[Keys.completed] = (prefs[Keys.completed] ?: emptySet()) + lessonId
                if (mistakes == 0) {
                    prefs[Keys.perfect] = (prefs[Keys.perfect] ?: emptySet()) + lessonId
                }
            }
        }
    }

    /** Merkt sich ein Wort, das falsch beantwortet wurde (für "Wiederholen"). */
    suspend fun addMistake(itemId: String) {
        store.edit { prefs ->
            prefs[Keys.mistakes] = (prefs[Keys.mistakes] ?: emptySet()) + itemId
        }
    }

    /** Wort wurde erfolgreich wiederholt – aus der Fehlerliste entfernen. */
    suspend fun clearMistake(itemId: String) {
        store.edit { prefs ->
            prefs[Keys.mistakes] = (prefs[Keys.mistakes] ?: emptySet()) - itemId
        }
    }

    suspend fun loseHeart() {
        store.edit { prefs ->
            if (prefs[Keys.unlimitedHearts] != false) return@edit
            val current = prefs[Keys.hearts] ?: UserProgress.MAX_HEARTS
            prefs[Keys.hearts] = (current - 1).coerceAtLeast(0)
            prefs[Keys.heartsUpdated] = System.currentTimeMillis()
        }
    }

    suspend fun refillHearts() {
        store.edit { prefs ->
            prefs[Keys.hearts] = UserProgress.MAX_HEARTS
            prefs[Keys.heartsUpdated] = System.currentTimeMillis()
        }
    }

    suspend fun setUnlimitedHearts(enabled: Boolean) {
        store.edit { prefs ->
            prefs[Keys.unlimitedHearts] = enabled
            if (enabled) prefs[Keys.hearts] = UserProgress.MAX_HEARTS
        }
    }

    suspend fun setDailyGoal(goal: Int) {
        store.edit { prefs -> prefs[Keys.dailyGoal] = goal }
    }

    /** Eltern-Bereich: kompletter Neustart. */
    suspend fun resetProgress() {
        store.edit { prefs -> prefs.clear() }
    }
}
