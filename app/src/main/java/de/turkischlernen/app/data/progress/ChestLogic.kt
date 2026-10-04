package de.turkischlernen.app.data.progress

import de.turkischlernen.app.data.settings.AvatarOptions
import java.time.LocalDate
import kotlin.random.Random

/** Was in einer Truhe liegt. */
sealed interface ChestReward {
    data class Xp(val amount: Int) : ChestReward
    data class Item(val id: String, val label: String) : ChestReward
}

/** Wo Truhen stehen und was sie enthalten. */
object ChestLogic {

    const val CHEST_EVERY = 3
    const val MIN_XP = 5
    const val MAX_XP = 20

    /** Nach jeder dritten Lektion steht eine Truhe. */
    fun chestAfter(lessonNumber: Int): Boolean =
        lessonNumber > 0 && lessonNumber % CHEST_EVERY == 0

    fun chestId(lessonId: String): String = "chest_$lessonId"

    fun dailyChestId(date: LocalDate): String = "chest_tag_$date"

    /**
     * Würfelt den Inhalt: etwa jedes dritte Mal ein noch gesperrtes Teil, sonst XP.
     * Sind alle Teile freigeschaltet, gibt es immer XP.
     */
    fun roll(random: Random, unlocked: Set<String>): ChestReward {
        val offen = AvatarOptions.lockedItemIds.filterNot { it in unlocked }
        if (offen.isNotEmpty() && random.nextInt(3) == 0) {
            val id = offen[random.nextInt(offen.size)]
            return ChestReward.Item(id, AvatarOptions.itemLabel(id))
        }
        return ChestReward.Xp(MIN_XP + random.nextInt(MAX_XP - MIN_XP + 1))
    }
}
