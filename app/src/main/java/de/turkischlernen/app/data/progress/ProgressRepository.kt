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
        val practiceXpToday = intPreferencesKey("practice_xp_today")
        val lessonsToday = intPreferencesKey("lessons_today")
        val perfectToday = intPreferencesKey("perfect_today")
        val wordsToday = intPreferencesKey("words_today")
        val correctToday = intPreferencesKey("correct_today")
        val playMinutes = intPreferencesKey("play_minutes")
        val activeDays = stringSetPreferencesKey("active_days")
        val longestStreak = intPreferencesKey("longest_streak")
        val openedChests = stringSetPreferencesKey("opened_chests")
        val unlockedItems = stringSetPreferencesKey("unlocked_items")
        val dailyGoal = intPreferencesKey("daily_goal")
        val streak = intPreferencesKey("streak")
        val lastActive = stringPreferencesKey("last_active")
        val hearts = intPreferencesKey("hearts")
        val heartsUpdated = longPreferencesKey("hearts_updated")
        val unlimitedHearts = booleanPreferencesKey("unlimited_hearts")
        val completed = stringSetPreferencesKey("completed_lessons")
        val lessonRounds = stringSetPreferencesKey("lesson_rounds")
        val itemReviews = stringSetPreferencesKey("item_reviews")
        val reviewedToday = intPreferencesKey("reviewed_today")
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
            practiceXpToday = this[Keys.practiceXpToday] ?: 0,
            lessonsToday = this[Keys.lessonsToday] ?: 0,
            perfectToday = this[Keys.perfectToday] ?: 0,
            wordsToday = this[Keys.wordsToday] ?: 0,
            correctToday = this[Keys.correctToday] ?: 0,
            reviews = ReviewLogic.decode(this[Keys.itemReviews] ?: emptySet()),
            reviewedToday = this[Keys.reviewedToday] ?: 0,
            playMinutes = this[Keys.playMinutes] ?: 0,
            activeDays = this[Keys.activeDays] ?: emptySet(),
            longestStreak = this[Keys.longestStreak] ?: 0,
            openedChests = this[Keys.openedChests] ?: emptySet(),
            unlockedItems = this[Keys.unlockedItems] ?: emptySet(),
            dailyGoal = this[Keys.dailyGoal] ?: 30,
            streakDays = this[Keys.streak] ?: 0,
            lastActiveDate = this[Keys.lastActive] ?: "",
            hearts = this[Keys.hearts] ?: UserProgress.MAX_HEARTS,
            heartsUpdatedAt = this[Keys.heartsUpdated] ?: 0L,
            unlimitedHearts = this[Keys.unlimitedHearts] ?: true,
            completedLessons = this[Keys.completed] ?: emptySet(),
            // Alte Installationen kennen nur abgeschlossene Lektionen – die zählen als voll.
            lessonRounds = LessonRounds.withMigratedLessons(
                rounds = LessonRounds.decode(this[Keys.lessonRounds] ?: emptySet()),
                completedLessons = this[Keys.completed] ?: emptySet()
            ),
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
            practiceXpToday = if (raw.lastActiveDate == today) raw.practiceXpToday else 0,
            lessonsToday = if (raw.lastActiveDate == today) raw.lessonsToday else 0,
            perfectToday = if (raw.lastActiveDate == today) raw.perfectToday else 0,
            wordsToday = if (raw.lastActiveDate == today) raw.wordsToday else 0,
            correctToday = if (raw.lastActiveDate == today) raw.correctToday else 0,
            reviewedToday = if (raw.lastActiveDate == today) raw.reviewedToday else 0,
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
        correctAnswers: Int = 0,
        today: LocalDate = LocalDate.now()
    ) {
        store.edit { prefs ->
            val lastActive = prefs[Keys.lastActive] ?: ""
            val streak = prefs[Keys.streak] ?: 0
            val xpToday = prefs[Keys.xpToday] ?: 0

            prefs[Keys.totalXp] = (prefs[Keys.totalXp] ?: 0) + earnedXp
            prefs[Keys.xpToday] = ProgressLogic.nextXpToday(lastActive, xpToday, earnedXp, today)
            val neueSerie = ProgressLogic.nextStreak(lastActive, streak, today)
            prefs[Keys.streak] = neueSerie
            prefs[Keys.lastActive] = today.toString()

            // Lern-Tage für den Serien-Kalender mitschreiben (auf 90 Tage begrenzt).
            val tage = StreakCalendar.trim(
                (prefs[Keys.activeDays] ?: emptySet()) + today.toString(),
                today
            )
            prefs[Keys.activeDays] = tage
            prefs[Keys.longestStreak] = maxOf(
                prefs[Keys.longestStreak] ?: 0,
                neueSerie,
                StreakCalendar.longestStreak(tage)
            )
            prefs[Keys.learned] = (prefs[Keys.learned] ?: emptySet()) + practicedItemIds
            prefs[Keys.runs] = (prefs[Keys.runs] ?: 0) + 1

            // Tageszähler für die Tagesaufgaben; an einem neuen Tag beginnen sie bei 0.
            val heute = lastActive == today.toString()
            fun zaehler(key: androidx.datastore.preferences.core.Preferences.Key<Int>): Int =
                if (heute) prefs[key] ?: 0 else 0

            prefs[Keys.lessonsToday] = zaehler(Keys.lessonsToday) + 1
            prefs[Keys.correctToday] = zaehler(Keys.correctToday) + correctAnswers
            prefs[Keys.wordsToday] = zaehler(Keys.wordsToday) + practicedItemIds.size
            if (mistakes == 0) {
                prefs[Keys.perfectToday] = zaehler(Keys.perfectToday) + 1
            }
            if (lessonId == null) {
                // Tageskontingent für freies Wiederholen mitzählen.
                val practiceToday =
                    if (lastActive == today.toString()) prefs[Keys.practiceXpToday] ?: 0 else 0
                prefs[Keys.practiceXpToday] = practiceToday + earnedXp
            }
            if (lessonId != null) {
                // Jede gespielte Runde bringt Spielzeit.
                prefs[Keys.playMinutes] =
                    (prefs[Keys.playMinutes] ?: 0) + LessonRounds.PLAY_MINUTES_PER_ROUND

                // Runde mitzählen; ab drei Runden gilt die Lektion als abgeschlossen.
                val runden = LessonRounds.withMigratedLessons(
                    rounds = LessonRounds.decode(prefs[Keys.lessonRounds] ?: emptySet()),
                    completedLessons = prefs[Keys.completed] ?: emptySet()
                ).toMutableMap()
                val neu = (runden[lessonId] ?: 0) + 1
                runden[lessonId] = neu
                prefs[Keys.lessonRounds] = LessonRounds.encode(runden)

                if (LessonRounds.isDone(neu)) {
                    prefs[Keys.completed] = (prefs[Keys.completed] ?: emptySet()) + lessonId
                }
                if (mistakes == 0) {
                    prefs[Keys.perfect] = (prefs[Keys.perfect] ?: emptySet()) + lessonId
                }
            }
        }
    }

    /**
     * Lernstand der beantworteten Wörter fortschreiben: richtig verlängert den
     * Abstand, falsch holt das Wort schon morgen zurück.
     */
    suspend fun recordAnswers(
        itemIds: List<String>,
        correct: Boolean,
        today: LocalDate = LocalDate.now()
    ) {
        if (itemIds.isEmpty()) return
        store.edit { prefs ->
            val stand = ReviewLogic.decode(prefs[Keys.itemReviews] ?: emptySet()).toMutableMap()

            // Nur fällige Wörter zählen als "aufgefrischt".
            val faellig = itemIds.count { id ->
                stand[id]?.let { ReviewLogic.isDue(it, today) } ?: false
            }

            itemIds.forEach { id ->
                stand[id] = ReviewLogic.afterAnswer(stand[id], id, correct, today)
            }
            prefs[Keys.itemReviews] = ReviewLogic.encode(stand.values)

            val heute = (prefs[Keys.lastActive] ?: "") == today.toString()
            val bisher = if (heute) prefs[Keys.reviewedToday] ?: 0 else 0
            prefs[Keys.reviewedToday] = bisher + faellig
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

    /** Zusätzliche XP, z. B. für ein beendetes Telefonat. */
    suspend fun addBonusXp(xp: Int) {
        store.edit { prefs ->
            prefs[Keys.totalXp] = (prefs[Keys.totalXp] ?: 0) + xp
            prefs[Keys.xpToday] = (prefs[Keys.xpToday] ?: 0) + xp
        }
    }

    /** Eltern-Bereich: Spielzeit direkt setzen. */
    suspend fun setPlayMinutes(minutes: Int) {
        store.edit { prefs -> prefs[Keys.playMinutes] = minutes.coerceIn(0, 999) }
    }

    /** Zusätzliche Spielzeit, z. B. beim Level-Aufstieg. */
    suspend fun addPlayMinutes(minutes: Int) {
        store.edit { prefs ->
            prefs[Keys.playMinutes] = (prefs[Keys.playMinutes] ?: 0) + minutes
        }
    }

    /** Eltern-Bereich: eingelöste Spielzeit abziehen. */
    suspend fun usePlayMinutes(minutes: Int) {
        store.edit { prefs ->
            val rest = (prefs[Keys.playMinutes] ?: 0) - minutes
            prefs[Keys.playMinutes] = rest.coerceAtLeast(0)
        }
    }

    /**
     * Truhe öffnen: XP gutschreiben bzw. Teil freischalten. Eine bereits geöffnete
     * Truhe bleibt zu, damit es keine doppelten Belohnungen gibt.
     */
    suspend fun openChest(chestId: String, reward: ChestReward) {
        store.edit { prefs ->
            val offen = prefs[Keys.openedChests] ?: emptySet()
            if (chestId in offen) return@edit
            prefs[Keys.openedChests] = offen + chestId

            when (reward) {
                is ChestReward.Xp -> {
                    prefs[Keys.totalXp] = (prefs[Keys.totalXp] ?: 0) + reward.amount
                    prefs[Keys.xpToday] = (prefs[Keys.xpToday] ?: 0) + reward.amount
                }

                is ChestReward.Item ->
                    prefs[Keys.unlockedItems] = (prefs[Keys.unlockedItems] ?: emptySet()) + reward.id
            }
        }
    }

    /** Eltern-Bereich: kompletter Neustart. */
    suspend fun resetProgress() {
        store.edit { prefs -> prefs.clear() }
    }
}
