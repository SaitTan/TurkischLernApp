package de.turkischlernen.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "einstellungen")

/** Welche Art von Ton gerade gespielt werden soll. */
enum class SoundKind { TAP, ANSWER, VOICE, COMBO, XP, REWARD }

/** Die einzelnen Ton-Schalter aus dem Eltern-Bereich. */
data class SoundSettings(
    val tap: Boolean = true,
    val answer: Boolean = true,
    val voice: Boolean = true,
    val combo: Boolean = true,
    val xp: Boolean = true,
    val reward: Boolean = true
) {
    fun isOn(kind: SoundKind): Boolean = when (kind) {
        SoundKind.TAP -> tap
        SoundKind.ANSWER -> answer
        SoundKind.VOICE -> voice
        SoundKind.COMBO -> combo
        SoundKind.XP -> xp
        SoundKind.REWARD -> reward
    }
}

data class AppSettings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    /** Aussehen und Name des Maskottchens. */
    val avatar: AvatarConfig = AvatarConfig(),
    /** Einzelne Ton-Schalter; greifen nur, wenn [soundEnabled] an ist. */
    val sounds: SoundSettings = SoundSettings(),
    /** Aus: Sprech-Aufgaben laufen als reines Nachsprechen ohne Mikrofon. */
    val speechRecognition: Boolean = true
)

/**
 * Geräte-Einstellungen aus dem Eltern-Bereich. Bewusst getrennt vom
 * Lernfortschritt, damit "Fortschritt zurücksetzen" sie nicht löscht.
 */
class SettingsRepository(context: Context) {

    private val store = context.applicationContext.settingsStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val sound = booleanPreferencesKey("sound_enabled")
        val haptics = booleanPreferencesKey("haptics_enabled")
        val avatarFur = stringPreferencesKey("avatar_fur")
        val avatarAccessory = stringPreferencesKey("avatar_accessory")
        val avatarName = stringPreferencesKey("avatar_name")
        val avatarSkin = stringPreferencesKey("avatar_skin")
        val avatarHair = stringPreferencesKey("avatar_hair")
        val avatarHairColor = stringPreferencesKey("avatar_hair_color")
        val avatarShirt = stringPreferencesKey("avatar_shirt")
        val avatarGlasses = booleanPreferencesKey("avatar_glasses")
        val soundTap = booleanPreferencesKey("sound_tap")
        val soundAnswer = booleanPreferencesKey("sound_answer")
        val soundVoice = booleanPreferencesKey("sound_voice")
        val soundCombo = booleanPreferencesKey("sound_combo")
        val soundXp = booleanPreferencesKey("sound_xp")
        val soundReward = booleanPreferencesKey("sound_reward")
        val speechRecognition = booleanPreferencesKey("speech_recognition")
    }

    /** Synchron lesbar, damit Sounds und Vibration ohne Verzögerung prüfen können. */
    val current: StateFlow<AppSettings> = store.data
        .map { prefs ->
            AppSettings(
                soundEnabled = prefs[Keys.sound] ?: true,
                hapticsEnabled = prefs[Keys.haptics] ?: true,
                avatar = AvatarConfig(
                    furId = prefs[Keys.avatarFur] ?: AvatarOptions.DEFAULT_FUR,
                    accessoryId = prefs[Keys.avatarAccessory] ?: AvatarOptions.DEFAULT_ACCESSORY,
                    name = prefs[Keys.avatarName] ?: AvatarOptions.DEFAULT_NAME,
                    skinId = prefs[Keys.avatarSkin] ?: AvatarOptions.DEFAULT_SKIN,
                    hairId = prefs[Keys.avatarHair] ?: AvatarOptions.DEFAULT_HAIR,
                    hairColorId = prefs[Keys.avatarHairColor] ?: AvatarOptions.DEFAULT_HAIR_COLOR,
                    shirtId = prefs[Keys.avatarShirt] ?: AvatarOptions.DEFAULT_SHIRT,
                    glasses = prefs[Keys.avatarGlasses] ?: false
                ),
                sounds = SoundSettings(
                    tap = prefs[Keys.soundTap] ?: true,
                    answer = prefs[Keys.soundAnswer] ?: true,
                    voice = prefs[Keys.soundVoice] ?: true,
                    combo = prefs[Keys.soundCombo] ?: true,
                    xp = prefs[Keys.soundXp] ?: true,
                    reward = prefs[Keys.soundReward] ?: true
                ),
                speechRecognition = prefs[Keys.speechRecognition] ?: true
            )
        }
        .catch { emit(AppSettings()) }
        .stateIn(scope, SharingStarted.Eagerly, AppSettings())

    suspend fun setSoundEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.sound] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.haptics] = enabled }
    }

    suspend fun setSpeechRecognition(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.speechRecognition] = enabled }
    }

    /** Einzelnen Ton-Schalter umlegen. */
    suspend fun setSound(kind: SoundKind, enabled: Boolean) {
        val key = when (kind) {
            SoundKind.TAP -> Keys.soundTap
            SoundKind.ANSWER -> Keys.soundAnswer
            SoundKind.VOICE -> Keys.soundVoice
            SoundKind.COMBO -> Keys.soundCombo
            SoundKind.XP -> Keys.soundXp
            SoundKind.REWARD -> Keys.soundReward
        }
        store.edit { prefs -> prefs[key] = enabled }
    }

    suspend fun setAvatarFur(id: String) {
        store.edit { prefs -> prefs[Keys.avatarFur] = id }
    }

    suspend fun setAvatarAccessory(id: String) {
        store.edit { prefs -> prefs[Keys.avatarAccessory] = id }
    }

    /** Angezeigter Name – zu lange Eingaben werden gekürzt. */
    suspend fun setAvatarName(name: String) {
        store.edit { prefs -> prefs[Keys.avatarName] = AvatarOptions.cleanName(name) }
    }

    suspend fun setAvatarSkin(id: String) {
        store.edit { prefs -> prefs[Keys.avatarSkin] = id }
    }

    suspend fun setAvatarHair(id: String) {
        store.edit { prefs -> prefs[Keys.avatarHair] = id }
    }

    suspend fun setAvatarHairColor(id: String) {
        store.edit { prefs -> prefs[Keys.avatarHairColor] = id }
    }

    suspend fun setAvatarShirt(id: String) {
        store.edit { prefs -> prefs[Keys.avatarShirt] = id }
    }

    suspend fun setAvatarGlasses(enabled: Boolean) {
        store.edit { prefs -> prefs[Keys.avatarGlasses] = enabled }
    }
}
